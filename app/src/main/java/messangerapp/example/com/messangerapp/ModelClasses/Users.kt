package messangerapp.example.com.messangerapp.ModelClasses

class Users {

    private var uid:String =""
    private var username:String =""
    private var profile:String =""
    private var cover:String =""
    private var status:String =""
    private var serach:String =""
    private var facebook:String =""
    private var Instagram:String =""
    private var Google:String =""

    constructor()
    constructor(
            uid: String,
            username: String,
            profile: String,
            cover: String,
            status: String,
            serach: String,
            facebook: String,
            Instagram: String,
            Google: String
    ) {
        this.uid = uid
        this.username = username
        this.profile = profile
        this.cover = cover
        this.status = status
        this.serach = serach
        this.facebook = facebook
        this.Instagram = Instagram
        this.Google = Google
    }
    fun getUID():String?{
        return uid
    }

    fun setUID(uid: String)
    {
        this.uid=uid
    }
    fun getUserName():String?{
        return username
    }
    fun setUserName(username: String)
    {
        this.username=username
    }
    fun getProfile():String?{
        return profile
    }

    fun setProfile(profile: String)
    {
        this.profile=profile
    }

    fun getCover():String?{
        return cover
    }

    fun setCover(cover: String)
    {
        this.cover=cover
    }

    fun getStatus():String?{
        return status
    }
    fun setStatus(status: String)
    {
        this.status=status
    }
    fun getSerach():String?{
        return serach
    }
    fun setSerach(serach: String)
    {
        this.serach=serach
    }
    fun getFacebook():String?{
        return facebook
    }
    fun setFacebook(facebook: String)
    {
        this.facebook=facebook
    }
    fun getInstagram():String?{
        return Instagram
    }
    fun setInstagram(Instagram: String)
    {
        this.Instagram=Instagram
    }
    fun getGoogle():String?{
        return Google
    }
    fun setGoogle(Google: String)
    {
        this.Google=Google
    }




}

