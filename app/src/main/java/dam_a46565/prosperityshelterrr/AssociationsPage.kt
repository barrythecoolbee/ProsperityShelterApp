package dam_a46565.prosperityshelterrr

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Outline
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.IOException

class AssociationsPage : AppCompatActivity() {

    var associationImage : CircleImageView ?= null
    var associationNamee : TextView ?= null

    private lateinit var mFirebaseAuth : FirebaseAuth
    var rootNode : FirebaseDatabase? = null
    private lateinit var reference: DatabaseReference

    private var storage = FirebaseStorage.getInstance()
    private lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_associations_page)

        val data : Intent = intent
        val associationName : String = data.getStringExtra("associationName").toString()
        associationImage = findViewById(R.id.AssociationImage)
        associationNamee = findViewById(R.id.AssociationName_text)
        getAssociationData(associationName)

        val image = findViewById<ImageView>(R.id.image)
        val image2 = findViewById<ImageView>(R.id.image2)
        val image3 = findViewById<ImageView>(R.id.image3)
        val curveRadius = 100F

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            image.outlineProvider = object : ViewOutlineProvider() {

                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun getOutline(view: View?, outline: Outline?) {
                    outline?.setRoundRect(0, 0, view!!.width, (view.height+curveRadius).toInt(), curveRadius)
                }
            }

            image.clipToOutline = true

            image2.outlineProvider = object : ViewOutlineProvider() {

                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun getOutline(view: View?, outline: Outline?) {
                    outline?.setRoundRect(0, (0-curveRadius).toInt(), view!!.width, view.height, curveRadius)
                }
            }

            image2.clipToOutline = true

            image3.outlineProvider = object : ViewOutlineProvider() {

                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun getOutline(view: View?, outline: Outline?) {
                    outline?.setRoundRect(0, (0-curveRadius).toInt(), view!!.width, view.height, curveRadius)
                }
            }

            image3.clipToOutline = true

        }

        //findViewById
        var homePage : ImageButton = findViewById(R.id.HomeButton)
        var calendarPage : ImageButton = findViewById(R.id.CallendarButton)
        var sponsorPage : ImageButton = findViewById(R.id.StarButton)
        var userProfile : ImageButton = findViewById(R.id.UserButton)
        var userProfile2 : ImageButton = findViewById(R.id.ProfileButton)
        var settingsPage : ImageButton = findViewById(R.id.SettingsButton)
        var aboutPage : Button = findViewById(R.id.AssociationAbout_button)
        var volunteeringPage : Button = findViewById(R.id.AssociationVolunteering_button)

        //Firebase
        rootNode = FirebaseDatabase.getInstance()
        reference = rootNode?.reference!!.child("Users")
        mFirebaseAuth = FirebaseAuth.getInstance()

        //SetOnClickListener
        homePage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@AssociationsPage, MainActivity::class.java)
            startActivity(intent)
        })

        calendarPage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@AssociationsPage, MeetingsPage::class.java)
            startActivity(intent)
        })

        sponsorPage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@AssociationsPage, ExtraPage::class.java)
            startActivity(intent)
        })

        userProfile.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@AssociationsPage, ProfilePage::class.java)
            startActivity(intent)
        })

        userProfile2.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@AssociationsPage, ProfilePage::class.java)
            startActivity(intent)
        })

        settingsPage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@AssociationsPage, SettingsPage::class.java)
            startActivity(intent)
        })

        aboutPage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@AssociationsPage, AboutAssociationPage::class.java)
            intent.putExtra("associationName", associationName)
            startActivity(intent)
        })

        volunteeringPage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@AssociationsPage, VolunteeringPage::class.java)
            intent.putExtra("associationName", associationName)
            startActivity(intent)
        })

        loadProfile()
    }

    private fun getAssociationData(associationName : String) {
        reference = FirebaseDatabase.getInstance().getReference("Associations/" + associationName)
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    associationNamee!!.text = snapshot.child("name").value.toString()
                    try {
                        storageRef = storage.getReference("associationPictures/" + snapshot.child("image").value.toString())

                        var localFile: File = File.createTempFile("tmpfile", ".png")
                        storageRef.getFile(localFile).addOnSuccessListener {
                            var img = findViewById<ImageView>(R.id.AssociationImage)

                            var bitmap: Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                            img.setImageBitmap(bitmap)
                        }
                    } catch (e: IOException) {
                        Toast.makeText(
                            this@AssociationsPage, "Unable to set profile picture!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun loadProfile(){
        val user = mFirebaseAuth.currentUser
        val userreference = reference?.child(user?.uid!!)

        userreference?.addValueEventListener(object: ValueEventListener{
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
                        this@AssociationsPage, "Unable to set profile picture!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        })
    }
}