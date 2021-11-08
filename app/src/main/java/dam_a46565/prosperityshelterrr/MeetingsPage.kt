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

class MeetingsPage : AppCompatActivity() {

    private lateinit var mFirebaseAuth : FirebaseAuth
    var rootNode : FirebaseDatabase? = null
    private lateinit var reference: DatabaseReference
    private lateinit var databaseReference: DatabaseReference
    private lateinit var petRecyclerView : RecyclerView
    private lateinit var petArrayList : ArrayList<Pet>

    private var storage = FirebaseStorage.getInstance()
    private lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meetings_page)

        petRecyclerView = findViewById(R.id.petsMeeting_recyclerView)
        petRecyclerView.layoutManager = LinearLayoutManager(this)
        petRecyclerView.setHasFixedSize(true)
        petArrayList = arrayListOf<Pet>()
        //getPetData()


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

        //Firebase
        rootNode = FirebaseDatabase.getInstance()
        reference = rootNode?.reference!!.child("Users")
        mFirebaseAuth = FirebaseAuth.getInstance()

        //SetOnClickListener
        homePage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MeetingsPage, MainActivity::class.java)
            startActivity(intent)
        })

        calendarPage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MeetingsPage, MeetingsPage::class.java)
            startActivity(intent)
        })

        sponsorPage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MeetingsPage, ExtraPage::class.java)
            startActivity(intent)
        })

        userProfile.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MeetingsPage, ProfilePage::class.java)
            startActivity(intent)
        })

        userProfile2.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MeetingsPage, ProfilePage::class.java)
            startActivity(intent)
        })

        settingsPage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MeetingsPage, SettingsPage::class.java)
            startActivity(intent)
        })

        loadProfile()
        getMeetingData()
    }

    private fun getMeetingData(){
        reference = FirebaseDatabase.getInstance().getReference()
        databaseReference = reference.child("Meetings")
        val user = mFirebaseAuth.currentUser
        val userreference = databaseReference?.child(user?.uid!!)

        userreference?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot1 in snapshot.children){
                    val petId : String = dataSnapshot1.child("petId").value.toString()
                    Log.d("BBBBBBBBBBB", dataSnapshot1.child("date").value.toString())

                    val petRef : DatabaseReference = reference.child("Pets").child(petId)
                    petRef.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(petSnapshot1: DataSnapshot) {
                            val pet = petSnapshot1.getValue(Pet::class.java) //Encontra os id's
                            if(!petSnapshot1.child("type").value.toString().equals("Wild")) {
                                petArrayList.add(pet!!)
                            }

                            var adapter = MyAdapter3(petArrayList)
                            petRecyclerView.adapter = adapter
                            adapter.setOnItemClickListener(object : MyAdapter3.onItemClickListener{
                                override fun onItemClick(position: Int) {
                                    //Toast.makeText(this@MainActivity, "You Clicked on item no. $position", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@MeetingsPage, PetProfile::class.java)
                                    intent.putExtra("name", petArrayList[position].name.toString())
                                    startActivity(intent)
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
                    storageRef = storage.getReference("profilePictures/" + snapshot.child("profilePic").value.toString())

                    var localFile: File = File.createTempFile("tmpfile", ".jpeg")
                    storageRef.getFile(localFile).addOnSuccessListener {
                        var img = findViewById<ImageView>(R.id.profile_image)

                        var bitmap: Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                        img.setImageBitmap(bitmap)
                    }
                } catch (e: IOException) {
                    Toast.makeText(
                        this@MeetingsPage, "Unable to set profile picture!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        })
    }
}