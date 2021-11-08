package dam_a46565.prosperityshelterrr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LoginActivity : AppCompatActivity() {

    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var mFirebaseAuth : FirebaseAuth
    private lateinit var login : Button

    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.EmailText)
        password = findViewById(R.id.PasswordText)

        mFirebaseAuth = FirebaseAuth.getInstance()

        login = findViewById(R.id.LoginButton)

        val signupButton : Button = findViewById(R.id.RegisterButton)
        signupButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@LoginActivity, SignUp::class.java)
            startActivity(intent)
        })

        login.setOnClickListener(View.OnClickListener {
            var emailText : String = email.text.toString()
            var passwordText : String = password.text.toString()

            if(!emailText.isEmpty() && !passwordText.isEmpty()) {
                mFirebaseAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(
                        this,
                        OnCompleteListener<AuthResult?> { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success")
                                val user: FirebaseUser? = mFirebaseAuth.currentUser
                                val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
                                startActivity(intent)
                                //updateUI(user)
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(
                                    TAG,
                                    "signInWithEmail:failure",
                                    task.exception
                                )
                                Toast.makeText(
                                    this@LoginActivity, "Authentication failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                //updateUI(null)
                            }

                        })
                }
        })
    }
}
