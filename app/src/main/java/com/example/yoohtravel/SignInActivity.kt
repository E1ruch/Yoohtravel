package com.example.yoohtravel

import android.annotation.SuppressLint
import android.app.LauncherActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.yoohtravel.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.credentials.IdToken
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private lateinit var auth: FirebaseAuth


class SignInActivity : AppCompatActivity() {
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivitySignInBinding




    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth =  Firebase.auth

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {

                val account = task.getResult(ApiException::class.java)
                if (account != null){
                    firebaseAuthWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException){
                Log.d("MyLog", "Ошибка при подключении")
            }
        }
        binding.signinBtn.setOnClickListener{
        signInWithGoogle()
        }
        checkAuthState()
    }

    private fun getClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(this,gso)

    }

    private fun signInWithGoogle() {
        val signInClient = getClient()
        launcher.launch(signInClient.signInIntent)

    }

    private fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful){
                Log.d("MyLog", "Авторизация успешна")
                checkAuthState()
            }else{

                Log.d("MyLog","Ошибка авторизации")
            }
        }
    }

    private fun checkAuthState(){
        if(auth.currentUser != null){
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
    }
}