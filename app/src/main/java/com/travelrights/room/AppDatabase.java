package com.travelrights.room;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.travelrights.model.AirportResponse;

@Database(entities = {AirportResponse.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AirportDao taskDao();
}