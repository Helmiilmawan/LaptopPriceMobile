package com.example.laptoppredict

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var model: TFLiteModelHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Toolbar dan Drawer setup
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open, R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_about -> startActivity(Intent(this, AboutActivity::class.java))
                R.id.nav_feature -> startActivity(Intent(this, FeatureActivity::class.java))
                R.id.nav_architecture -> startActivity(Intent(this, ArchitectureActivity::class.java))
                // R.id.nav_simulation diabaikan karena simulasi langsung di MainActivity
            }
            drawerLayout.closeDrawers()
            true
        }

        // Inisialisasi model prediksi
        model = TFLiteModelHelper(this)

        // Spinner untuk brand
        val brands = arrayOf("Asus", "Acer", "Lenovo", "HP", "Dell")
        val brandSpinner = findViewById<Spinner>(R.id.input_brand)
        val brandAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, brands)
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        brandSpinner.adapter = brandAdapter

        // Spinner untuk RAM
        val rams = arrayOf("4", "8", "16", "32")
        val ramSpinner = findViewById<Spinner>(R.id.input_ram)
        val ramAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, rams)
        ramAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ramSpinner.adapter = ramAdapter

        // Spinner untuk Penyimpanan
        val storages = arrayOf("256", "512", "1000")
        val storageSpinner = findViewById<Spinner>(R.id.input_storage)
        val storageAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, storages)
        storageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        storageSpinner.adapter = storageAdapter

        // Input fields dan tombol
        val processor = findViewById<EditText>(R.id.input_processor)
        val screen = findViewById<EditText>(R.id.input_screen)
        val weight = findViewById<EditText>(R.id.input_weight)
        val resultText = findViewById<TextView>(R.id.result_text)
        val predictBtn = findViewById<Button>(R.id.predict_button)

        predictBtn.setOnClickListener {
            try {
                val brandIndex = brandSpinner.selectedItemPosition.toFloat()
                val cpuSpeed = processor.text.toString().toFloat()
                val ramSize = rams[ramSpinner.selectedItemPosition].toFloat()
                val storageCap = storages[storageSpinner.selectedItemPosition].toFloat()
                val screenSize = screen.text.toString().toFloat()
                val weightKg = weight.text.toString().toFloat()

                val input = floatArrayOf(
                    brandIndex, cpuSpeed, ramSize, storageCap, screenSize, weightKg
                )

                val result = model.predictPrice(input)
                resultText.text = "Prediksi Harga: Rp %.0f".format(result)
            } catch (e: Exception) {
                resultText.text = "Input tidak valid!"
            }
        }
    }
}
