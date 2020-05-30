package br.com.alexandre.cursoredesocial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth

class MessagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_messages)

        verifyAuthentication()
    }

    //checagem para verificar se estamos logados ou não
    private fun verifyAuthentication() {
        if (FirebaseAuth.getInstance().uid == null) {
            val intent = Intent(this@MessagesActivity, LoginActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.message_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                verifyAuthentication()
            }
            R.id.contacts -> {
                //logando no contacts
                val intent = Intent(this@MessagesActivity, ContactsActivity::class.java)
                startActivity(intent)

                Log.i("Teste", "contacts clicked")
            }
        }

        return super.onOptionsItemSelected(item)
    }

}
