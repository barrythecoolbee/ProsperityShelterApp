package dam_a46565.prosperityshelterrr

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Outline
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.w3c.dom.Text
import java.io.File
import java.io.IOException

class PetProfile : AppCompatActivity() {
    var petNamee : TextView ?= null
    var petAssociation :TextView ?= null
    var petGender : TextView ?= null
    var petRace : TextView ?= null
    var petFur : TextView ?= null
    var petCastration : TextView ?= null
    var petDescription : TextView ?= null
    var genderImage : ImageView ?= null

    private lateinit var mFirebaseAuth : FirebaseAuth
    var rootNode : FirebaseDatabase? = null
    lateinit var reference: DatabaseReference
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReference2: DatabaseReference

    private var storage = FirebaseStorage.getInstance()
    private lateinit var storageRef: StorageReference
    private lateinit var storageRefGallery: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_profile)

        val data : Intent = intent
        val name : String = data.getStringExtra("name").toString()

        petNamee = findViewById(R.id.PetNameText)
        petAssociation = findViewById(R.id.PetAssociationText)
        petGender = findViewById(R.id.GenderText)
        petRace = findViewById(R.id.RaceText)
        petFur = findViewById(R.id.FurText)
        petCastration = findViewById(R.id.CastrationText)
        petDescription = findViewById(R.id.PetDescriptionText)
        genderImage = findViewById(R.id.GenderImage)

        getPetData(name)

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
        var heartButton : ImageView = findViewById(R.id.favouritePetBtn)
        var meetingButton : Button = findViewById(R.id.BookMeeting_button)

        //Firebase
        rootNode = FirebaseDatabase.getInstance()
        reference = rootNode?.reference!!.child("Users")
        mFirebaseAuth = FirebaseAuth.getInstance()

        //SetOnClickListener
        homePage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@PetProfile, MainActivity::class.java)
            startActivity(intent)
        })

        calendarPage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@PetProfile, MeetingsPage::class.java)
            startActivity(intent)
        })

        sponsorPage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@PetProfile, ExtraPage::class.java)
            startActivity(intent)
        })

        userProfile.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@PetProfile, ProfilePage::class.java)
            startActivity(intent)
        })

        userProfile2.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@PetProfile, ProfilePage::class.java)
            startActivity(intent)
        })

        settingsPage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@PetProfile, SettingsPage::class.java)
            startActivity(intent)
        })

        heartButton.setOnClickListener(View.OnClickListener {
            addFavorite()
        })

        meetingButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_INSERT)
            intent.setData(CalendarContract.Events.CONTENT_URI)
            intent.putExtra(CalendarContract.Events.TITLE, "Meeting with " + petNamee!!.text.toString())
            intent.putExtra(CalendarContract.Events.DESCRIPTION, "Meeting with " + petNamee!!.text.toString())
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, petAssociation!!.text.toString())
            intent.putExtra(CalendarContract.Events.ALL_DAY, true)

            if(intent.resolveActivity(packageManager) != null){
                startActivity(intent)
            } else {
                Toast.makeText(this@PetProfile, "There is no app that can support this action", Toast.LENGTH_SHORT).show()
            }
            //open callendar app
        })

        loadProfile()
        checkHeartColor()
        checkMeeting()
    }

    private fun checkMeeting() {
        reference = FirebaseDatabase.getInstance().getReference()
        databaseReference = reference.child("Meetings")

        val user = mFirebaseAuth.currentUser
        val userreference = databaseReference?.child(user?.uid!!)

        userreference?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot1 in snapshot.children){
                    val petId : String = dataSnapshot1.child("petId").value.toString()
                    val petRef : DatabaseReference = reference.child("Pets").child(petId)
                    petRef.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(petSnapshot1: DataSnapshot) {
                            var name : TextView = findViewById(R.id.PetNameText)
                            var bookButton : Button = findViewById(R.id.BookMeeting_button)
                            var date : TextView = findViewById(R.id.MeetingDateText)
                            var bookText : TextView = findViewById(R.id.BookText)
                            var arrow : ImageView = findViewById(R.id.imageView2)
                            var meetingText : TextView = findViewById(R.id.MeetingText)

                            if(petSnapshot1.child("name").value.toString().equals(name.text)){
                                date.text = dataSnapshot1.child("date").value.toString()
                                meetingText.visibility = View.VISIBLE
                                bookButton.visibility = View.GONE
                                bookText.visibility = View.GONE
                                arrow.visibility = View.GONE
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun addFavorite(){
        reference = FirebaseDatabase.getInstance().getReference()
        databaseReference = reference.child("Pets")
        databaseReference2 = reference.child("Favorites")
        val user = mFirebaseAuth.currentUser
        val userreference = databaseReference2?.child(user?.uid!!)

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (petSnapshot in snapshot.children){
                        if(petSnapshot.child("name").value.toString().equals(petNamee!!.text)){
                            val key = petSnapshot.key.toString()

                            userreference.child(key).addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {


                                    if(key.equals(snapshot.child("petId").value.toString())){
                                        userreference.child(key).child("petId").removeValue()
                                        Toast.makeText(this@PetProfile, "Removed from favorites!",Toast.LENGTH_SHORT).show()
                                    } else {
                                        userreference.child(key).child("petId").setValue(key)
                                        Toast.makeText(this@PetProfile, "Added to favorites!",Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun checkHeartColor(){
        reference = FirebaseDatabase.getInstance().getReference()
        databaseReference = reference.child("Favorites")

        val user = mFirebaseAuth.currentUser
        val userreference = databaseReference?.child(user?.uid!!)

        userreference?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot1 in snapshot.children){
                    val petId : String = dataSnapshot1.child("petId").value.toString()
                    val petRef : DatabaseReference = reference.child("Pets").child(petId)
                    petRef.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(petSnapshot1: DataSnapshot) {
                            var name : TextView = findViewById(R.id.PetNameText)
                            var heartButton : ImageView = findViewById(R.id.favouritePetBtn)

                            if(petSnapshot1.child("name").value.toString().equals(name.text)){
                                heartButton.setImageResource(R.drawable.redheart)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getPetData(name: String) {
        reference = FirebaseDatabase.getInstance().getReference("Pets")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (petSnapshot in snapshot.children){

                        if(petSnapshot.child("name").value.toString().equals(name)){
                            petNamee!!.text = name
                            petAssociation!!.text = petSnapshot.child("association").value.toString()
                            petGender!!.text = petSnapshot.child("gender").value.toString()
                            petRace!!.text = petSnapshot.child("race").value.toString()
                            petFur!!.text = petSnapshot.child("fur").value.toString()
                            petCastration!!.text = petSnapshot.child("castration").value.toString()
                            petDescription!!.text = petSnapshot.child("description").value.toString()

                            if(petSnapshot.child("gender").value.toString().equals("Male")){
                                genderImage!!.setImageResource(R.drawable.male)
                            } else if (petSnapshot.child("gender").value.toString().equals("Female")){
                                genderImage!!.setImageResource(R.drawable.female)
                            }

                            try {
                                storageRef = storage.getReference("petPictures/" + petSnapshot.child("image").value.toString())
                                storageRefGallery  = storage.getReference("petPictures/" + petSnapshot.child("gallery").value.toString())

                                var localFile: File = File.createTempFile("tmpfile", ".png")
                                var localFileGallery: File = File.createTempFile("tmpfile", ".jpg")
                                storageRef.getFile(localFile).addOnSuccessListener {
                                    var img = findViewById<ImageView>(R.id.PetProfileImage)
                                    //gallery e avatar

                                    var bitmap: Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                                    img.setImageBitmap(bitmap)
                                }
                                storageRefGallery.getFile(localFileGallery).addOnSuccessListener {
                                    var imgGallery = findViewById<ImageView>(R.id.PetGallery)
                                    //gallery e avatar

                                    var bitmap: Bitmap = BitmapFactory.decodeFile(localFileGallery.absolutePath)
                                    imgGallery.setImageBitmap(bitmap)
                                }
                            } catch (e: IOException) {
                                Toast.makeText(
                                    this@PetProfile, "Unable to set profile picture!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
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
                        this@PetProfile, "Unable to set profile picture!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        })
    }
}