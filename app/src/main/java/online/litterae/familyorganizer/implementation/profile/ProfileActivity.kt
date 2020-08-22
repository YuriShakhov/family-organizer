package online.litterae.familyorganizer.implementation.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import online.litterae.familyorganizer.R
import online.litterae.familyorganizer.abstracts.view.BaseCompatActivity

class ProfileActivity : BaseCompatActivity() {
    override fun init(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_profile)
    }
}