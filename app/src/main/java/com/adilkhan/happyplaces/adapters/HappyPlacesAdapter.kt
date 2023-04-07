package com.adilkhan.happyplaces.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adilkhan.happyplaces.activities.AddHappyPlacesActivity
import com.adilkhan.happyplaces.activities.MainActivity
import com.adilkhan.happyplaces.databases.DatabaseHandler
import com.adilkhan.happyplaces.databinding.ActivityAddHappyPlacesBinding
import com.adilkhan.happyplaces.databinding.ItemHappyPlaceBinding
import com.adilkhan.happyplaces.models.HappyPlaceModel
import kotlinx.coroutines.MainScope

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
    // swipe to edit or delete
    fun notifyEditItem(activity:Activity, position: Int, requestCode:Int)
    {
        // from here open add happy place activity
        val intentAddHappyPlacesActivity = Intent(context, AddHappyPlacesActivity::class.java)
        intentAddHappyPlacesActivity.putExtra(MainActivity.EXTRA_PLACE_DETAILS, list[position])
        activity.startActivityForResult(intentAddHappyPlacesActivity,requestCode)
        notifyItemChanged(position)
    }
   // swipe to delete
    fun notifyDeleteItem(position: Int)
   {
        val dbHandler = DatabaseHandler(context)
       val isDeleted =  dbHandler.deleteHappyPlace(list[position])
       if(isDeleted>0)
       {    list.removeAt(position)
           notifyItemRemoved(position)
       }

    }
}