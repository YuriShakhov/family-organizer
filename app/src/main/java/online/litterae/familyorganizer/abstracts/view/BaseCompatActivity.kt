package online.litterae.familyorganizer.abstracts.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseCompatActivity : AppCompatActivity(), BaseViewInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        overridePendingTransition(0, 0)
    }

    override fun getContext(): Context = this

    protected abstract fun init(savedInstanceState: Bundle?)
}