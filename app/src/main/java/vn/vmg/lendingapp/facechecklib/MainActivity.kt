package vn.vmg.lendingapp.facechecklib

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val faceCheckView = findViewById<FaceCheckView>(R.id.faceCheckView)
        faceCheckView.initListener(this,"","")
    }
}