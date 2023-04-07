package com.adilkhan.happyplaces.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adilkhan.happyplaces.databinding.ActivityAddHappyPlacesBinding
import com.adilkhan.happyplaces.databinding.ItemHappyPlaceBinding
import com.adilkhan.happyplaces.models.HappyPlaceModel

open class HappyPlacesAdapter(
    private val context: Context,
    private var list:ArrayList<HappyPlaceModel>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener:OnClickListener?=null

    inner class MyViewHolder(private val itemBinding: ItemHappyPlaceBinding):RecyclerView.ViewHolder(itemBinding.root)
    {
        fun bindItem(model:HappyPlaceModel)
        {
            itemBinding.tvName.text = model.title
            itemBinding.tvDescription.text = model.description
            itemBinding.cvImage.setImageURI(Uri.parse(model.image))
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return MyViewHolder(ItemHappyPlaceBinding.inflate(LayoutInflater.from(context),
                parent,false)
                )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val model = list[position]
        if(holder is MyViewHolder)
        {
            holder.bindItem(model)
            holder.itemView.setOnClickListener{
                if(onClickListener!=null)
                {
                    onClickListener!!.ocClick(position,model)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun setOnClickListener(onClickListener: OnClickListener)
    {
        this.onClickListener = onClickListener
    }
    interface OnClickListener{
        fun ocClick(position: Int,model:HappyPlaceModel)
    }

}