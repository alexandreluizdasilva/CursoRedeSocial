package br.com.alexandre.cursoredesocial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.act_chat.*

class ChatActivity : AppCompatActivity() {

    private lateinit var mAdapter: GroupAdapter<ViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_chat)

        mAdapter = GroupAdapter()
        list_chat.adapter = mAdapter

        mAdapter.add(MessageItem(true))
        mAdapter.add(MessageItem(false))
        mAdapter.add(MessageItem(false))
        mAdapter.add(MessageItem(true))
        mAdapter.add(MessageItem(true))
        mAdapter.add(MessageItem(false))
    }

    private inner class MessageItem(private val mLeft: Boolean)
        : Item<ViewHolder>(){

        override fun getLayout(): Int {
            return if (mLeft) R.layout.item_from_message
            else R.layout.item_to_message
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {

        }
    }
}
