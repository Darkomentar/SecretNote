package com.example.secretnote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.secretnote.databinding.ChatsActivityBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar


lateinit var chats_k:AppCompatActivity
class Chats : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ChatsActivityBinding
     //lateinit var recyclerView: RecyclerView;
     //lateinit var recyclerViewAdapter: RecycleViewAdapterMessages
     lateinit var context: Context







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this;
        chats_k = this;

        binding = ChatsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.importExportPrivateKey, R.id.protectionAccess
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        val item = menu.findItem(R.id.action_settings)
        item.setOnMenuItemClickListener { menuItem ->

            when (menuItem.itemId) {
                R.id.action_settings -> {
                    Thread {
                        val db by lazy {
                            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "lp.db").build();
                        }
                        db.loginPasswordDao().delete()
                        db.close()
                    }.start()
                    startActivity( Intent( applicationContext, LoginActivity::class.java))
                    return@setOnMenuItemClickListener true
                }
//                R.id.action_add_user_group_chat -> {
//
//                    startActivity( Intent( applicationContext, LoginActivity::class.java))
//                    return@setOnMenuItemClickListener true
//                }
            }
            false
        }
        return true
    }





    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}