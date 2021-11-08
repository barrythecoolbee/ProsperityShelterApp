package dam_a46565.prosperityshelterrr

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Outline
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException

class SettingsPage : AppCompatActivity() {

    private lateinit var mFirebaseAuth : FirebaseAuth
    var rootNode : FirebaseDatabase? = null
    var reference: DatabaseReference? = null

    private var storage = FirebaseStorage.getInstance()
    private lateinit var storageRef: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_page)

        val image = findViewById<ImageView>(R.id.image)
        val curveRadius = 100F

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            image.outlineProvider = object : ViewOutlineProvider() {

                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun getOutline(view: View?, outline: Outline?) {
                    outline?.setRoundRect(0, 0, view!!.width, (view.height+curveRadius).toInt(), curveRadius)
                }
            }

            image.clipToOutline = true

        }

        //findViewById
        var homePage : ImageButton = findViewById(R.id.HomeButton)
        var calendarPage : ImageButton = findViewById(R.id.CallendarButton)
        var sponsorPage : ImageButton = findViewById(R.id.StarButton)
        var userProfile : ImageButton = findViewById(R.id.UserButton)
        var userProfile2 : ImageButton = findViewById(R.id.ProfileButton)
        var settingsPage : ImageButton = findViewById(R.id.SettingsButton)
        var settingsButtonPage : Button = findViewById(R.id.SettingsSettings_button)
        var aboutButtonPage : Button = findViewById(R.id.SettingsAbout_button)

        //Firebase
        rootNode = FirebaseDatabase.getInstance()
        reference = rootNode?.reference!!.child("Users")
        mFirebaseAuth = FirebaseAuth.getInstance()

        //SetOnClickListener
        homePage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SettingsPage, MainActivity::class.java)
            startActivity(intent)
        })

        calendarPage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SettingsPage, MeetingsPage::class.java)
            startActivity(intent)
        })

        sponsorPage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SettingsPage, ExtraPage::class.java)
            startActivity(intent)
        })

        userProfile.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SettingsPage, ProfilePage::class.java)
            startActivity(intent)
        })

        userProfile2.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SettingsPage, ProfilePage::class.java)
            startActivity(intent)
        })

        settingsPage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SettingsPage, SettingsPage::class.java)
            startActivity(intent)
        })

        settingsButtonPage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SettingsPage, Settings::class.java)
            startActivity(intent)
        })

        aboutButtonPage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SettingsPage, AboutPage::class.java)
            startActivity(intent)
        })

        loadProfile()
    }

    private fun loadProfile(){
        val user = mFirebaseAuth.currentUser
        val userreference = reference?.child(user?.uid!!)

        userreference?.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    storageRef = storage.getReference("profilePictures/" + snapshot.child("profilePic").value.toString())

                    var localFile: File = File.createTempFile("tmpfile", ".jpeg")
                    storageRef.getFile(localFile).addOnSuccessListener {
                        var img = findViewById<ImageView>(R.id.profile_image)

                        var bitmap: Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                        img.setImageBitmap(bitmap)
                    }
                } catch (e: IOException) {
                    Toast.makeText(
                        this@SettingsPage, "Unable to set profile picture!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        })
    }
}