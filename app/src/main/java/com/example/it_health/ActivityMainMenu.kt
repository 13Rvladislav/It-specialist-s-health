package com.example.it_health

import android.Manifest
import android.app.Dialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.it_health.databinding.ActivityMainMenuBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
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
    var step: Int = 0


    private val CHANNEL_ID = "channel_id"
    private val notificationId = 101

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //получение данных пользователя из бд
        readData()

        val sharedPreferences =
            getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val savedName = sharedPreferences.getString("name", "")
        var savedwater = sharedPreferences.getInt("waterNow", 0)
        var savedsleep = sharedPreferences.getInt("key3", 0)
        var workTime = sharedPreferences.getString("workTime", "")
        previousTotalSteps = sharedPreferences.getFloat("key1", 0f)
        binding.textViewSteps.setText("$previousTotalSteps")
        binding.sleepcounter.text = savedsleep.toString()
        binding.name.setText("Привет," + savedName + "!")
        binding.watercounter.text = savedwater.toString()
        notify(this)

        //првоерка разрешений для шагомера
        checkingPermissions()
        //навигация
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.home
        bottomNavigationView.setOnItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.home -> {
                    //  overridePendingTransition(R.id.anim.slide_in_right,R.id.anim.left)
                    true
                }
                R.id.sport -> {
                    startActivity(Intent(this, ActivitySport::class.java))
                    true
                }
                R.id.todo -> {
                    startActivity(Intent(this, ActivityTodo::class.java))
                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this, ActivityProfile::class.java))
                    true
                }
                else -> false
            }
        }


//проверки разрешений


        binding.watercounter.text = savedwater.toString()
        binding.sleepcounter.text = savedsleep.toString()

        // карточка вода
        binding.cardWather.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_water)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            val cansel = dialog.findViewById(R.id.cansel) as ImageButton
            val text = dialog.findViewById(R.id.water_now) as TextView
            val edt = dialog.findViewById(R.id.email) as TextInputLayout
            val btn = dialog.findViewById(R.id.authorization) as TextView

            //память получение воды

            val sharedPreferences =
                getSharedPreferences("myPrefs", AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            var savedNumber = sharedPreferences.getInt("waterNow", 0)
            var normWater = sharedPreferences.getString("waterNorm", "")
            text.text = savedNumber.toString() + "/".toString() + normWater.toString()
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
                val edt = edt.getEditText()?.getText().toString()
                if (edt.isNotEmpty()) {
                    water = savedNumber.toInt() + edt.toInt()
                    text.text = savedNumber.toString() + "/".toString() + normWater.toString()
                    editor.putInt("waterNow", water)
                    editor.apply()
                    savedNumber = sharedPreferences.getInt("waterNow", water)
                    binding.watercounter.text = savedNumber.toString()
                    dialog.cancel()
                    Toast.makeText(
                        this,
                        "Водный баланс был успешно восполнен!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(this, "Поле пустое", Toast.LENGTH_LONG).show()
                }
            }

        }
        // карточка сна
        binding.cardSleep.setOnClickListener() {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_sleep)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()


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
                Toast.makeText(this, "Начало сна", Toast.LENGTH_LONG).show()
                date1 = LocalDateTime.now()
            }
            //кнопка конец
            btnend.setOnClickListener() {
                date2 = LocalDateTime.now()
                Toast.makeText(this, "Конец сна", Toast.LENGTH_LONG).show()
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
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_work)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val cansel = dialog.findViewById(R.id.cansel) as ImageButton
            val text = dialog.findViewById(R.id.water_now) as TextView
            val btnstart = dialog.findViewById(R.id.start) as Button
            val change = dialog.findViewById(R.id.end) as Button
            dialog.show()
//кнопка назад
            cansel.setOnClickListener()
            {
                dialog.cancel()
            }
            //кнопка старт
            btnstart.setOnClickListener()
            {
                var timers = sharedPreferences.getString("workTime", "")!!.toLong()
                editor.putLong("key4", timers * 3600000)
                editor.apply()
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
                        if (min==0.toLong())
                            sendNotification()
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
            change.setOnClickListener()
            {

                if (workTime != null) {
                    editor.putLong("key4", workTime.toLong() * 3600000)
                }
                editor.apply()
                timer?.cancel()

            }
        }
        //карточка итоги дня
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
            var savedwater = sharedPreferences.getInt("waterNow", 0)
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
        realDate(this)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        notify(this)

    }

    //дата
    @RequiresApi(Build.VERSION_CODES.O)
    private fun realDate(context: Context) {
        val sharedPref = context.getSharedPreferences("my_shared_pref", MODE_PRIVATE)
        val savedDate = sharedPref.getString("savedDate", "")

        val newDate = if (savedDate.isNullOrEmpty()) {
            sharedPref.edit().putString("savedDate", "0000-00-00").apply()
            "0000-00-00"
        } else {
            savedDate
        }

        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(formatter)

        if (newDate != formattedDate) {
            sharedPref.edit().putString("savedDate", formattedDate).apply()
            //обнуляю шагомер
            val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putFloat("key1", previousTotalSteps)
            editor.apply()
        }
    }

    //уведомления
    private fun notify(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification title"
            val decriptionText = "Not"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = decriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Напоминание!")
            .setContentText("Прошел рабочий час,прервитесь на перерыв!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this))
        {
            notify(notificationId, builder.build())
        }
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

    //проверка даны ли разрешения
    private fun checkingPermissions() {
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

    //шагомер логика
    //изменение шагов
    override fun onSensorChanged(event: SensorEvent?) {
        if (running) {
            totalSteps = event!!.values[0]
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()

            binding.textViewSteps.text = ("$currentSteps")
            step = currentSteps
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

    //сохранение шагов
    private fun saveData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    //получение шагов
    private fun loadData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val savedNumber = sharedPreferences.getFloat("key1", 0f)
        Log.d("MainActivity", "$savedNumber")
        previousTotalSteps = savedNumber
    }

    //пользовательские данные
    private fun readData() {
        val sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //получение информации о пользователе user-info
        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(FirebaseAuth.getInstance().currentUser!!.uid).child("User-info").get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val fio = it.child("name").value
                    val lifeStyle = it.child("lifeStyle").value
                    val height = it.child("height").value
                    val weight = it.child("weight").value
                    val sex = it.child("sex").value
                    val workTime = it.child("workTime").value
                    editor.putString("height", height.toString())
                    editor.putString("lifeStyle", lifeStyle.toString())
                    editor.putString("name", fio.toString())
                    editor.putString("sex", sex.toString())
                    editor.putString("weight", weight.toString())
                    editor.putString("workTime", workTime.toString())

                    editor.apply()

                }

                database = FirebaseDatabase.getInstance().getReference("Users")
                database.child(FirebaseAuth.getInstance().currentUser!!.uid).child("MainWorkInfo")
                    .get()
                    .addOnSuccessListener {
                        if (it.exists()) {
                            val waterNorm = it.child("waterNorm").value
                            editor.putString("waterNorm", waterNorm.toString())
                            editor.apply()

                        }
                    }
            }
    }

}

