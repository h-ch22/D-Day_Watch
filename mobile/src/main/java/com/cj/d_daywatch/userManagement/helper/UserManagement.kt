package com.cj.d_daywatch.userManagement.helper

import android.content.Context
import androidx.activity.result.ActivityResult
import com.cj.d_daywatch.frameworks.helper.DataStoreUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class UserManagement {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    companion object{
        private var email: String? = null
        private var signInMethod: Int? = null
    }

    fun signInWithGoogle(activityResult: ActivityResult, onSuccess: (String) -> Unit, onFail: () -> Unit){
        try{
            val account = GoogleSignIn.getSignedInAccountFromIntent(activityResult.data)
                .getResult(ApiException::class.java)

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            auth.signInWithCredential(credential).addOnCompleteListener {
                if(it.isSuccessful){
                    email = auth.currentUser?.email
                    signInMethod = 0

                    onSuccess(account.idToken!!)
                } else{
                    onFail()
                }
            }
        } catch(e: Exception){
            e.printStackTrace()
            onFail()
        }
    }

    fun signInWithToken(token: String, completion: (Boolean) -> Unit){
        val credential = GoogleAuthProvider.getCredential(token, null)

        auth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                email = auth.currentUser?.email
                signInMethod = 0

                completion(true)
                return@addOnCompleteListener
            } else{
                completion(false)
                return@addOnCompleteListener
            }
        }.addOnFailureListener {
            it.printStackTrace()
            completion(false)
        }
    }

    fun signIn(email: String, password: String, completion: (Boolean) -> Unit){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            Companion.email = auth.currentUser?.email
            signInMethod = 1

            completion(it.isSuccessful)
        }.addOnFailureListener {
            it.printStackTrace()
            completion(false)
        }
    }

    fun signUp(email: String, password: String, completion: (Boolean) -> Unit){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            Companion.email = auth.currentUser?.email
            signInMethod = 1

            completion(it.isSuccessful)
        }.addOnFailureListener {
            it.printStackTrace()
            completion(false)
        }
    }

    fun signOut(completion: (Boolean) -> Unit){
        try {
            auth.signOut()
            completion(true)
            return
        } catch(e: Exception){
            e.printStackTrace()
            completion(false)
            return
        }
    }

    fun cancelMembership(completion: (Boolean) -> Unit){
        db.collection("D_Day").document(auth.currentUser?.uid ?: "").delete().addOnCompleteListener {
            if(it.isSuccessful){
                auth.currentUser?.delete()?.addOnCompleteListener {
                    completion(it.isSuccessful)
                    return@addOnCompleteListener
                }?.addOnFailureListener {
                    it.printStackTrace()
                    completion(false)
                    return@addOnFailureListener
                }
            } else{
                completion(false)
                return@addOnCompleteListener
            }
        }.addOnFailureListener {
            it.printStackTrace()
            completion(false)
            return@addOnFailureListener
        }
    }
}