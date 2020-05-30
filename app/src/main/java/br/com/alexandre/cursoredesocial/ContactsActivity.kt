package br.com.alexandre.cursoredesocial

import android.content.AbstractThreadedSyncAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.act_contacts.*
import kotlinx.android.synthetic.main.item_user.view.*

class ContactsActivity : AppCompatActivity() {

    private lateinit var mAdapter: GroupAdapter<ViewHolder>

    //Ação para enviar o usuário que queremos conversar para outra Activity
    companion object {
        val USER_KEY = "user_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_contacts)

        mAdapter = GroupAdapter()
        list_contact.adapter = mAdapter

        //cria evento para abrir tela de chat
        mAdapter.setOnItemClickListener { item, view ->
            val intent = Intent(this@ContactsActivity, ChatActivity::class.java)
            val userItem: UserItem = item as UserItem

            intent.putExtra(USER_KEY, userItem.mUser)
            startActivity(intent)
        }


        fetchUsers()
    }

    private inner class UserItem(internal val mUser: User)
        : Item<ViewHolder>(){
        override fun getLayout(): Int {

            return R.layout.item_user
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {

            viewHolder.itemView.txt_username.text = mUser.name
            Picasso.get()
                .load(mUser.url)
                .into(viewHolder.itemView.img_photo)
        }
    }

    private fun fetchUsers(){
        FirebaseFirestore.getInstance().collection("/users/").addSnapshotListener { snapshot, exception ->
            exception?.let {
                Log.i("Teste", it.message)
                return@addSnapshotListener
                }
            snapshot?.let {
                for(doc in snapshot) {
                    val user = doc.toObject(User::class.java)
                    Log.i("Teste", "user ${user.uid}, ${user.name}")
                    mAdapter.add(UserItem(user))
                }
            }
        }
    }
}
