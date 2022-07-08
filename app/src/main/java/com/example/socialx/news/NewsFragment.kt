package com.example.socialx.news

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialx.R
import com.example.socialx.api.models.Articles
import com.example.socialx.api.models.News
import com.example.socialx.databinding.FragmentNewsBinding
import com.google.firebase.auth.FirebaseAuth

class NewsFragment : Fragment() {
    private lateinit var binding: FragmentNewsBinding //binding
    private var adapter: RecyclerView.Adapter<NewsAdapter.ViewHolder>? = null //adapter
    private lateinit var viewModel: NewsViewModel //viewmodel
    private lateinit var auth: FirebaseAuth //firebase auth
    private val newsList: ArrayList<Articles> = arrayListOf() //news
    private lateinit var progressDialog: ProgressDialog //progress dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(inflater, container, false) //setting up binding
        viewModel =
            ViewModelProvider(requireActivity())[NewsViewModel::class.java] //setting view model
        binding.recyclerview.layoutManager =
            LinearLayoutManager(requireContext()) //setting recycler view
        progressDialog = ProgressDialog(requireContext()) //setting progress bar
        auth = FirebaseAuth.getInstance() //initialising using firebase instance
        setHasOptionsMenu(true)

        //calling the function to GET news from API
        viewModel.getNews()
        //showing the progess dialog
        showProgressDialog("Getting News")

        //observing the changes in the variable which holds data, once we receive data, it is shown in recycler view
        viewModel.receivedNews.observe(requireActivity(), Observer { news ->
            if (news != null)
                putNewsIntoArray(news)
        })

        //observing the status of job to hide the progress bar
        viewModel.jobCompleted.observe(requireActivity(), Observer { status ->
            if (status)
                if (progressDialog.isShowing)
                    progressDialog.dismiss()
        })

        return binding.root
    }

    //putting data received in an array for sending to the adapter
    private fun putNewsIntoArray(news: News?) {
        if (news != null) {
            for (i in 0 until news.articles.size) {
                Log.i("NewsFragment", news.articles[i].toString())
                newsList.add(news.articles[i])
            }
            showInRecyclerView(newsList)
        }
    }

    //showing data in recycler view
    private fun showInRecyclerView(newsList: ArrayList<Articles>) {
        adapter = NewsAdapter(newsList)
        binding.recyclerview.adapter = adapter
    }

    //function to show progress dialog
    private fun showProgressDialog(message: String) {
        progressDialog.setMessage(message)
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    //setting up option menu with the menu with sign out icon
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //handling the press of sign out button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> signOut()
            else -> super.onOptionsItemSelected(item)
        }
    }

    //signing out the user and moving to login fragment
    private fun signOut(): Boolean {
        auth.signOut()
        Toast.makeText(activity, "Signed Out", Toast.LENGTH_SHORT).show()
        val action = NewsFragmentDirections.actionNewsFragmentToLoginFragment()
        findNavController().navigate(action)
        return true
    }
}