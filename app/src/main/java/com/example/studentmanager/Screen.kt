package com.example.studentmanager

sealed class Screen(val route:String) {
    object MainScreen:Screen(route = "main_screen")
    object DetailsScreen:Screen(route = "details_screen")
    object AddScreen:Screen(route="add_screen")
    object EditScreen:Screen(route = "edit_screen")
}