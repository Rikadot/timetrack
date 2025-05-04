package com.example.timetrack

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase


@Entity (tableName = "Arbeitszeit")
data class Arbeitszeit(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ZeitID" ) val zeitId: Int,
    @ColumnInfo(name = "StartArbeit") val startArbeit: Long?,
    @ColumnInfo(name = "StopArbeit") val stopArbeit: Long?,
    @ColumnInfo(name = "Auftrag") val auftrag: String?,
    @ColumnInfo(name = "uebernachtung") val uebernachtung: Boolean?
)


@Entity(
    tableName = "Pausenzeit",
    foreignKeys = [
        ForeignKey(
            entity = Arbeitszeit::class,
            parentColumns = arrayOf("ZeitID"),
            childColumns = arrayOf("ZeitID"),
            onDelete = ForeignKey.CASCADE,

        )
    ]
)
data class Pausenzeit(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "PausenzeitID") val pausenzeitId: Int,
    @ColumnInfo(name = "ZeitID") val zeitId: Int,
    @ColumnInfo(name = "StartArbeit") val startPause: Long?,
    @ColumnInfo(name = "StopArbeit") val stopPause: Long?
    //TODO übernachtung
)

@Dao
interface ArbeitszeitDao {
    //GETTERS
    @Query("SELECT ZeitID FROM Arbeitszeit WHERE startArbeit LIKE :day")
    suspend fun getIdByWorkday(day: Long): Int
    @Query("SELECT ZeitID FROM Arbeitszeit ORDER BY ZeitID DESC limit 1")
    suspend fun getlatestEntry()

    //GET START and STOP
    @Query("SELECT StopArbeit FROM Arbeitszeit ORDER BY ZeitID DESC limit 1")
    suspend fun getLatestStop():Int?
/*
    @Query(
        "SELECT * FROM Arbeitszeit "+
        "JOIN Pausenzeit ON Arbeitszeit.zeitID=Pausenzeit.zeitID WHERE Arbeitszeit.zeitID LIKE :id"
    )
    fun getWorkdayWithBreaksById(id: Int)
*/
    //start Work Querys
    @Query("INSERT INTO Arbeitszeit (startArbeit) VALUES (:startTime)")
    suspend fun startWork(startTime :Long)
    //updates
    @Query("UPDATE Arbeitszeit SET stopArbeit = :startTime WHERE zeitID LIKE :id")
    suspend fun setStartWork(startTime: Long, id:Int)
    @Query("UPDATE Arbeitszeit SET stopArbeit = :stopTime WHERE zeitID LIKE :id")
    suspend fun setStopWork(stopTime: Long, id:Int)
    @Query("UPDATE Arbeitszeit SET auftrag = :notes WHERE zeitID Like :id")
    suspend fun setNotes(notes: String, id: Int)


    //TODO add break start stop
}

@Database(entities = [Arbeitszeit::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun ArbeitszeitDao(): ArbeitszeitDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "timestamp_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}