package com.sultonbek1547.firebaselogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sultonbek1547.firebaselogin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()

        val googleSigningClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()

        binding.btnSignIn.setOnClickListener {
            startActivityForResult(googleSigningClient.signInIntent, 1)
        }

        binding.btnSignOut.setOnClickListener {
            deleteUser()
            auth.signOut()
            googleSigningClient.signOut()

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("TAG", "onActivityResult: ${account.displayName}")
                fireBaseAuthWithGoogle(account.idToken)

            } catch (e: Exception) {
                Log.d("TAG", "onActivityResult: FAILURE ${e.message}")
            }

        }

    }

    private fun fireBaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("TAG", "fireBaseAuthWithGoogle: Sign in with credential SUCCESSFUL")
                val user = auth.currentUser
                Toast.makeText(this, "$user", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("TAG", "fireBaseAuthWithGoogle: Sign in with credential FAILED")
                Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()

            }
        }

    }

    private fun deleteUser() {

        /** |->ATTENTION<-|
         * Keep in mind that deleting a user account cannot be undone
         * and all data associated with the user will be permanently deleted.
         * So, make sure to prompt the user for confirmation before deleting their account.
         * */

        val user = auth.currentUser
        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // User account deleted successfully
                } else {
                    // An error occurred while deleting the user account
                    val exception = task.exception
                    // Handle the exception
                }
            }

    }

}