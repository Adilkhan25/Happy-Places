package com.adilkhan.happyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.adilkhan.happyplaces.adapters.HappyPlacesAdapter
import com.adilkhan.happyplaces.databases.DatabaseHandler
import com.adilkhan.happyplaces.databinding.ActivityMainBinding
import com.adilkhan.happyplaces.models.HappyPlaceModel

class MainActivity : AppCompatActivity() {
    private var mainBinding:ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding?.root)
        mainBinding?.fabAddHappyPlaces?.setOnClickListener {
            val intent = Intent(this, AddHappyPlacesActivity::class.java)
            startActivityForResult(intent,ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }
        getHappyPlacesListFromLocalDB()
    }
    private fun setUpHappyPlacesRecyclerView(happyPlaceList:ArrayList<HappyPlaceModel>)
    {
        mainBinding?.rvHappyPlaceList?.layoutManager = LinearLayoutManager(this)
        mainBinding?.rvHappyPlaceList?.setHasFixedSize(true)
        val happyPlaceAdapter = HappyPlacesAdapter(this,
        happyPlaceList)
        mainBinding?.rvHappyPlaceList?.adapter = happyPlaceAdapter
        // setting the click listener on the rv manually
        happyPlaceAdapter.setOnClickListener(object : HappyPlacesAdapter.OnClickListener{
            override fun ocClick(position: Int, model: HappyPlaceModel) {
                val intentPlaceDetails = Intent(this@MainActivity,
                    HappyPlaceDetailActivity::class.java)
                intentPlaceDetails.putExtra(EXTRA_PLACE_DETAILS, model)
                startActivity(intentPlaceDetails)
            }
        })

    }
    private fun getHappyPlacesListFromLocalDB()
    {
        val dbHandler = DatabaseHandler(this)
        val getHappyPlaceList = dbHandler.getHappyPlacesList()
        if(getHappyPlaceList.size>0)
        {
            mainBinding?.rvHappyPlaceList?.visibility = View.VISIBLE
            mainBinding?.tvNoRecordAvailable?.visibility = View.GONE
            setUpHappyPlacesRecyclerView(getHappyPlaceList)
        }
        else
        {
            mainBinding?.rvHappyPlaceList?.visibility = View.GONE
            mainBinding?.tvNoRecordAvailable?.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== ADD_PLACE_ACTIVITY_REQUEST_CODE)
        {
            if(resultCode==Activity.RESULT_OK)
            {
                getHappyPlacesListFromLocalDB()
            }
            else
            {
                Log.e("Activity","Cancelled or Back Pressed")
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mainBinding = null
    }
    companion object{
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        var EXTRA_PLACE_DETAILS = "happy place detail activity"
    }
}