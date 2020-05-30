package br.com.alexandre.cursoredesocial

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//Essa classe tem o objetivo só de armazenar dados
//data class User(val uid: String, val name: String, val url: String)
@Parcelize
data class User(val uid: String = "", val name: String = "", val url: String = "" ) : Parcelable