package com.example.secretnote
import androidx.room.*

//@Database(entities = [LoginPassword::class, SecretCode::class, Ipport::class], version = 2)
@Database(entities = [LoginPassword::class, SecretCode::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun loginPasswordDao(): LoginPasswordDao
    abstract fun secretCodeDao(): SecretCodeDao
//    abstract fun ipportDao(): IpportDao
}

@Dao
interface LoginPasswordDao {

    @Query("SELECT * FROM lp")
    fun all(): List<LoginPassword>
    @Insert
    fun insert(loginPassword: LoginPassword)
    @Query("DELETE FROM lp")
    fun delete()

}

@Dao
interface SecretCodeDao {

    @Query("SELECT * FROM sc")
    fun all(): List<SecretCode>
    @Insert
    fun insert(secretCode: SecretCode)
    @Query("DELETE FROM sc")
    fun delete()

}
//@Dao
//interface IpportDao {
//
//    @Query("SELECT * FROM ip")
//    fun all(): List<Ipport>
//    @Insert
//    fun insert(ipport: Ipport)
//    @Query("DELETE FROM ip")
//    fun delete()
//
//}