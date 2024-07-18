package com.example.integrated

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.example.integrated.ui.theme.IntegratedTheme
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class MainActivity : ComponentActivity() {
    private var isLogging = mutableStateOf(false)
    private var logJob: Job? = null
    private val storage = Firebase.storage
    private val database = FirebaseDatabase.getInstance()
    private lateinit var logFile: File
    private lateinit var logFileName: String
    private lateinit var zipFile: File
    private lateinit var zipFileName: String
    private val logList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MyApp() {
        val isLogging by isLogging

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Logcat Logger") }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        if (!isLogging) {
                            startLogging()
                        } else {
                            stopLogging()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isLogging) "Stop Logging" else "Start Logging")
                }
            }
        }
    }

    private fun startLogging() {
        createNewLogFile()
        isLogging.value = true
        logJob = lifecycleScope.launch(Dispatchers.IO) {
            val process = ProcessBuilder().command("logcat").redirectErrorStream(true).start()
            val inputStream = process.inputStream

            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?=null

            while (isLogging.value && reader.readLine().also { line = it } != null) {
                logList.add(line!!)
                if (logList.size >= 100) { // adjust the size as needed
                    writeLogsToJsonFile()
                    compressJsonToZip()
                    uploadZipFile()
                    logList.clear()
                }
            }

            if (logList.isNotEmpty()) {
                writeLogsToJsonFile()
                compressJsonToZip()
                uploadZipFile()
                logList.clear()
            }

            inputStream.close()
        }
    }

    private fun stopLogging() {
        isLogging.value = false
        logJob?.cancel()
        logJob = null
    }

    private fun createNewLogFile() {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        logFileName = "logcat_$timestamp.json"
        logFile = File(getExternalFilesDir(null), logFileName)
        zipFileName = "logcat_$timestamp.zip"
        zipFile = File(getExternalFilesDir(null), zipFileName)
    }

    private fun writeLogsToJsonFile() {
        val jsonArray = JSONArray()
        logList.forEach { log ->
            val jsonObject = JSONObject()
            jsonObject.put("log", log)
            jsonArray.put(jsonObject)
        }

        val jsonString = jsonArray.toString()
        FileOutputStream(logFile, false).use { outputStream ->
            outputStream.write(jsonString.toByteArray())
        }
    }

    private fun compressJsonToZip() {
        ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
            FileInputStream(logFile).use { fis ->
                val zipEntry = ZipEntry(logFileName)
                zipOut.putNextEntry(zipEntry)
                fis.copyTo(zipOut, 1024)
            }
        }
    }

    private fun uploadZipFile() {
        val storageRef = storage.reference.child("logs/$zipFileName")
        val fileUri = zipFile.toUri()

        storageRef.putFile(fileUri)
            .addOnSuccessListener {
                // File uploaded successfully
                Log.i(TAG, "Zip file uploaded successfully")
            }
            .addOnFailureListener { exception ->
                // Handle unsuccessful uploads
                Log.e(TAG, "Failed to upload zip file", exception)
            }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
