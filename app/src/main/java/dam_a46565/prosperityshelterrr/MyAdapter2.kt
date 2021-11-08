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

class MyAdapter2(private val discountList: ArrayList<Discount>) : RecyclerView.Adapter<MyAdapter2.MyViewHolder2>() {

    private var storage = FirebaseStorage.getInstance()
    private lateinit var storageRef: StorageReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter2.MyViewHolder2 {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_discounts, parent, false)
        return MyViewHolder2(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder2, position: Int) {

        val currentItem = discountList[position]

        holder.value.text = currentItem.value
        holder.location.text = currentItem.location

        try {
            storageRef = storage.getReference("discountPictures/" + currentItem.image)

            var localFile: File = File.createTempFile("tmpfile", ".png")
            storageRef.getFile(localFile).addOnSuccessListener {
                var bitmap: Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                holder.image.setImageBitmap(bitmap)
            }
        } catch (e: IOException) {

        }
    }

    override fun getItemCount(): Int {
        return discountList.size
    }

    class MyViewHolder2(itemView : View) : RecyclerView.ViewHolder(itemView){

        val value : TextView = itemView.findViewById(R.id.nameDiscountTv)
        val location : TextView = itemView.findViewById(R.id.locationDiscountTv)
        val image : ImageView = itemView.findViewById(R.id.avatarDiscountIv)
    }
}