package com.assignment.chaitanya.flows.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.assignment.chaitanya.R
import com.assignment.chaitanya.flows.pojo.BusinessDetails
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.near_by_item.view.*

class NearByAdapter(private val businessModelList: List<BusinessDetails>, private val mItemClicked: OnItemClicked)
    : RecyclerView.Adapter<NearByAdapter.MyView>() {

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyView {

        val itemView: View = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.near_by_item,
                        parent,
                        false)
        return MyView(itemView)
    }

    override fun onBindViewHolder(holder: MyView,
                                  position: Int) {
        val businessModel = businessModelList[position]
        holder.nameTv.text = businessModel.name
        holder.ratingTv.text = businessModel.rating.toString()
        if (businessModel.is_closed ) {
            holder.openingHoursTv.text = "Status - Open Now"
            holder.openingHoursTv.setTextColor(ContextCompat.getColor(holder.openingHoursTv.context,
                    android.R.color.holo_green_light))
        } else {
            holder.openingHoursTv.text =  "Status - Closed"
            holder.openingHoursTv.setTextColor(ContextCompat.getColor(holder.openingHoursTv.context,
                    android.R.color.white))
        }
        for (address in businessModel.location.display_address) {
            holder.businessAddress.text =  String.format("%s %s", holder.businessAddress.text.toString(), address)
        }
        Glide.with(holder.shopIv)
                .load(businessModel.image_url)
                .centerCrop()
                .placeholder(R.drawable.yelp_background)
                .into(holder.shopIv)

        holder.itemView.setOnClickListener {
            mItemClicked.onItemClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return businessModelList.size
    }

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        var nameTv: TextView = view.name_tv
        var ratingTv: TextView = view.rating_tv
        var openingHoursTv: TextView = view.opening_hours
        var businessAddress: TextView = view.business_address
        var shopIv: ImageView = view.shop_iv
    }
}

