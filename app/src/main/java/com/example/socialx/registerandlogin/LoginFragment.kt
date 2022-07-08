package com.example.socialx.registerandlogin

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.socialx.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding //binding
    private lateinit var firebaseAuth: FirebaseAuth //firebase auth
    private lateinit var googleSignInClient: GoogleSignInClient //google sign in client
    private lateinit var progressDialog: ProgressDialog //progress dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false) //binding the view
        firebaseAuth = FirebaseAuth.getInstance() //initialising using firebase instance
        progressDialog = ProgressDialog(requireContext()) //setting up progress bar

        //setting up google sign in
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("241410034475-sq9cd2cqvum2uonnajf3lge7bj18ctkd.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        //sign up button takes us to registration page
        binding.signUp.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        //sign up text takes us to registration page
        binding.signuptext.setOnClickListener {
            val navigation = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(navigation)
        }

        //bottom view validates the fields and logs in the user using email and password
        binding.bottomview.setOnClickListener {
            val email: String = binding.etEmail.text.toString()
            val password: String = binding.etPassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    activity,
                    "Please make sure to fill all the fields",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                showProgressDialog("Signing in")
                //login starts
                loginUser(email, password)
            }
        }

        //forgot password sends an email for resetting the password
        binding.forgotPassword.setOnClickListener {
            val email: String = binding.etEmail.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(activity, "Enter the email first", Toast.LENGTH_SHORT)
                    .show()
            } else {
                showProgressDialog("Sending password reset email")
                sendResetPasswordEmail(email)
            }
        }

        //tap on google icon to sign in using google
        binding.google.setOnClickListener {
            signInUsingGoogle()
        }

        return binding.root
    }

    //sign in using google
    private fun signInUsingGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    //onActivityResult allows us to select the account from which to log in
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                showProgressDialog("Signing in")
                val account = task.getResult(ApiException::class.java)!!
                Log.d("Login Fragment", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                if (progressDialog.isShowing)
                    progressDialog.dismiss()
                // Google Sign In failed, update UI appropriately
                Log.w("Login Fragment", "Google sign in failed", e)
            }
        }
    }

    //authenticates the user
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Login Fragment", "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    updateUI(user)
                } else {
                    if (progressDialog.isShowing)
                        progressDialog.dismiss()
                    // If sign in fails, display a message to the user.
                    Log.w("Login Fragment", "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    //once the authentication using google is done, user is moved to news fragment
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            if (progressDialog.isShowing)
                progressDialog.dismiss()
            val action = LoginFragmentDirections.actionLoginFragmentToNewsFragment()
            findNavController().navigate(action)
        }
    }

    //function which sends the email to reset the password
    private fun sendResetPasswordEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        activity,
                        "Reset password mail has been sent. Check your email!",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressDialog.dismiss()
                } else {
                    Toast.makeText(
                        activity,
                        "Fail to send reset password email!",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressDialog.dismiss()
                }
            }
    }

    //login the user using email and password and navigate to news fragment
    private fun loginUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        progressDialog.dismiss()
                        val action = LoginFragmentDirections.actionLoginFragmentToNewsFragment()
                        findNavController().navigate(action)

                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(
                            activity,
                            task.exception!!.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
    }

    //function to show progress dialog
    private fun showProgressDialog(message: String) {
        progressDialog.setMessage(message)
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    //sign in code
    companion object {
        const val RC_SIGN_IN = 1001
    }
}