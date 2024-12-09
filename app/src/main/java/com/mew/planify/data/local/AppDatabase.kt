package com.mew.planify.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mew.planify.data.local.dao.MateriaDao
import com.mew.planify.data.local.dao.ProfesorDao
import com.mew.planify.data.local.dao.TareaDao
import com.mew.planify.data.local.entities.MateriaEntity
import com.mew.planify.data.local.entities.ProfesorEntity
import com.mew.planify.data.local.entities.TareaEntity
import com.mew.planify.utils.Converters
import java.util.Date
import java.util.concurrent.Executors

@Database(
    entities = [
        TareaEntity::class,
        ProfesorEntity::class,
        MateriaEntity::class
    ],
    version = 1, // Incrementa con cada cambio en la estructura de la BD
    exportSchema = true // Cambia a true si quieres exportar el esquema de la BD
)
@TypeConverters(Converters::class)

abstract class AppDatabase : RoomDatabase() {
    abstract fun tareaDao(): TareaDao
    abstract fun profesorDao(): ProfesorDao
    abstract fun materiaDao(): MateriaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "planifydb"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}