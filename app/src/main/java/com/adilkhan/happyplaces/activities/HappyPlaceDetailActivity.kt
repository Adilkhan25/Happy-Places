package com.adilkhan.happyplaces.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adilkhan.happyplaces.R
import com.adilkhan.happyplaces.databinding.ActivityHappyPlaceDetailBinding
import com.adilkhan.happyplaces.models.HappyPlaceModel

class HappyPlaceDetailActivity : AppCompatActivity() {
    private var happyPlaceDetailBinding : ActivityHappyPlaceDetailBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        happyPlaceDetailBinding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(happyPlaceDetailBinding?.root)

        var happyPlaceDetailModel : HappyPlaceModel? = null
        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS))
        {
             happyPlaceDetailModel = intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel
        }
        if(happyPlaceDetailModel!=null)
        {
            setSupportActionBar(happyPlaceDetailBinding?.happyPlaceDetailToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = happyPlaceDetailModel.title
            happyPlaceDetailBinding?.happyPlaceDetailToolbar?.setNavigationOnClickListener { onBackPressed() }
            happyPlaceDetailBinding?.ivPlaceImage?.setImageURI(Uri.parse(happyPlaceDetailModel.image))
            happyPlaceDetailBinding?.tvDescription?.text = happyPlaceDetailModel.description
            happyPlaceDetailBinding?.tvLocation?.text = happyPlaceDetailModel.location
        }



    }
}