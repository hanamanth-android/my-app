package com.example.todolist.util

import androidx.appcompat.widget.SearchView


inline fun SearchView.onQueryTextchanged(crossinline  listener:(String)->Unit){
    this.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }

        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }
    })
}