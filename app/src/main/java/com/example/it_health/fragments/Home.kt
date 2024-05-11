package com.example.it_health.fragments

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.it_health.R
import com.example.it_health.ToDoAdapter
import com.example.it_health.ToDoModel
import com.example.it_health.databinding.FragmentHomeBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue


class Home : Fragment(),SensorEventListener {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

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
    var database = FirebaseDatabase.getInstance().reference
    var step: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    val Fragment.packageManager get() = activity?.packageManager
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val sharedPreferences =
            requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val savedName = sharedPreferences.getString("name", "")
        var savedwater = sharedPreferences.getInt("waterNow", 0)
        var savedsleep = sharedPreferences.getInt("key3", 0)
        var workTime = sharedPreferences.getString("workTime", "")
        binding.sleepcounter.text = savedsleep.toString()
        binding.name.setText("Привет," + savedName + "!")
        binding.watercounter.text = savedwater.toString()


        //проверки разрешений
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onStepCounterPermissionGranted()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                RQ_PERMISSION_FOR_STEPCOUNTER_CODE
            )
        }





        //карточка вода
        binding.cardWather.setOnClickListener {
            val dialog = Dialog(requireContext())
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
                requireContext().getSharedPreferences("myPrefs", AppCompatActivity.MODE_PRIVATE)
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
                        requireActivity(),
                        "Водный баланс был успешно восполнен!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(requireActivity(), "Поле пустое", Toast.LENGTH_LONG).show()
                }
            }


        }
        //карточка сон
        binding.cardSleep.setOnClickListener() {
            val dialog = Dialog(requireContext())
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
                Toast.makeText(requireActivity(), "Начало сна", Toast.LENGTH_LONG).show()
                date1 = LocalDateTime.now()
            }
            //кнопка конец
            btnend.setOnClickListener() {
                date2 = LocalDateTime.now()
                Toast.makeText(requireActivity(), "Конец сна", Toast.LENGTH_LONG).show()
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
            val dialog = Dialog(requireContext())
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
            change.setOnClickListener()
            {

                if (workTime != null) {
                    editor.putLong("key4", workTime.toLong() * 3600000)
                }
                editor.apply()
                timer?.cancel()

            }
        }
        loadData()
        resetSteps()
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onPause() {
        super.onPause()
            // сохранять текущее состояние таймера
        timer?.cancel()
    }



    override fun onResume() {
        super.onResume()
        //запускать таймер если он был запущен и выдавать ему данные исходные если не был запущен не запускать его

        running = true
        val stepSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            Toast.makeText(requireActivity(), "No Step sensor detected on this device", Toast.LENGTH_SHORT)
                .show()
        } else {
           // sensorManager?.registerListener(requireActivity(), stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //для шагомера

    // проверка разрешений
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Home.RQ_PERMISSION_FOR_STEPCOUNTER_CODE -> {
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
            Uri.fromParts("package", "com.example.it_health.fragments", null)
        )
        if (packageManager?.resolveActivity(
                sppSettingsIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            ) == null
        ) {
            Toast.makeText(requireContext(), "Permissions are denied forever", Toast.LENGTH_SHORT).show()
        } else {
            AlertDialog.Builder(requireContext())
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
        Toast.makeText(requireActivity(), "StepCounter sensor permission is granted", Toast.LENGTH_LONG).show()
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

    // сброс шагов
    private fun resetSteps() {
        binding.textViewSteps.setOnClickListener {
            Toast.makeText( requireActivity(), "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }
        binding.textViewSteps.setOnLongClickListener {
            previousTotalSteps = totalSteps
            binding.textViewSteps.text = 0.toString()
            saveData()
            true
        }
    }

    private fun saveData() {
        val sharedPreferences =requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences =requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)
        Log.d("MainActivity", "$savedNumber")
        previousTotalSteps = savedNumber
    }




}