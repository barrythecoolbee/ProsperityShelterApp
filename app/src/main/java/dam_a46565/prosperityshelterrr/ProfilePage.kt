package dam_a46565.prosperityshelterrr

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Outline
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException

class ProfilePage : AppCompatActivity() {

    var username : TextView? = null

    private lateinit var mFirebaseAuth : FirebaseAuth
    var rootNode : FirebaseDatabase? = null
    private lateinit var reference: DatabaseReference
    private lateinit var databaseReference: DatabaseReference
    private lateinit var referenceUser: DatabaseReference
    private lateinit var petRecyclerView : RecyclerView
    private lateinit var petArrayList : ArrayList<Pet>

    private var storage = FirebaseStorage.getInstance()
    private lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        petRecyclerView = findViewById(R.id.pets_recyclerView)
        petRecyclerView.layoutManager = LinearLayoutManager(this)
        petRecyclerView.setHasFixedSize(true)
        petArrayList = arrayListOf<Pet>()


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
        username = findViewById(R.id.Username_text)
        //findViewById
        var homePage : ImageButton = findViewById(R.id.HomeButton)
        var calendarPage : ImageButton = findViewById(R.id.CallendarButton)
        var sponsorPage : ImageButton = findViewById(R.id.StarButton)
        var userProfile : ImageButton = findViewById(R.id.UserButton)
        var userProfile2 : ImageButton = findViewById(R.id.ProfileButton)
        var settingsPage : ImageButton = findViewById(R.id.SettingsButton)

        //Firebase
        rootNode = FirebaseDatabase.getInstance()
        reference = rootNode?.reference!!.child("Users")
        mFirebaseAuth = FirebaseAuth.getInstance()

        //SetOnClickListener
        homePage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ProfilePage, MainActivity::class.java)
            startActivity(intent)
        })

        calendarPage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ProfilePage, MeetingsPage::class.java)
            startActivity(intent)
        })

        sponsorPage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ProfilePage, ExtraPage::class.java)
            startActivity(intent)
        })

        userProfile.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ProfilePage, ProfilePage::class.java)
            startActivity(intent)
        })

        userProfile2.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ProfilePage, ProfilePage::class.java)
            startActivity(intent)
        })

        settingsPage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ProfilePage, SettingsPage::class.java)
            startActivity(intent)
        })

        loadProfile()
        getFavoriteData()
    }

    private fun getFavoriteData(){
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
                            val pet = petSnapshot1.getValue(Pet::class.java) //Encontra os id's
                            petArrayList.add(pet!!)

                            var adapter = MyAdapter(petArrayList)
                            petRecyclerView.adapter = adapter
                            adapter.setOnItemClickListener(object : MyAdapter.onItemClickListener{
                                override fun onItemClick(position: Int) {
                                    //Toast.makeText(this@MainActivity, "You Clicked on item no. $position", Toast.LENGTH_SHORT).show()
                                    if(!petArrayList[position].type.toString().equals("Wild")) {
                                        val intent = Intent(this@ProfilePage, PetProfile::class.java)
                                        intent.putExtra("name", petArrayList[position].name.toString())
                                        startActivity(intent)
                                    } else {
                                        val intent = Intent(this@ProfilePage, SponsorProfile::class.java)
                                        intent.putExtra("name", petArrayList[position].name.toString())
                                        startActivity(intent)
                                    }
                                }

                            })

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

    private fun loadProfile(){
        val user = mFirebaseAuth.currentUser
        val userreference = reference?.child(user?.uid!!)

        userreference?.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    username!!.text = snapshot.child("username").value.toString()

                    storageRef = storage.getReference("profilePictures/" + snapshot.child("profilePic").value.toString())

                    var localFile: File = File.createTempFile("tmpfile", ".jpeg")
                    storageRef.getFile(localFile).addOnSuccessListener {
                        var img = findViewById<ImageView>(R.id.profile_image)
                        var img2 = findViewById<ImageView>(R.id.ProfileImage)

                        var bitmap: Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                        img.setImageBitmap(bitmap)
                        img2.setImageBitmap(bitmap)
                    }
                } catch (e: IOException) {
                    Toast.makeText(
                        this@ProfilePage, "Unable to set profile picture!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        })
    }
}