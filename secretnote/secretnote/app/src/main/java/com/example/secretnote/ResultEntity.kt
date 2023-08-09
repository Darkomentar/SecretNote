package com.example.secretnote

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "lp")
data class LoginPassword(
    @PrimaryKey(autoGenerate = true)
    var _id: Int = 0,
    @ColumnInfo(name = "login") var login: String,
    @ColumnInfo(name = "password") var password: String
)

@Entity(tableName = "sc")
data class SecretCode(
    @PrimaryKey(autoGenerate = true)
    var _id: Int = 0,
    @ColumnInfo(name = "code") var code: String
)

//@Entity(tableName = "ip")
//data class Ipport(
//    @PrimaryKey(autoGenerate = true)
//    var _id: Int = 0,
//    @ColumnInfo(name = "ip") var ip: String,
//    @ColumnInfo(name = "port") var port: String
//)