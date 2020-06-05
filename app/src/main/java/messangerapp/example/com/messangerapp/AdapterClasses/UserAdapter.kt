package messangerapp.example.com.messangerapp.AdapterClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import messangerapp.example.com.messangerapp.AdapterClasses.UserAdapter.*
import messangerapp.example.com.messangerapp.ModelClasses.Users
import messangerapp.example.com.messangerapp.R


class UserAdapter(mContext:Context,mUsers: List<Users>,isChatCheck:Boolean) : RecyclerView.Adapter<ViewHolder?>()
{

    private val mContext:Context = mContext
    private val mUsers: List<Users> = mUsers
    private var isChatCheck:Boolean = isChatCheck

    override fun onCreateViewHolder( viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view:View=LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout,viewGroup,false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return mUsers.size

    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int)
    {
        val user:Users?=mUsers[i]
        holder.userNameTxt.text=user!!.getUserName()
        Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(holder.profileImageView)

    }


    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var userNameTxt: TextView = itemView.findViewById(R.id.username)
        var profileImageView: CircleImageView = itemView.findViewById(R.id.profile_image)
        var onlineImageView: CircleImageView = itemView.findViewById(R.id.image_offline)
        var offlineImageView: CircleImageView = itemView.findViewById(R.id.image_online)
        var lastMessageTxt: TextView = itemView.findViewById(R.id.message_last)

    }




}