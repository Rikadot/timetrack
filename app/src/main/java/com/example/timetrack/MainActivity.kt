package com.example.timetrack

import android.annotation.SuppressLint
import android.icu.text.DateFormat
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.timetrack.ui.theme.TimeTrackTheme
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity : ComponentActivity() {
    private lateinit var db:AppDatabase

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getDatabase(this)


        val buttonInsert = findViewById<Button>(R.id.button_insert)

        //TODO retrieve current day even after app was closed
        var currentId:Int = -1
        buttonInsert.setOnClickListener {
            buttonInsert.isEnabled = false

            lifecycleScope.launch {
                val timestamp = System.currentTimeMillis()
                db.ArbeitszeitDao().startWork(timestamp)
                //val count = db.timestampDao().getCount()
                currentId=db.ArbeitszeitDao().getIdByWorkday(timestamp)
            }
        }
        /*
        buttonStop.setOnClickListener{
            buttonInsert.isEnabled = true
            buttonStop.isEnabled = false
            lifecycleScope.launch {
                val timestamp = System.currentTimeMillis()
                db.ArbeitszeitDao().setStopWork(timestamp, currentId)
            }
        }
         */
    }

    private fun insertWorkTime(){
        val timestamp = System.currentTimeMillis();
    }

    private suspend fun hasWorkStarted():Boolean{
        val temp = db.ArbeitszeitDao().getLatestStop()
        return if(temp == null)
            true
        else
            false
    }
}
}