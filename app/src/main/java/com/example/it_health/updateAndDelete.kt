package com.example.it_health

import android.text.BoringLayout

interface UpdateDelete{


    fun  modifyItem(itemUID:String,isDone:Boolean)
    fun  onItemDelete(itemUID:String)
}