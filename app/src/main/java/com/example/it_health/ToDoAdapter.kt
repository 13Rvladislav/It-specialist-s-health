package com.example.it_health

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class ToDoAdapter(context: Context, toDoList: MutableList<ToDoModel>) : BaseAdapter() {
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private var itemList = toDoList
    private var updateAndDelete: UpdateDelete = context as UpdateDelete
    override fun getCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getItem(p0: Int): Any {
        return itemList.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val UID: String = itemList.get(p0).UID as String
        val itemTextData = itemList.get(p0).itemDataText as String
        val done: Boolean = itemList.get(p0).done as Boolean
        val view: View
        val viewHolder: ListViewHolder
        if (p1 == null) {
            view = inflater.inflate(R.layout.new_item, p2, false)
            viewHolder = ListViewHolder(view)
            view.tag = viewHolder
        } else {
            view = p1
            viewHolder = view.tag as ListViewHolder
        }
        viewHolder.textLabel1.text = itemTextData
        viewHolder.isDone.isChecked = done


        viewHolder.isDone.setOnClickListener()
        {
            updateAndDelete.modifyItem(UID, !done)
        }
        viewHolder.isDeleted.setOnClickListener()
        {
            updateAndDelete.onItemDelete(UID)
        }

        return view
    }

    private class ListViewHolder(row: View?) {
        val textLabel1: TextView = row!!.findViewById(R.id.text) as TextView
        val isDone: CheckBox = row!!.findViewById(R.id.CheckBox) as CheckBox
        val isDeleted: ImageButton = row!!.findViewById(R.id.close) as ImageButton


    }
}