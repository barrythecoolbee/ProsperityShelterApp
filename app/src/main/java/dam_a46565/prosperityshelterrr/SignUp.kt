package dam_a46565.prosperityshelterrr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity(), View.OnClickListener {

    private lateinit var fullName : EditText
    private lateinit var username : EditText
    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var progressBar : ProgressBar
    private lateinit var signupButton : Button

    private lateinit var mFirebaseAuth : FirebaseAuth
    var rootNode : FirebaseDatabase? = null
    var reference: DatabaseReference? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        fullName = findViewById(R.id.FullNameText)
        username = findViewById(R.id.UsernameText)
        email = findViewById(R.id.EmailText)
        password = findViewById(R.id.PasswordText)
        progressBar = findViewById(R.id.ProgressBar)
        signupButton = findViewById(R.id.RegisterButton)

        rootNode = FirebaseDatabase.getInstance()
        reference = rootNode?.reference!!.child("Users")

        mFirebaseAuth = FirebaseAuth.getInstance()

        signupButton.setOnClickListener(this)

            //val intent = Intent(this@SignUp, LoginActivity::class.java)
            //startActivity(intent)

    }

    override fun onClick(v : View){
        registerUser()
    }

    private fun registerUser() {
        var fullNameText : String = fullName.text.toString().trim()
        var usernameText : String = username.text.toString().trim()
        var emailText : String = email.text.toString().trim()
        var passwordText : String = password.text.toString().trim()

        if(fullNameText.isEmpty()){
            fullName.error = "Full name is required!"
            fullName.requestFocus()
            return
        }

        if(usernameText.isEmpty()){
            username.error = "Username is required!"
            username.requestFocus()
            return
        }

        if(emailText.isEmpty()){
            email.error = "Email is required!"
            email.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            email.error = "Please provide a valid email!"
            email.requestFocus()
            return
        }

        if(passwordText.isEmpty()){
            password.error = "Password is required!"
            password.requestFocus()
            return
        }

        if(passwordText.length < 6){
            password.error = "Min password length should be 6 characters!"
            password.requestFocus()
            return
        }

        progressBar.visibility = View.VISIBLE
        mFirebaseAuth.createUserWithEmailAndPassword(emailText, passwordText)
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    val currentUser = mFirebaseAuth.currentUser

                    mFirebaseAuth.signInWithEmailAndPassword(emailText, passwordText)

                    val currentUserDb = reference?.child(currentUser?.uid!!)
                    currentUserDb?.child("fullName")?.setValue(fullNameText)
                    currentUserDb?.child("username")?.setValue(usernameText)
                    currentUserDb?.child("email")?.setValue(emailText)

                    Toast.makeText(this@SignUp, "User has been registered successfully!", Toast.LENGTH_LONG).show()
                    progressBar.visibility = View.GONE
                    finish()
                } else {
                    Toast.makeText(this@SignUp, "Failed to register!!!! Try again!", Toast.LENGTH_LONG).show()
                    progressBar.visibility = View.GONE
                }

            }
    }
}