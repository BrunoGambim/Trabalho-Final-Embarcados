package com.example.embarcados

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.embarcados.data.network.SocketAdapter
import com.example.embarcados.data.database.UserRepository
import com.example.embarcados.data.network.Esp8266Gateway
import com.example.embarcados.data.network.HTTPAdapter
import com.example.embarcados.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserRepository.INSTANCE = UserRepository(applicationContext)
        Esp8266Gateway.INSTANCE = Esp8266Gateway(UserRepository.INSTANCE)
        SocketAdapter.INSTANCE = SocketAdapter(applicationContext)
        HTTPAdapter.INSTANCE = HTTPAdapter(applicationContext)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        supportActionBar?.title = "EMBARCADOS"

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

}