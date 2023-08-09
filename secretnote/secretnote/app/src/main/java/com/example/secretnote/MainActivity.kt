package com.example.secretnote

import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.seretnote.SaveReadFile
import kotlinx.coroutines.launch
import android.Manifest
import android.content.res.Configuration
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.annotation.RequiresApi
import com.chaos.view.PinView

lateinit var mainActivityContext : AppCompatActivity



class MainActivity : AppCompatActivity() {
    var intentt: Intent? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = FirebaseDatabase.getInstance()
        val ipReference = database.getReference("ip")
        val portReference = database.getReference("port")

        if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(POST_NOTIFICATIONS), 1)
        }
        setContentView(R.layout.activity_main)
        VM_REGg = ViewModelProvider(this).get(VM_REG::class.java)
        VM_REGg.handlerMA.value = Handler(Looper.getMainLooper())
        mainActivityContext = this


        ipReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ip = snapshot.getValue(String::class.java)
                Log.d("ipport", ip.toString())
                VM_REGg.ip.value = ip
                // Используйте значение ip по вашему усмотрению
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработайте ошибку чтения значения "ip"
            }
        })

        portReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val port = snapshot.getValue(String::class.java)
                Log.d("ipport", port.toString())
                VM_REGg.port.value = port
                // Используйте значение port по вашему усмотрению
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработайте ошибку чтения значения "port"
            }
        })


        var pinView = findViewById<PinView>(R.id.pinview)
        var flag = true
        var code = ""
        Thread {
            val db by lazy {
                Room.databaseBuilder(
                    mainActivityContext,
                    AppDatabase::class.java,
                    "sc.db"
                ).build();
            }
            var allLp = db.secretCodeDao().all()
            db.close()
            if (allLp.count() > 0)
            {
                code = allLp[0].code
            }
            else
            {
                code = "error"
            }
            flag = false;
        }.start()
        while(flag)
        {
            Thread.sleep(50)
        }

        if(code == "error")
        {
            var login_for_vm = ""
            var password_for_vm = "";

            val thread = Thread {

                val db by lazy { Room.databaseBuilder(mainActivityContext, AppDatabase::class.java, "lp.db").build() ;}

                var allLp = db.loginPasswordDao().all()
                db.close()
                if(allLp.size > 0)
                {
                    var t : grpc_method = grpc_method(VM_REGg.ip.value!!, VM_REGg.port.value!!);

                    var errorr = t.login(allLp[0].login, allLp[0].password)
                    errorr = 0

                    if(errorr == 0)
                    {

                        login_for_vm = allLp[0].login
                        password_for_vm = allLp[0].password
                        lifecycleScope.launch {
                            VM_REGg.login.value = login_for_vm
                            VM_REGg.password.value = password_for_vm
                            VM_REGg.publicKey.value = t.get_publick_key(login_for_vm)
                            val Saver = SaveReadFile()
                            VM_REGg.privateKey.value = Saver.readFileFromInternalStorage(mainActivityContext, "private.txt")
                        }
                        intentt = Intent(applicationContext, ClientSecretNoteService::class.java);
                        startService(intentt);
                        startActivity(Intent(applicationContext, Chats::class.java))
                    }
                    if(errorr == 3)
                    {
                        Toast.makeText(this,"Ошибка связи с сервером", Toast.LENGTH_LONG).show()
                    }
                    if(errorr == 1)
                    {
                        Toast.makeText(this,"Неверный логин или пароль", Toast.LENGTH_LONG).show()
                        startActivity( Intent( applicationContext, LoginActivity::class.java))
                    }
                }
                else
                {
                    startActivity( Intent( applicationContext, LoginActivity::class.java))
                }

            }
            thread.start()
        }
        else
        {
            pinView.visibility = View.VISIBLE
            pinView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Здесь можно выполнить действия до изменения текста в PinView
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // Здесь можно выполнить действия при изменении текста в PinView
                }

                override fun afterTextChanged(s: Editable) {
                    if (s.count() == 4)
                    {
                        // Здесь код типа шо делать если введен пин код
                        if(pinView.text.toString() == code)
                        {
                            var login_for_vm = ""
                            var password_for_vm = "";

                            val thread = Thread {

                                val db by lazy { Room.databaseBuilder(mainActivityContext, AppDatabase::class.java, "lp.db").build() ;}

                                var allLp = db.loginPasswordDao().all()
                                db.close()
                                if(allLp.size > 0)
                                {
                                    var t : grpc_method = grpc_method(VM_REGg.ip.value!!, VM_REGg.port.value!!);

                                    var errorr = t.login(allLp[0].login, allLp[0].password)
                                    errorr = 0

                                    if(errorr == 0)
                                    {

                                        login_for_vm = allLp[0].login
                                        password_for_vm = allLp[0].password
                                        lifecycleScope.launch {
                                            VM_REGg.login.value = login_for_vm
                                            VM_REGg.password.value = password_for_vm
                                            VM_REGg.publicKey.value = t.get_publick_key(login_for_vm)
                                            val Saver = SaveReadFile()
                                            VM_REGg.privateKey.value = Saver.readFileFromInternalStorage(mainActivityContext, "private.txt")
                                        }
                                        intentt = Intent(applicationContext, ClientSecretNoteService::class.java);
                                        startService(intentt);
                                        startActivity(Intent(applicationContext, Chats::class.java))
                                    }
                                    if(errorr == 3)
                                    {
                                        Toast.makeText(mainActivityContext,"Ошибка связи с сервером", Toast.LENGTH_LONG).show()
                                    }
                                    if(errorr == 1)
                                    {
                                        Toast.makeText(mainActivityContext,"Неверный логин или пароль", Toast.LENGTH_LONG).show()
                                        startActivity( Intent( applicationContext, LoginActivity::class.java))
                                    }
                                }
                                else
                                {
                                    startActivity( Intent( applicationContext, LoginActivity::class.java))
                                }

                            }
                            thread.start()
                        }
                        else
                        {
                            Toast.makeText(
                                mainActivityContext,
                                "Неверный логин или пароль",
                                Toast.LENGTH_LONG
                            ).show()
                            s.clear()
                        }

                    }
                }
            })
        }
    }



    override fun onDestroy() {
        stopService(intentt)
        super.onDestroy()
    }

}

