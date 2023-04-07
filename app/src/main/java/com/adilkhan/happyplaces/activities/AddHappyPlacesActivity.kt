package com.adilkhan.happyplaces.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.adilkhan.happyplaces.R
import com.adilkhan.happyplaces.databases.DatabaseHandler
import com.adilkhan.happyplaces.databinding.ActivityAddHappyPlacesBinding
import com.adilkhan.happyplaces.models.HappyPlaceModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


class AddHappyPlacesActivity : AppCompatActivity(), View.OnClickListener {
    private var addHappyPlacesBinding:ActivityAddHappyPlacesBinding? = null
    private var myCalender : Calendar = Calendar.getInstance()
    private var dateSetListener : DatePickerDialog.OnDateSetListener? = null
    private var saveImageToInternalStorage: Uri? = null
    private var mLatitude : Double = 0.0
    private var mLongitude : Double = 0.0
    private var mHappyPlaceDetails : HappyPlaceModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addHappyPlacesBinding = ActivityAddHappyPlacesBinding.inflate(layoutInflater)
        setContentView(addHappyPlacesBinding?.root)
        setSupportActionBar(addHappyPlacesBinding?.addHappyPlacesToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        addHappyPlacesBinding?.addHappyPlacesToolbar?.setNavigationOnClickListener { onBackPressed() }
        //set date picker dialog
         dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
             myCalender.set(Calendar.YEAR, year)
             myCalender.set(Calendar.MONTH, month)
             myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
             updateDateInView()
         }
        updateDateInView() // automatic set the current date
        // we call from main to adapter then this activity to edit to swipe so lets check
        // it has already contain some data or not if it is then populated these data

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS))
        {
            mHappyPlaceDetails = intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS)
            as HappyPlaceModel
        }
        // if it contain some data then it will be not null
        if(mHappyPlaceDetails!=null){
            supportActionBar?.title = "Edit Happy Place"
            addHappyPlacesBinding?.etTitle?.setText(mHappyPlaceDetails?.title.toString())
            addHappyPlacesBinding?.etDescription?.setText(mHappyPlaceDetails?.description.toString())
            addHappyPlacesBinding?.etDate?.setText(mHappyPlaceDetails?.date.toString())
            addHappyPlacesBinding?.etLocation?.setText(mHappyPlaceDetails?.location.toString())
            addHappyPlacesBinding?.btnSave?.text = "Update"
            saveImageToInternalStorage = Uri.parse(mHappyPlaceDetails!!.image)
            addHappyPlacesBinding?.ivPlaceImage?.setImageURI(saveImageToInternalStorage)

        }
        // this is added because when we click in this class , this will call on click method
        addHappyPlacesBinding?.etDate?.setOnClickListener(this)
        addHappyPlacesBinding?.tvAddImage?.setOnClickListener(this)
        addHappyPlacesBinding?.btnSave?.setOnClickListener(this)
    }
    // we have inherit onClickListener so we have to implement on Click
    // so far we have done individually for every listener but now we are going to do in
    // a different way
    // this time we will just go in onClick method by using when statement we will set the fields
    override fun onClick(view: View?) {
        when(view!!.id)
        {
            R.id.etDate ->{
                DatePickerDialog(this@AddHappyPlacesActivity, dateSetListener,
                myCalender.get(Calendar.YEAR),
                myCalender.get(Calendar.MONTH),
                myCalender.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            R.id.tv_add_image ->{
                val pictureDialog=AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("select photo from gallery",
                "capture photo from camera")
                pictureDialog.setItems(pictureDialogItems){
                    _,which->
                    when(which)
                    {
                        0-> choosePhotoFromGallery()
                        1-> takePhotoFromCamera()
                    }
                }
                pictureDialog.show()

            }
            R.id.btn_save ->{
                when{
                    addHappyPlacesBinding?.etTitle?.text.isNullOrEmpty()->{
                        Toast.makeText(this@AddHappyPlacesActivity,"Please enter the title",Toast.LENGTH_SHORT).show()

                    }
                    addHappyPlacesBinding?.etDescription?.text.isNullOrEmpty()->{
                        Toast.makeText(this@AddHappyPlacesActivity,"Please enter the description",Toast.LENGTH_SHORT).show()

                    }
                    addHappyPlacesBinding?.etLocation?.text.isNullOrEmpty()->{
                        Toast.makeText(this@AddHappyPlacesActivity,"Please enter the location",Toast.LENGTH_SHORT).show()

                    }
                    saveImageToInternalStorage==null->{
                        Toast.makeText(this@AddHappyPlacesActivity,"Add the image",Toast.LENGTH_SHORT).show()

                    }
                    else->{
                        val id = if(mHappyPlaceDetails==null) 0 else mHappyPlaceDetails!!.id
                        val happyPlaceModel = HappyPlaceModel(id,
                        addHappyPlacesBinding?.etTitle?.text.toString(),
                        saveImageToInternalStorage.toString(),
                            addHappyPlacesBinding?.etDescription?.text.toString(),
                            addHappyPlacesBinding?.etDate?.text.toString(),
                            addHappyPlacesBinding?.etLocation?.text.toString(),
                            mLatitude,
                            mLongitude
                        )
                        val dbHandler=DatabaseHandler(this)
                        // check for update or add
                        if(mHappyPlaceDetails==null)
                        {
                            val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)
                            if(addHappyPlace>0)
                            {
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }
                        else
                        {
                            if(mHappyPlaceDetails!=null)
                            {
                                val updateHappyPlace = dbHandler.updateHappyPlace(happyPlaceModel)
                                if(updateHappyPlace>0)
                                {
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                }
                            }
                        }


                    }
                }
            }
        }
    }
    // for setting the date we are using this method
    private fun updateDateInView()
    {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        addHappyPlacesBinding?.etDate?.setText(sdf.format(myCalender.time).toString())
    }
    private fun saveImageToInternalStorage(bitmap:Bitmap):Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY,Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")
        try {
            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }
        catch (e:IOException)
        {
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    public  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK)
        {
            if(requestCode== GALLERY_REQUEST)
            {
                if(data!=null)
                {
                    val contentUri = data.data
                    try {
                        val selectedImagesBitmap :Bitmap=
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)
                         saveImageToInternalStorage = saveImageToInternalStorage(selectedImagesBitmap)
                        Log.e("Save Image: ", "Path:: $saveImageToInternalStorage")
                       addHappyPlacesBinding?.ivPlaceImage?.setImageBitmap(selectedImagesBitmap)
                    }
                    catch (e:IOException)
                    {
                        e.printStackTrace()
                        Toast.makeText(this@AddHappyPlacesActivity, "Image not found",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else if(requestCode== CAMERA_REQUEST)
            {
                val selectedImageBitmap : Bitmap = data!!.extras!!.get("data") as Bitmap
                saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)
                Log.e("Save Image: ", "Path:: $saveImageToInternalStorage")
                addHappyPlacesBinding?.ivPlaceImage?.setImageBitmap(selectedImageBitmap)
            }
        }
    }
    // select photo from gallery
    private fun takePhotoFromCamera()
    {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener{
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if(report!!.areAllPermissionsGranted())
                    {
                        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intentCamera, CAMERA_REQUEST)
                    }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                    showRationaleDialogForPermission()
            }
        }).onSameThread().check()
    }
    private fun choosePhotoFromGallery()
    {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if(report!!.areAllPermissionsGranted())
                    {
                      val intentGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(intentGallery, GALLERY_REQUEST)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    showRationaleDialogForPermission()
                }
            }).onSameThread().check()
    }

    private fun showRationaleDialogForPermission()
    {
        AlertDialog.Builder(this).setMessage("It looks like you have turned off permission required" +
                " for this feature. It can be enabled under the " +
                "application settings").setPositiveButton("Go to setting"){
            _,_ ->
                  try {
                      val intentSetting = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                      val uri = Uri.fromParts("package",packageName, null)
                      intentSetting.data = uri
                      startActivity(intentSetting)
                  }
                  catch (e:ActivityNotFoundException)
                  {
                      e.printStackTrace()
                  }

        }.setNegativeButton("Cancel")
        {
            dialog,_-> dialog.dismiss()
        }.show()
    }
    companion object{
        private const val GALLERY_REQUEST=1
        private const val CAMERA_REQUEST = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
    }
}