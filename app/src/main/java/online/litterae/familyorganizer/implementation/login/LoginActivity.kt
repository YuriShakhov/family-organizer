package online.litterae.familyorganizer.implementation.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import online.litterae.familyorganizer.application.MainApplication
import online.litterae.familyorganizer.R
import online.litterae.familyorganizer.abstracts.view.BaseCompatActivity
import online.litterae.familyorganizer.implementation.family.FamilyActivity
import javax.inject.Inject

class LoginActivity : BaseCompatActivity(), LoginContract.View, View.OnClickListener {
    
    @Inject lateinit var presenter: LoginContract.Presenter

    override fun init(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_login)
        MainApplication.getAppComponent().createLoginComponent().inject(this)
        presenter.attach(this)
        presenter.checkIfLoggedIn()
        login.setOnClickListener(this)
        register.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.login -> {
                loading.visibility = View.VISIBLE
                presenter.signIn(username.getText().toString(), password.getText().toString())
            }
            R.id.register -> {
                loading.visibility = View.VISIBLE
                presenter.register(username.getText().toString(), password.getText().toString())
            }
        }
    }

    override fun enter () {
        loading.visibility = View.GONE
        startActivity(Intent(this, FamilyActivity::class.java))
    }

    override fun showErrorMessage(message: String) {
        loading.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}