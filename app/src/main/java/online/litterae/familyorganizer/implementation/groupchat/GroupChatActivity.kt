package online.litterae.familyorganizer.implementation.groupchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_group_chat.*
import online.litterae.familyorganizer.R

class GroupChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)
        val groupName = intent.getStringExtra("groupName")
        tv_group_chat.text = groupName
    }
}