package com.example.it_health

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.it_health.databinding.ActivityTodoBinding
import com.example.it_health.utils.TaskAdapter
import com.example.it_health.utils.ToDoData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dbClases.ToDos
private lateinit var binding: ActivityTodoBinding
class ActivityTodo : AppCompatActivity(), TaskAdapter.TaskAdapterInterface {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var authId: String
    private val TAG = "ActivityTodo"

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var toDoItemList: MutableList<ToDoData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)
        binding = ActivityTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.todo

        bottomNavigationView.setOnItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, ActivityMainMenu::class.java))
                    //  overridePendingTransition(R.id.anim.slide_in_right,R.id.anim.left)
                    true
                }
                R.id.sport -> {
                    startActivity(Intent(this, ActivitySport::class.java))
                    true
                }
                R.id.todo -> {

                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this, ActivityProfile::class.java))
                    true
                }
                else -> false
            }
        }
        auth = FirebaseAuth.getInstance()
        init()
        getTaskFromFirebase()
        //кнопка редактирования
        binding.addBtnHome.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_add_task)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()


            //сохранение
            dialog.findViewById<View>(R.id.save).setOnClickListener {

                //Задача
                val task =
                    dialog.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.task)
                        .getEditText()?.getText().toString()
                //дата
                val date =
                    dialog.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.date)
                        .getEditText()?.getText().toString()
                if (task.isNotEmpty() && date.isNotEmpty()) {
//запись
                    val todos = ToDos(
                        task,
                        date,
                    )

                    //запись в FB
                    (FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("Todo")).push().setValue(todos)

                    Toast.makeText(this, "Задача успешно добавлена", Toast.LENGTH_LONG)
                        .show()

                    dialog.dismiss() // Закрытие диалогового окна
                } else {
                    Toast.makeText(
                        this,
                        "Одно или несколько полей пусты",
                        Toast.LENGTH_LONG
                    ).show()

                }

            }
            // Закрытие диалогового окна
            dialog.findViewById<View>(R.id.cansel).setOnClickListener {
                dialog.dismiss() // Закрытие диалогового окна
            }
        }

    }
    private fun getTaskFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                toDoItemList.clear()
                for (taskSnapshot in snapshot.children) {
                    val todoTask =
                        taskSnapshot.key?.let { ToDoData(it, taskSnapshot.child("nameTask").value.toString(),taskSnapshot.child("date").value.toString()) }

                    if (todoTask != null) {
                        toDoItemList.add(todoTask)
                    }

                }
                Log.d(TAG, "onDataChange: " + toDoItemList)
                taskAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ActivityTodo, error.toString(), Toast.LENGTH_SHORT).show()
            }


        })
    }
    private fun init() {
        database = FirebaseDatabase.getInstance().getReference("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Todo")



        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(this@ActivityTodo)

        toDoItemList = mutableListOf()
        taskAdapter = TaskAdapter(toDoItemList)
        taskAdapter.setListener(this@ActivityTodo)
        binding.mainRecyclerView.adapter = taskAdapter
    }
    override fun onDeleteItemClicked(toDoData: ToDoData, position: Int) {
        database.child(toDoData.taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}