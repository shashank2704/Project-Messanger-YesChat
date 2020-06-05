package messangerapp.example.com.messangerapp.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.view.*
import messangerapp.example.com.messangerapp.ModelClasses.Users
import messangerapp.example.com.messangerapp.R

class SettingsFragment : Fragment()
{
    var usersRefernce: DatabaseReference?=null
    var firebaseUser: FirebaseUser?=null
    private val RequestCode= 438
    private  var imageUri: Uri?=null
    private var storageRef:StorageReference?=null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_settings, container, false)


        firebaseUser= FirebaseAuth.getInstance().currentUser
        usersRefernce= FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")

        usersRefernce!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    val user:Users?=p0.getValue(Users::class.java)

                    if(context!=null)
                    {
                        view.username_settings.text = user!!.getUserName()
                        Picasso.get().load(user.getProfile()).into(view.profile_image_settings)
                        Picasso.get().load(user.getCover()).into(view.cover_image_settings)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        view.profile_image_settings.setOnClickListener {
            pickImage()
        }

        view.cover_image_settings.setOnClickListener {
            pickImage()
        }

        return view
    }




    private fun pickImage() {
        val intent =Intent()
        intent.type="image/*"
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==RequestCode && resultCode== Activity.RESULT_OK && data!!.data !=null)
        {
            imageUri=data.data
            Toast.makeText(context,"Uploading....",Toast.LENGTH_LONG).show()
            uploadImageToDatabase()


        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("image is uploading,please wait....")
        progressBar.show()

        if(imageUri!=null)
        {
            val
        }
    }

}