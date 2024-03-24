package com.example.it_health

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.it_health.databinding.ActivityMainMenuBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.absoluteValue


class ActivityMainMenu : AppCompatActivity(), SensorEventListener {

    // we have assigned sensorManger to nullable
    private var sensorManager: SensorManager? = null

    // Creating a variable which will give the running status
    // and initially given the boolean value as false
    private var running = false

    // Creating a variable which will counts total steps
    // and it has been given the value of 0 float
    private var totalSteps = 0f

    // Creating a variable which counts previous total
    // steps and it has also been given the value of 0 float
    private var previousTotalSteps = 0f
    private var water = 0

    var toDoList: MutableList<ToDoModel>? = null
    lateinit var adapter: ToDoAdapter
    private var listViewItem: ListView? = null
    var timer: CountDownTimer? = null
    private lateinit var binding: ActivityMainMenuBinding
    var database = FirebaseDatabase.getInstance().reference
var step:Int=0
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
//проверки разрешений
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onStepCounterPermissionGranted()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                RQ_PERMISSION_FOR_STEPCOUNTER_CODE
            )
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        var savedwater = sharedPreferences.getInt("key2", 0)
        var savedsleep = sharedPreferences.getInt("key3", 0)


        binding.watercounter.text = savedwater.toString()
        binding.sleepcounter.text = savedsleep.toString()

// карточка вода
        binding.cardWather.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_water)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val cansel = dialog.findViewById(R.id.cansel) as ImageButton
            val text = dialog.findViewById(R.id.water_now) as TextView
            val edt = dialog.findViewById(R.id.email) as TextView
            val btn = dialog.findViewById(R.id.authorization) as TextView

//память получение воды

            val sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            var savedNumber = sharedPreferences.getInt("key2", 0)
            text.text = savedNumber.toString() + "/3440".toString()
            //отоброжение диалогового
            dialog.show()
            //кнопка назад
            cansel.setOnClickListener()
            {
                dialog.cancel()
            }
            //кнопка добавить
            btn.setOnClickListener()
            {
                val email = edt?.text.toString()
                if (email.isNotEmpty()) {
                    water = savedNumber.toInt() + email.toInt()
                    text.text = water.toString() + "/3440".toString()
                    editor.putInt("key2", water)
                    editor.apply()
                    savedNumber = sharedPreferences.getInt("key2", water)
                    binding.watercounter.text = savedNumber.toString()
                } else {
                    Toast.makeText(this, "Поле пустое", Toast.LENGTH_LONG).show()
                }
            }

        }
// карточка сна
        binding.cardSleep.setOnClickListener() {
            val sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_sleep)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val cansel = dialog.findViewById(R.id.cansel) as ImageButton
            val text = dialog.findViewById(R.id.water_now) as TextView
            val btnstart = dialog.findViewById(R.id.start) as TextView
            val btnend = dialog.findViewById(R.id.end) as TextView

            lateinit var date1: LocalDateTime
            lateinit var date2: LocalDateTime
            dialog.show()
//кнопка назад
            cansel.setOnClickListener()
            {
                dialog.cancel()
            }
            //кнопка старт
            btnstart.setOnClickListener() {
                date1 = LocalDateTime.now()
            }
            //кнопка конец
            btnend.setOnClickListener() {
                date2 = LocalDateTime.now()

                var diffSeconds = ChronoUnit.SECONDS.between(date1, date2).absoluteValue.toString()
                lateinit var diffHours: String
                lateinit var diffMinutes: String


                var minute = (diffSeconds.toInt() / 60).toInt()
                var hour = (minute / 60).toInt()
                diffHours = hour.toString()
                diffMinutes = minute.toString()


                if (diffHours.length == 1) {
                    diffHours = '0' + diffHours
                }
                if (diffMinutes.length == 1) {
                    diffMinutes = '0' + diffMinutes
                }
                text.text = diffHours + ':' + diffMinutes
                editor.putInt("key3", hour)
                editor.apply()
            }


        }

        //карточка рабочее время
        binding.cardw.setOnClickListener() {
            val sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE)

            val editor = sharedPreferences.edit()
            var savedwater = sharedPreferences.getInt("key2", 0)


            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_work)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val cansel = dialog.findViewById(R.id.cansel) as ImageButton
            val text = dialog.findViewById(R.id.water_now) as TextView
            val btnstart = dialog.findViewById(R.id.start) as Button
            val change = dialog.findViewById(R.id.change) as Button
            val edt = dialog.findViewById(R.id.email) as EditText
            dialog.show()


            change.setOnClickListener()
            {
                val pass = edt?.text.toString()
                editor.putLong("key4", pass.toLong() * 3600000)
                editor.apply()
                timer?.cancel()

                if (pass.isNotEmpty() && pass.isNotEmpty()) {
                } else {
                    Toast.makeText(this, " поле не заполнено", Toast.LENGTH_SHORT).show()
                }
            }

//кнопка назад
            cansel.setOnClickListener()
            {
                dialog.cancel()
            }
            //кнопка старт
            btnstart.setOnClickListener()
            {
                var time = sharedPreferences.getLong("key4", 0)

                timer = object : CountDownTimer(time, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        // Used for formatting digit to be in 2 digits only
                        val f: NumberFormat = DecimalFormat("00")
                        val hour = millisUntilFinished / 3600000 % 24
                        val min = millisUntilFinished / 60000 % 60
                        val sec = millisUntilFinished / 1000 % 60
                        text.setText(
                            f.format(hour)
                                .toString() + ":" + f.format(min) + ":" + f.format(sec)
                        )
                        editor.putString(
                            "key6",
                            f.format(hour).toString() + ":" + f.format(min) + ":" + f.format(sec)
                        )
                        editor.putLong("key4", millisUntilFinished)

                        editor.apply()
                        binding.workcounter.setText(
                            f.format(hour)
                                .toString() + ":" + f.format(min) + ":" + f.format(sec)
                        )
                    }

                    // When the task is over it will print 00:00:00 there
                    override fun onFinish() {
                        text.setText("00:00:00")
                    }
                }.start()
            }
        }



        binding.cardW.setOnClickListener() {


            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_final)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val cansel = dialog.findViewById(R.id.cansel) as ImageButton
            val text1 = dialog.findViewById(R.id.sleep) as TextView
            val text2 = dialog.findViewById(R.id.work) as TextView
            val text3 = dialog.findViewById(R.id.steps) as TextView
            val text4 = dialog.findViewById(R.id.water) as TextView
            val sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE)

            val editor = sharedPreferences.edit()
            var savedwork = sharedPreferences.getString("key6", "")


            var strtext1 = text1.text.toString()
            var strtext2 = text2.text.toString()
            var strtext3 = text3.text.toString()
            var strtext4 = text4.text.toString()


            text1.text = strtext1 + savedsleep.toString()
            text2.text = strtext2 + savedwork.toString()
            text3.text = strtext3 + step.toString()
            text4.text = strtext4 + savedwater.toString()
            dialog.show()


//кнопка назад
            cansel.setOnClickListener()
            {
                dialog.cancel()
            }
            //кнопка старт
        }


        loadData()
        resetSteps()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        val view = binding.root
        setContentView(view)
        // Adding a context of SENSOR_SERVICE aas Sensor Manager
    }


    // проверка разрешений
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RQ_PERMISSION_FOR_STEPCOUNTER_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onStepCounterPermissionGranted()
                } else {
                    if (!shouldShowRequestPermissionRationale(android.Manifest.permission.ACTIVITY_RECOGNITION)) {
                        askUserForOpeningUpSettings()
                    }
                }
            }
        }
    }

    private fun askUserForOpeningUpSettings() {
        val sppSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        if (packageManager.resolveActivity(
                sppSettingsIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            ) == null
        ) {
            Toast.makeText(this, "Permissions are denied forever", Toast.LENGTH_SHORT).show()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Permission denied")
                .setMessage(
                    "You have denied permissions permanently. " +
                            "You can change it in app settings.\n\n" +
                            "Would you like to open app settings?"
                )
                .setPositiveButton("Open") { _, _ ->
                    startActivity(sppSettingsIntent)
                }
                .create()
                .show()
        }
    }

    private fun onStepCounterPermissionGranted() {
        Toast.makeText(this, "StepCounter sensor permission is granted", Toast.LENGTH_LONG).show()
    }

    private companion object {
        const val RQ_PERMISSION_FOR_STEPCOUNTER_CODE = 1
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//        Not implemented yst
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (running) {
            totalSteps = event!!.values[0]
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()

            binding.textViewSteps.text = ("$currentSteps")
            step=currentSteps
        }
    }

    override fun onResume() {
        super.onResume()
        running = true
        val stepSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            Toast.makeText(this, "No Step sensor detected on this device", Toast.LENGTH_SHORT)
                .show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }

    }


    // сброс шагов
    private fun resetSteps() {
        binding.textViewSteps.setOnClickListener {
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }
        binding.textViewSteps.setOnLongClickListener {
            previousTotalSteps = totalSteps
            binding.textViewSteps.text = 0.toString()
            saveData()
            true
        }
    }

    private fun saveData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)
        Log.d("MainActivity", "$savedNumber")
        previousTotalSteps = savedNumber
    }


}

