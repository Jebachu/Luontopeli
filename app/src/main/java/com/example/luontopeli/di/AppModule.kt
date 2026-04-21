package com.example.luontopeli.di

import android.content.Context
import androidx.room.Room
import com.example.luontopeli.data.local.AppDatabase
import com.example.luontopeli.data.local.dao.NatureSpotDao
import com.example.luontopeli.data.local.dao.WalkSessionDao
//import com.example.luontopeli.data.remote.firebase.AuthManager
//import com.example.luontopeli.data.remote.firebase.FirestoreManager
//import com.example.luontopeli.data.remote.firebase.StorageManager
import com.example.luontopeli.data.repository.NatureSpotRepository
import com.example.luontopeli.data.repository.WalkRepository
import com.example.luontopeli.location.LocationManager
import com.example.luontopeli.sensor.StepCounterManager
import com.example.luontopeli.ml.PlantClassifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- ROOM DATABASE ---
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "luontopeli_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideNatureSpotDao(db: AppDatabase): NatureSpotDao = db.natureSpotDao()

    @Provides
    fun provideWalkSessionDao(db: AppDatabase): WalkSessionDao = db.walkSessionDao()


   /* // --- FIREBASE STUBIT ---
    @Provides
    @Singleton
    fun provideAuthManager(): AuthManager = AuthManager()

    @Provides
    @Singleton
    fun provideFirestoreManager(): FirestoreManager = FirestoreManager()

    @Provides
    @Singleton
    fun provideStorageManager(): StorageManager = StorageManager()
*/

    // --- REPOSITORYT ---
    @Provides
    @Singleton
    fun provideNatureSpotRepository(
        dao: NatureSpotDao,
       // auth: AuthManager,
        //// storage: StorageManager
    ): NatureSpotRepository {
        return NatureSpotRepository(
            dao = dao,
            // firestore = firestore,
            //storage = storage
        )
    }

    @Provides
    @Singleton
    fun provideWalkRepository(
        dao: WalkSessionDao
    ): WalkRepository = WalkRepository(dao)


    // --- SENSORIT & GPS ---
    @Provides
    @Singleton
    fun provideStepCounterManager(
        @ApplicationContext context: Context
    ): StepCounterManager = StepCounterManager(context)

    @Provides
    @Singleton
    fun provideLocationManager(
        @ApplicationContext context: Context
    ): LocationManager = LocationManager(context)


    // --- ML KIT ---
    @Provides
    @Singleton
    fun providePlantClassifier(
        @ApplicationContext context: Context
    ): PlantClassifier = PlantClassifier(context)
}
