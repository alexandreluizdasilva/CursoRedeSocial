package br.com.alexandre.cursoredesocial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.act_main.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)

        btn_entrar.setOnClickListener {
            signIn()
        }

        txt_account.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)


        }
    }

    private fun signIn() {
        val email = edit_email.text.toString()
        val password = edit_Password.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email e senha devem ser informados",
            Toast.LENGTH_LONG).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    Log.i("Teste", it.result.user.uid)

                //Lógica para abrir a tela MessagesActivity após logIn
                val intent = Intent(this@LoginActivity, MessagesActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }.addOnFailureListener {
                Log.e("Teste", it.message)
            }
    }
}
