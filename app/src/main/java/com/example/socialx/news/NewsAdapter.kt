package com.example.socialx.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialx.R
import com.example.socialx.api.models.Articles

class NewsAdapter(private val news: ArrayList<Articles>) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    // view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)

        return ViewHolder(view)
    }

    //binding the views with data
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = news[position]

        holder.source.text = itemsViewModel.source.name
        holder.publishedAt.text = itemsViewModel.publishedAt
        holder.title.text = itemsViewModel.description
        holder.content.text = itemsViewModel.content
        Glide.with(holder.itemView.context).load(itemsViewModel.urlToImage).into(holder.newsImage)
    }

    //item count
    override fun getItemCount(): Int {
        return news.size
    }

    //view holder class specifying the views to be used
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val source: TextView = itemView.findViewById(R.id.source)
        val title: TextView = itemView.findViewById(R.id.title)
        val content: TextView = itemView.findViewById(R.id.content)
        val publishedAt: TextView = itemView.findViewById(R.id.publishedAt)
        val newsImage: ImageView = itemView.findViewById(R.id.newsImage)
    }
}