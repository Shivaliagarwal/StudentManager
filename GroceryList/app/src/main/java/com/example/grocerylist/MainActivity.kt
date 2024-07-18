package com.example.grocerylist

import Item
import ItemAdapter
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

        private lateinit var recyclerView: RecyclerView
        private lateinit var itemAdapter: ItemAdapter
        private val items = mutableListOf<Item>()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            recyclerView = findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            itemAdapter = ItemAdapter(items)
            recyclerView.adapter = itemAdapter
        }

        fun submitData(view: View) {
            val input = findViewById<EditText>(R.id.input)
            val text = input.text.toString()
            if (text.isNotEmpty()) {
                items.add(Item(text))
                itemAdapter.notifyItemInserted(items.size - 1)
                input.text.clear()
            }
        }
    }

