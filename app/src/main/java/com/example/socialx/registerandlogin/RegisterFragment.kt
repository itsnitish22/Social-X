package com.example.socialx.registerandlogin

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.socialx.databinding.FragmentRegisterBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding //bindings
    private lateinit var auth: FirebaseAuth //firebase auth
    private lateinit var progressDialog: ProgressDialog //progress dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false) //setting up binding
        auth = FirebaseAuth.getInstance() //setting up firebase by initialising it
        progressDialog = ProgressDialog(requireContext()) //setting up progress bar

        //click on login text to navigate to login framgment
        binding.logintext.setOnClickListener {
            val navigation = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            findNavController().navigate(navigation)
        }

        //click on sign in to navigate to login fragment
        binding.signIn.setOnClickListener {
            val navigation = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            findNavController().navigate(navigation)
        }

        //click on bottom view and fields will be validated and the user will be registered on firebase
        binding.bottomview.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val phone = binding.etphone.text.toString()
            val password = binding.etPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(activity, "Please make sure to fill all fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                showProgressDialog("Registering user")
                //authentication starts
                authenticateEmail(email, password)
            }
        }

        return binding.root
    }

    // register the user on firebase using email and password
    private fun authenticateEmail(email: String, password: String) {
        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
            OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    val currentUser = Firebase.auth.currentUser
                    Toast.makeText(
                        activity,
                        "Registration successful. Go to Login page",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressDialog.dismiss()
                } else {
                    Toast.makeText(activity, "Some error occurred", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()

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
}