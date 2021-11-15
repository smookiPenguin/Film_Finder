package com.example.filmfinder

import android.app.Dialog
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filmfinder.externData.ExternDatabase
import com.example.filmfinder.localData.LocalDatabase
import com.example.filmfinder.view.Film
import com.example.filmfinder.view.FilmAdapter
import com.squareup.picasso.Picasso
import android.widget.CompoundButton
import com.example.filmfinder.view.Platform
import com.example.filmfinder.view.PlatformAdpater
import java.lang.Math.sqrt
import java.util.*


class MainActivity : AppCompatActivity(){

    private lateinit var spinner:Spinner
    private lateinit var adapter: ArrayAdapter<String>

    private lateinit var localDatabase:LocalDatabase
    private var switchUnplayed = false

    private lateinit var genre:String


    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        localDatabase = LocalDatabase(this, this)

        initSpinner()
        initSwitch()
        initShakeListener()
        //initPlatforms()
    }

//================= Controler =========================================

    //reload display film when user change film's genre
    private fun initSpinner(){
        val genres = arrayOf("Action",
            "Adventure",
            "Animation",
            "Comedy",
            "Crime",
            "Documentary",
            "Drama",
            "Family",
            "Fantasy",
            "History",
            "Horror",
            "Music",
            "Mystery",
            "Romance",
            "Science Fiction",
            "Thriller",
            "TV Movie",
            "War",
            "Western")
        adapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, genres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner = findViewById(R.id.spinnerGenre) as Spinner
        spinner.adapter=adapter
        spinner.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                genre=adapter.getItem(position).toString()
                changeFilm()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    //display random film or local film
    private fun initSwitch() {
        val s = findViewById(R.id.switchUnplayed) as Switch

        s.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            switchUnplayed = !switchUnplayed
            changeFilm()
        })
    }

/////////   Shake control  ////////

    private fun initShakeListener(){
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(sensorManager)!!.registerListener(sensorListener, sensorManager!!
            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH
    }

    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta
            if (acceleration > 25) {
                changeFilm()
                //Toast.makeText(applicationContext, "Shake event detected", Toast.LENGTH_SHORT).show()
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onResume() {
        sensorManager?.registerListener(sensorListener, sensorManager!!.getDefaultSensor(
            Sensor .TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }

    override fun onPause() {
        sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
    }

///////////////////////// ////////

//========================== recyclerview film =======================================

    //when a item is selected in spinnerGenre
    fun changeFilm(){
        if(!switchUnplayed){
            val d: ExternDatabase = ExternDatabase(this, this)
            d.getFilmByGenre(genre)
        }
        else{
            localDatabase.setGenre(genre)
            val d: ExternDatabase = ExternDatabase(this, this)
            d.getFilmFromLocalData()
        }
    }

    //call local film after fetch json film from externData
    fun getLocalFilm(films:List<Film>){
        localDatabase.loadFilm(films)
    }

    //reload the recyclerView
    fun displayFilm(list: List<Film>){
        val adapter = FilmAdapter(list)
        var recyclerView = findViewById(R.id.film_recycler_view) as RecyclerView
        adapter.setOnItemClickListener(object :FilmAdapter.OnItemClickListener{ //if user click on film
            override fun onItemClick(position: Int) {
                showCustomDialog(list.get(position))
            }
        })
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    //show a dialog for rate a film
    fun showCustomDialog(film: Film){
        var dialog: Dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_film)

        val poster = dialog.findViewById(R.id.poster) as ImageView
        val ratingBar = dialog.findViewById(R.id.ratingBar) as RatingBar

        Picasso.get().load(film.poster_path).into(poster);
        dialog.show()
        ratingBar.setOnRatingBarChangeListener{r, fl, b->
            saveFilm(film, r.rating)
            dialog.dismiss()
        }
    }

    //call localDatabase for save the film selected by showCustomDialog
    private fun saveFilm(film:Film, rateUser:Float){
        localDatabase.saveFilm(film, rateUser)
    }

//========================== recyclerview platform =======================================

    private fun initPlatforms(){

        val netflix = Platform(Platform.NETFLIX)
        val primeVideo = Platform(Platform.PRIME_VIDEO)

        val list = arrayOf(netflix, primeVideo)

        val adapter = PlatformAdpater(list)
        var recyclerView = findViewById(R.id.platform_recycler_view) as RecyclerView
        adapter.setOnItemClickListener(object :PlatformAdpater.OnItemClickListener{ //if user click on film
            override fun onItemClick(position: Int) {
                println("sdfsf")
            }
        })
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
    }

    private fun createPlatform(){


    }
}