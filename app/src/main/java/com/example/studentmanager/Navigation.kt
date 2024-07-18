package com.example.studentmanager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.Flow
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import createNotificationChannel
import showNotification


@Composable
fun Navigation(db:AppDatabase,viewModel: StudentViewModel){

    val navController= rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MainScreen.route ){
        composable(route = Screen.MainScreen.route){
            MainScreen(navController = navController)

        }
        composable(route = Screen.DetailsScreen.route){
            DetailsScreen( navController = navController,db,viewModel,)
        }
        composable(route = Screen.AddScreen.route){
            AddScreen(db,viewModel)
        }
        composable(route = "${Screen.EditScreen.route}/{studentId}") { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId")?.toIntOrNull()
            if (studentId != null) {
                Edit(db, viewModel, studentId)
            }
        }

    }
}

@Composable
fun MainScreen(navController: NavController){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Color(0XFFFBDCE2))) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Student Manager",
                fontSize = 38.sp, // Large text size
                modifier = Modifier.padding(bottom = 200.dp)
            )

            Button(
                onClick = { navController.navigate(Screen.DetailsScreen.route) },
                modifier = Modifier.padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color(0XFFFF9FB2)
                )
            ) {
                Text(text = "Show Students", fontSize = 18.sp)
            }
            Button(
                onClick = { navController.navigate(Screen.AddScreen.route) },
                modifier = Modifier.padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color(0XFFFF9FB2)
                )
            ) {
                Text(text = "Add New Student", fontSize = 18.sp)
            }
        }
    }

}

@Composable
fun DetailsScreen(navController: NavController, db: AppDatabase, viewModel: StudentViewModel) {
    val studentDao = db.studentdao()
    val studentsFlow = studentDao.getOrderedByName()
    val students by studentsFlow.collectAsState(initial = emptyList())
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val dialogMessage = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF60AB9A))
    ) {
        Text(
            text = "List of Students",
            fontSize = 40.sp,
            modifier = Modifier
                .padding(35.dp)
                .align(Alignment.CenterHorizontally)
        )
        LazyColumn {
            items(students) { student ->
                StudentItem(
                    student = student,
                    onDeleteClick = {
                        viewModel.deleteStudent(student)
                        dialogMessage.value = "Student ${student.name} has been deleted!"
                        showDialog.value = true
                    },
                    onEditClick = {
                        navController.navigate("${Screen.EditScreen.route}/${student.id}")
                    },
                    onEmailClick = {
                        openEmail(context,student.address)
                    },
                    onPhoneClick = {
                        openPhoneDialer(context, student.contactNumber)
                    }
                )
            }
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("OK")
                }
            },
            title = { Text(text = "Deleted") },
            text = { Text(text = dialogMessage.value) }
        )
    }
}

@Composable
fun StudentItem(student: Student, onDeleteClick: () -> Unit, onEditClick: (Student) -> Unit, onEmailClick: () -> Unit, onPhoneClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .background(Color(0XFFDEDEE0), shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            Text(
                text = "Name: ${student.name}",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Contact: ${student.contactNumber}",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Age: ${student.age}",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Gender: ${student.gender}",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Email: ${student.address}",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        Column {
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Student"
                )
            }
            IconButton(onClick = { onEditClick(student) }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Student"
                )
            }
            IconButton(onClick = onEmailClick) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email Student"
                )
            }
            IconButton(onClick = onPhoneClick) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call Student"
                )
            }
        }
    }
}

@Composable
fun AddScreen(db: AppDatabase, viewModel: StudentViewModel) {
    val context = LocalContext.current

    // Create the notification channel
    LaunchedEffect(Unit) {
        createNotificationChannel(context)
    }
    val nameState = remember { mutableStateOf("") }
    val contactNumberState = remember { mutableStateOf("") }
    val ageState = remember { mutableStateOf("") }
    val genderState = remember { mutableStateOf("") }
    val addressState = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0XFF0ACDFF))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "FILL  DETAILS",
                fontSize = 40.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(horizontal = 25.dp, vertical = 25.dp)
                    .background(Color(0XFFDEDEE0), shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    TextField(
                        value = nameState.value,
                        onValueChange = { nameState.value = it },
                        placeholder = { Text("Enter name of student") },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    TextField(
                        value = contactNumberState.value,
                        onValueChange = { contactNumberState.value = it },
                        placeholder = { Text("Enter contact number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    TextField(
                        value = ageState.value,
                        onValueChange = { ageState.value = it },
                        placeholder = { Text("Enter age") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    TextField(
                        value = genderState.value,
                        onValueChange = { genderState.value = it },
                        placeholder = { Text("Enter gender") },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    TextField(
                        value = addressState.value,
                        onValueChange = { addressState.value = it },
                        placeholder = { Text("Enter Email address") },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (nameState.value.isEmpty() || contactNumberState.value.isEmpty() ||
                        ageState.value.isEmpty() || genderState.value.isEmpty() ||
                        addressState.value.isEmpty()
                    ) {
                        dialogMessage.value = "Please fill the complete Details!"
                        showDialog.value = true
                    } else {
                        val newStudent = Student(
                            name = nameState.value,
                            contactNumber = contactNumberState.value,
                            age = ageState.value.toIntOrNull() ?: 0,
                            gender = genderState.value,
                            address = addressState.value
                        )
                        viewModel.insertStudent(newStudent)
                        dialogMessage.value = "Saved Successfully!"
                        showDialog.value = true

                        // Reset the text fields
                        nameState.value = ""
                        contactNumberState.value = ""
                        ageState.value = ""
                        genderState.value = ""
                        addressState.value = ""



                        showNotification(context, "Student Added", "Student ${newStudent.name} has been added successfully.")

                    }

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0XFFFF9FB2),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.5f)
            ) {
                Text(text = "SAVE")
            }
        }
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                confirmButton = {
                    Button(onClick = { showDialog.value = false }) {
                        Text("OK")
                    }
                },
                title = { Text(text = "Alert!") },
                text = { Text(text = dialogMessage.value) }
            )
        }
    }
}

@Composable
fun Edit(db: AppDatabase, viewModel: StudentViewModel, studentId: Int) {
    val studentDao = db.studentdao()
    val student by studentDao.getStudentById(studentId).collectAsState(initial = null)

    val nameState = remember { mutableStateOf("") }
    val contactNumberState = remember { mutableStateOf("") }
    val ageState = remember { mutableStateOf("") }
    val genderState = remember { mutableStateOf("") }
    val addressState = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }

    LaunchedEffect(student) {
        student?.let {
            nameState.value = it.name
            contactNumberState.value = it.contactNumber
            ageState.value = it.age.toString()
            genderState.value = it.gender
            addressState.value = it.address
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0XFF0ACDFF))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "EDIT DETAILS",
                fontSize = 40.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(horizontal = 25.dp, vertical = 25.dp)
                    .background(Color(0XFFDEDEE0), shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    TextField(
                        value = nameState.value,
                        onValueChange = { nameState.value = it },
                        placeholder = { Text("Enter name of student") },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    TextField(
                        value = contactNumberState.value,
                        onValueChange = { contactNumberState.value = it },
                        placeholder = { Text("Enter contact number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    TextField(
                        value = ageState.value,
                        onValueChange = { ageState.value = it },
                        placeholder = { Text("Enter age") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    TextField(
                        value = genderState.value,
                        onValueChange = { genderState.value = it },
                        placeholder = { Text("Enter gender") },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    TextField(
                        value = addressState.value,
                        onValueChange = { addressState.value = it },
                        placeholder = { Text("Enter Email address") },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (nameState.value.isEmpty() || contactNumberState.value.isEmpty() ||
                        ageState.value.isEmpty() || genderState.value.isEmpty() ||
                        addressState.value.isEmpty()
                    ) {
                        dialogMessage.value = "Please fill the complete Details!"
                        showDialog.value = true
                    } else {
                        val updatedStudent = student?.copy(
                            name = nameState.value,
                            contactNumber = contactNumberState.value,
                            age = ageState.value.toIntOrNull() ?: 0,
                            gender = genderState.value,
                            address = addressState.value
                        )
                        if (updatedStudent != null) {
                            viewModel.updateStudent(updatedStudent)
                            dialogMessage.value = "Updated Successfully!"
                            showDialog.value = true
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0XFFFF9FB2),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.5f)
            ) {
                Text(text = "UPDATE")
            }
        }
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                confirmButton = {
                    Button(onClick = { showDialog.value = false }) {
                        Text("OK")
                    }
                },
                title = { Text(text = "Alert") },
                text = { Text(text = dialogMessage.value) }
            )
        }
    }
}

