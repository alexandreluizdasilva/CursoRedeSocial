package br.com.alexandre.cursoredesocial

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.act_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private var mSelectedUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_register)

        btn_register.setOnClickListener {

           createUser()
        }

        btn_select_photo.setOnClickListener {
            selectPhoto()
        }
    }

    private fun createUser(){

        val email = edit_email.text.toString()
        val password = edit_Password.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email e senha devem ser informados",
            Toast.LENGTH_LONG).show()
            return
        }

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
            if (it.isSuccessful) {
                Log.i("Teste", "UserID é ${it.result.user.uid}")

                saveUserInFirebase()
            }
        }
            .addOnFailureListener {
                Log.i("Teste", it.message, it)
        }
    }

    private fun saveUserInFirebase() {
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("images/${filename}")

        mSelectedUri?.let {
            ref.putFile(it)
                .addOnSuccessListener {
                    Log.i("Teste", it.toString())

                    val url = it.toString()
                    val name = edit_name.text.toString()
                    val uid = FirebaseAuth.getInstance().uid!!

                    val user = User(uid, name, url)

                    FirebaseFirestore.getInstance().collection("users")
                        .document(uid) // adicionando uid como chave
                        .set(user) // atribuindo o usuário a esta chave (uid)
                        .addOnSuccessListener {

                            //abre uma nova activity e coloca no topo
                            val intent = Intent(this@RegisterActivity,
                                MessagesActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                    Intent.FLAG_ACTIVITY_NEW_TASK

                            startActivity(intent)
                        }
                        .addOnFailureListener{
                            Log.e("Teste", it.message, it)
                        }
                }
        }
    }



    //Método que seleciona a foto
    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0) {
            mSelectedUri = data?.data
            Log.i("Teste", mSelectedUri.toString())

            val bitmap = MediaStore.Images.Media.getBitmap(
                contentResolver,
                mSelectedUri)
            img_photo.setImageBitmap(bitmap)
            btn_select_photo.alpha = 0f
        }
    }
}
