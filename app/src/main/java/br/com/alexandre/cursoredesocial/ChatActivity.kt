package br.com.alexandre.cursoredesocial

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.act_chat.*
import kotlinx.android.synthetic.main.item_from_message.view.*
import kotlinx.android.synthetic.main.item_to_message.*
import kotlinx.android.synthetic.main.item_to_message.view.*
import kotlinx.android.synthetic.main.item_user.*

class ChatActivity : AppCompatActivity() {

    private lateinit var mAdapter: GroupAdapter<ViewHolder>

    lateinit var mUser: User //Guarda uma referencia ao objeto User

    private var mMe: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_chat)

        //val user = intent.extras.getParcelable<User>(ContactsActivity.USER_KEY)
        mUser = intent.extras.getParcelable<User>(ContactsActivity.USER_KEY)
        Log.i("Teste", "username ${mUser.name}")
        supportActionBar?.title = mUser.name

        mAdapter = GroupAdapter()
        list_chat.adapter = mAdapter

       btn_send.setOnClickListener {
        sendMessage()
       }

        FirebaseFirestore.getInstance().collection("/users")
            .document(FirebaseAuth.getInstance()
                .uid.toString())
            .get().addOnSuccessListener {
                mMe = it.toObject(User::class.java)
                fetchMessages()
            }
    }

    private fun fetchMessages(){
        mMe?.let {
            val fromId = it.uid.toString()
            val toId = mUser.uid.toString()

            FirebaseFirestore.getInstance().collection("/conversations")
                .document(fromId)
                .collection(toId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    querySnapshot?.documentChanges?.let {
                        for (doc in it) {
                            when(doc.type) {
                                DocumentChange.Type.ADDED -> {
                                    val message =
                                        doc.document.toObject(Message::class.java)
                                    mAdapter.add(MessageItem(message))
                                }
                            }
                        }
                    }
                }
        }
    }

    private fun sendMessage(){
        val text = edit_msg.text.toString()

        edit_msg.text = null //apaga o EditText após clicar no botão enviar. Permitindo escrever ma nova mensagem

        val fromId = FirebaseAuth.getInstance().uid.toString()
        val toId = mUser.uid
        val timestamp = System.currentTimeMillis()

        val message = Message(text = text,
            timestamp = timestamp, toId = toId, fromId = fromId) //cria o Objeto Message

        if (!message.text.isEmpty()) {
            //Cria uma nova coleção de chamada CONVERSATIONS q terá basicamente documentos dos remententes
            FirebaseFirestore.getInstance().collection("/conversations")
                    //Quando o outro usuário abrir o app poder visualizar os balões de quem recebeu a msg
                .document(fromId)
                .collection(toId)
                .add(message)
                .addOnSuccessListener {
                    Log.i("Teste", it.id)
                }
                .addOnFailureListener {
                    Log.e("Teste", it.message)
                }

            FirebaseFirestore.getInstance().collection("/conversations")
                .document(toId)
                .collection(fromId)
                .add(message)
                .addOnSuccessListener {
                    Log.i("Teste", it.id)
                }
                .addOnFailureListener {
                    Log.e("Teste", it.message)
                }
        }

    }

    private inner class MessageItem(private val mMessage: Message)
        : Item<ViewHolder>(){

        override fun getLayout(): Int {
            return if (mMessage.fromId == FirebaseAuth.getInstance().uid)
                R.layout.item_from_message
            else
                R.layout.item_to_message
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {

            if (mMessage.fromId == FirebaseAuth.getInstance().uid) {
                viewHolder.itemView.txt_msg_from.text = mMessage.text
                Picasso.get().load(mMe?.url).into(viewHolder.itemView.img_msg_from)
            }else{
                viewHolder.itemView.txt_msg.text = mMessage.text
                Picasso.get().load(mUser.url).into(viewHolder.itemView.img_msg)
            }
        }
    }
}
