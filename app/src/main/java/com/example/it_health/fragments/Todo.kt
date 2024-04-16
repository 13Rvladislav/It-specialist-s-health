package com.example.it_health.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.it_health.R
import com.example.it_health.databinding.FragmentTodoBinding
import com.example.it_health.utils.ToDoData
import com.example.it_health.utils.TaskAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import dbClases.ToDos


class Todo : Fragment(), TaskAdapter.TaskAdapterInterface {

    private var _binding: FragmentTodoBinding? = null

    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var authId: String
    private val TAG = "HomeFragment"

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var toDoItemList: MutableList<ToDoData>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        init()
        getTaskFromFirebase()

        //кнопка редактирования
        binding.addBtnHome.setOnClickListener {
            val dialog = Dialog(requireContext())
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

                    Toast.makeText(requireActivity(), "Задача успешно добавлена", Toast.LENGTH_LONG)
                        .show()

                    dialog.dismiss() // Закрытие диалогового окна
                } else {
                    Toast.makeText(
                        requireActivity(),
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
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }


        })
    }


    private fun init() {
        database = FirebaseDatabase.getInstance().getReference("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Todo")



        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)

        toDoItemList = mutableListOf()
        taskAdapter = TaskAdapter(toDoItemList)
        taskAdapter.setListener(this)
        binding.mainRecyclerView.adapter = taskAdapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDeleteItemClicked(toDoData: ToDoData, position: Int) {
        database.child(toDoData.taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }


}

