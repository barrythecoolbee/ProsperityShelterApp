package dam_a46565.prosperityshelterrr

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException

class MyAdapter(private val petList: ArrayList<Pet>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private var storage = FirebaseStorage.getInstance()
    private lateinit var storageRef: StorageReference

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener : onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_pets, parent, false)
        return MyViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = petList[position]

        holder.name.text = currentItem.name
        holder.association.text = currentItem.association
        holder.description.text = currentItem.description

        try {
            storageRef = storage.getReference("petPictures/" + currentItem.image)

            var localFile: File = File.createTempFile("tmpfile", ".png")
            storageRef.getFile(localFile).addOnSuccessListener {
                var bitmap: Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                holder.image.setImageBitmap(bitmap)
            }
        } catch (e: IOException) {

        }
    }

    override fun getItemCount(): Int {
        return petList.size
    }

    class MyViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView){

        val name : TextView = itemView.findViewById(R.id.namePetTv)
        val association : TextView = itemView.findViewById(R.id.associationPetTv)
        val description : TextView = itemView.findViewById(R.id.descriptionPetTv)
        val image : ImageView = itemView.findViewById(R.id.avatarPetIv)

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }
}