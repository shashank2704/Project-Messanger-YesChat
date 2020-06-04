package messangerapp.example.com.messangerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import java.sql.DatabaseMetaData

class RegisterActivity : AppCompatActivity()
{
    private  lateinit var mAuth:FirebaseAuth
    private var firebaseUserID:String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar: Toolbar =findViewById(R.id.toolbar_register)
        setSupportActionBar(toolbar)
        supportActionBar!!.title="Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent= Intent(this@RegisterActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth= FirebaseAuth.getInstance()

        register_btn.setOnClickListener { it: View? ->
            registerUser()
        }

    }

    private fun registerUser() {
        val username:String=username_register.text.toString()
        val email:String=email_register.text.toString()
        val password:String=password_register.text.toString()

        if(username == "")
        {
            Toast.makeText(this@RegisterActivity,"Please Write Username.", Toast.LENGTH_LONG).show()
        }
        else if(email == "")
        {
            Toast.makeText(this@RegisterActivity,"Please Write Email.", Toast.LENGTH_LONG).show()
        }
        else if(password == "")
        {
            Toast.makeText(this@RegisterActivity,"Please Write Password.", Toast.LENGTH_LONG).show()
        }
        else
        {
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                task->
                if(task.isSuccessful)
                {
                    Log.d("RegisterActivity","Successfull Auth")
                    firebaseUserID=mAuth.currentUser!!.uid
                     val refUsers= FirebaseDatabase.getInstance().getReference("/Users/$firebaseUserID")

                    val user=User(firebaseUserID,username_register.text.toString())

                    refUsers.setValue(user)
                            .addOnCompleteListener {
                        task ->
                        if(task.isSuccessful)
                        {
                            Log.d("RegisterActivity","Successfull Registered")
                            val intent=Intent(this@RegisterActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }

                    }
                            .addOnFailureListener {
                                Log.d("RegisterActivity","Failed Reg ${it.message}")
                            }



                  /*  val userHashMap= HashMap<String,Any>()
                    userHashMap["uid"]=firebaseUserID
                    userHashMap["username"]=username
                    userHashMap["profile"]="https://firebasestorage.googleapis.com/v0/b/messangerapp-8602d.appspot.com/o/profile.png?alt=media&token=f96f755d-62fb-4911-b1f6-fa77060a9cd0"
                    userHashMap["cover"]="https://firebasestorage.googleapis.com/v0/b/messangerapp-8602d.appspot.com/o/cover.png?alt=media&token=f9c09483-d088-4fa9-a8e8-79b12784746d"
                    userHashMap["status"]="offline"
                    userHashMap["search"]=username.toLowerCase()
                    userHashMap["facebook"]="https://www.facebook.com"
                    userHashMap["Instagram"]="https://www.instagram.com"
                    userHashMap["Google"]="https://www.google.com"*/


                    //refUsers.updateChildren(userHashMap)

                }
                else
                {
                    Toast.makeText(this@RegisterActivity,"Please Check Details: "+ task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}

class User(val uid: String,val Username:String,val status: String = "Offline"){
    constructor(): this("","")
}