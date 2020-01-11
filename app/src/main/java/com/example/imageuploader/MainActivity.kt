package com.example.imageuploader

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*

class MainActivity: AppCompatActivity(), View.OnClickListener{

    private val PICK_IMAGE_REQUEST = 1234




    private var filepath : Uri? = null

    internal var storage:FirebaseStorage?=null
    internal var storageReference:StorageReference?=null

    override fun onClick(p0:View)
    {
        if(p0== btnChoose)
        {
            showFileChooser()
        }
        else if(p0 == btnUpload)
        {
            uploadFile()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK &&
                data != null && data.data !=null)
        {
            filepath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filepath)
                imageView!!.setImageBitmap(bitmap)

        }catch (e:IOException)
            {
            e.printStackTrace()
            }
        }
    }

    private fun uploadFile() {
        if(filepath!=null)
        {
            val progessDialog = ProgressDialog(this)
            progessDialog.setTitle("Uploading ... ")
            progessDialog.show()

            val imageRef = storageReference!!.child("image/"+ UUID.randomUUID().toString())
            imageRef.putFile(filepath!!)
                .addOnSuccessListener {
                    progessDialog.dismiss()
                    Toast.makeText(applicationContext,"File Uploaded",Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener{
                    progessDialog.dismiss()
                    Toast.makeText(applicationContext,"File Failed to Upload",Toast.LENGTH_SHORT).show()

                }

                .addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred/taskSnapshot.totalByteCount
                    progessDialog.setMessage("Uploaded" + progress.toInt() + "%...")
                }
        }
    }

    private fun showFileChooser(){
        val intent = Intent()
        intent.type= "image/*"
        intent.action =  Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init firebase
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        //button
        btnChoose.setOnClickListener(this)
        btnUpload.setOnClickListener (this)
    }

}