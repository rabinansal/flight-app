package com.travelrights.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "task")
data class AirportResponse  (
    @PrimaryKey(autoGenerate = true)
    var id: Int?=null,
    @ColumnInfo(name = "main_airport_name")
    var main_airport_name: String?=null,
    @ColumnInfo(name = "name")
    var name: String?=null,
    @ColumnInfo(name = "code")
    var code: String?=null,
    @ColumnInfo(name = "country_name")
    var country_name: String?=null


)