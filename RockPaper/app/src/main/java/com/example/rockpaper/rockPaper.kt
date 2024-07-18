package com.example.rockpaper

fun main()
{
    val integer= mutableListOf(1,2,3,4,5)
    for(index in 0 until integer.size)
    {
        integer[index]=integer[index]*2

    }
    println(integer)

}
