package messangerapp.example.com.messangerapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ResultReceiver
import android.widget.Toast
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*
import messangerapp.example.com.messangerapp.AdapterClasses.ChatsAdapter
import messangerapp.example.com.messangerapp.ModelClasses.Chat
import messangerapp.example.com.messangerapp.ModelClasses.Users
import retrofit2.http.Url
import java.sql.RowId

class MessageChatActivity : AppCompatActivity() {

    var userIdVisit: String=""
    var firebaseUser: FirebaseUser?=null
    var chatsAdapter:ChatsAdapter? = null
    var mChatlist: List<Chat>? = null
    lateinit var  recycler_view_chats:RecyclerView
    var reference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        val toolbar: androidx.appcompat.widget.Toolbar=findViewById(R.id.toolbar_message_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent=Intent(this@MessageChatActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        intent=intent
        userIdVisit=intent.getStringExtra("visit_id")
        firebaseUser=FirebaseAuth.getInstance().currentUser

        recycler_view_chats = findViewById(R.id.recycler_view_chats)
        recycler_view_chats.setHasFixedSize(true)

        var linearLayoutManager=LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recycler_view_chats.layoutManager=linearLayoutManager

        val reference=FirebaseDatabase.getInstance().reference.child("Users").child(userIdVisit)
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val user: Users?=p0.getValue(Users::class.java)

                username_mchat.text= user!!.getUserName()
                Picasso.get().load(user.getProfile()).into(profile_image_mchat)
                retrieveMessages(firebaseUser!!.uid,userIdVisit,user.getProfile())

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        send_message_btn.setOnClickListener {
            val message=text_message.text.toString()
            if (message == "")
            {
                Toast.makeText(this@MessageChatActivity,"Please weite a message, first...", Toast.LENGTH_LONG).show()
            }
            else
            {
                sendMessageToUser(firebaseUser!!.uid,userIdVisit,message)
            }

            text_message.setText("")
        }

        attach_image_file_btn.setOnClickListener {
            val intent=Intent()
            intent.action=Intent.ACTION_GET_CONTENT
            intent.type="image/*"
            startActivityForResult(Intent.createChooser(intent,"Pick Image Origin"),438)
        }


        seenMessage(userIdVisit)

    }

    private fun retrieveMessages(senderId: String, receiverId: String?, receiverImageUrl: String?)
    {
        mChatlist = ArrayList()
        reference = FirebaseDatabase.getInstance().reference.child("Chats")
        reference!!.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                (mChatlist as ArrayList<Chat>).clear()
                for(snapshot in p0.children)
                {
                    val chat=snapshot.getValue(Chat::class.java)

                    if(chat!!.getReceiver().equals(senderId)  && chat.getSender().equals(receiverId)
                            || chat.getReceiver().equals(receiverId) && chat.getSender().equals(senderId))
                    {
                        (mChatlist as ArrayList<Chat>).add(chat)

                    }
                    chatsAdapter=ChatsAdapter(this@MessageChatActivity,(mChatlist as ArrayList<Chat>),receiverImageUrl!!)
                    recycler_view_chats.adapter=chatsAdapter

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

    }

    private fun sendMessageToUser(senderId: String, receiverId: String?, message: String)
    {
        val reference=FirebaseDatabase.getInstance().reference
        val messageKey=reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"]=senderId
        messageHashMap["message"]=message
        messageHashMap["receiver"]=receiverId
        messageHashMap["isseen"]=false
        messageHashMap["url"]=""
        messageHashMap["messageId"]=messageKey
        reference.child("Chats")
                .child(messageKey!!)
                .setValue(messageHashMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                    {
                        val chatsListRefrence=FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid).child(userIdVisit)
                        chatsListRefrence.addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(p0: DataSnapshot) {
                                if(!p0.exists())
                                {
                                    chatsListRefrence.child("id").setValue(userIdVisit)
                                }
                                val chatsListReceiverRef=FirebaseDatabase.getInstance()
                                        .reference.child("ChatList")
                                        .child(userIdVisit)
                                        .child(firebaseUser!!.uid)
                                chatsListReceiverRef.child("id").setValue(firebaseUser!!.uid)
                            }

                            override fun onCancelled(p0: DatabaseError) {

                            }
                        })

                        val reference=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
                    }
                }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==438 && resultCode==RESULT_OK && data!=null && data!!.data!=null)
        {
            val progressBar = ProgressDialog(this)
            progressBar.setMessage("image is uploading,please wait....")
            progressBar.show()
            val fileUri=data.data
            val storageReference=FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref=FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            var uploadTask: StorageTask<*>
            uploadTask=filePath.putFile(fileUri!!)
            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if(task.isSuccessful)
                {
                    task.exception?.let {
                        throw it
                    }


                }
                return@Continuation filePath.downloadUrl

            }).addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    val downloadUrl= task.result
                    val url=downloadUrl.toString()

                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"]=firebaseUser!!.uid
                    messageHashMap["message"]="sent you a image."
                    messageHashMap["receiver"]=userIdVisit
                    messageHashMap["isseen"]=false
                    messageHashMap["url"]=url
                    messageHashMap["messageId"]=messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)

                }
            }

        }
    }

    var seenListener:ValueEventListener? = null

    private fun seenMessage(userId: String)
    {
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        seenListener = reference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                {

                }
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (DataSnapshot in p0.children)
                {
                    val chat= DataSnapshot.getValue(Chat::class.java)

                    if(chat!!.getReceiver().equals(firebaseUser!!.uid) && chat!!.getSender().equals(userId))
                    {
                        val hashMap =  HashMap<String,Any>()
                        hashMap["isseen"]=true
                        DataSnapshot.ref.updateChildren(hashMap)
                    }
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListener!!)
    }

}