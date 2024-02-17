package com.yildirimtechnologies.galeriyeerismevefotorafarayuzu

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.yildirimtechnologies.galeriyeerismevefotorafarayuzu.databinding.ActivityYuklemeBinding
import java.io.ByteArrayOutputStream

class YuklemeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityYuklemeBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionizinlauncher: ActivityResultLauncher<String>
    private var secilibitmap: Bitmap? = null
    private lateinit var database: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYuklemeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        database = this.openOrCreateDatabase("ürünler", MODE_PRIVATE,null)
        launcherlarikayit()
        val intent = intent
        val bilgi = intent.getStringExtra("bilgi")
        if (bilgi.equals("yeni")){
            binding.markaText.setText("")
            binding.modelText.setText("")
            binding.fiyatText.setText("")
            binding.button.visibility = View.VISIBLE
            binding.imageView.setImageResource(R.drawable.resimekle)
        }else{
            binding.button.visibility = View.INVISIBLE
            val seciliid = intent.getIntExtra("id",1)
            val cursor = database.rawQuery("SELECT * FROM ürünler WHERE id = ?", arrayOf(seciliid.toString()))
            val markaIx = cursor.getColumnIndex("markaismi")
            val modelIx = cursor.getColumnIndex("modelismi")
            val fiyatIx = cursor.getColumnIndex("fiyattutarı")
            val imageIx = cursor.getColumnIndex("image")
            while (cursor.moveToNext()){
                binding.markaText.setText(cursor.getString(markaIx))
                binding.modelText.setText(cursor.getString(modelIx))
                binding.fiyatText.setText(cursor.getString(fiyatIx))
                val byteArray = cursor.getBlob(imageIx)
                val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                binding.imageView.setImageBitmap(bitmap)
            }
            cursor.close()
        }
    }
    fun kayitButonu(view: View) {
        val markaismi = binding.markaText.text.toString()
        val modelismi = binding.modelText.text.toString()
        val fiyatdegeri = binding.fiyatText.text.toString()
        if (secilibitmap != null){
            val kucukBitmap = kucukbitmapolustur(secilibitmap!!,300)
            //Görseli veriye (1'le0'a )çevirme (veri tabanına kaydetmek için.)
            val outputStream = ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray = outputStream.toByteArray()
            try {
                //val database = this.openOrCreateDatabase("ürünler", MODE_PRIVATE,null)
                database.execSQL("CREATE TABLE IF NOT EXISTS ürünler(id INTEGER PRIMARY KEY, markaismi VARCHAR,modelismi VARCHAR,fiyattutarı,VARCHAR ,image BLOB)")
                val sqlString = "INSERT INTO ürünler(markaismi,modelismi,fiyattutarı,image) VALUES (?, ?, ?, ?)"
                val statement = database.compileStatement(sqlString)
                statement.bindString(1,markaismi)
                statement.bindString(2,modelismi)
                statement.bindString(3,fiyatdegeri)
                statement.bindBlob(4,byteArray)
                statement.execute()
            }catch (e:Exception){
                e.printStackTrace()
            }
            val intent = Intent(this@YuklemeActivity,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
    //fotoğrafı küçültmek için algoritma. 
    private fun kucukbitmapolustur(image : Bitmap, maximumBoyut : Int) : Bitmap{
        var width = image.width
        var height = image.height
        val bitmaporani : Double = width.toDouble() / height.toDouble()
        if (bitmaporani > 1){
            //yatay görsel
            width = maximumBoyut
            val değiştirilmişgenişlik = width / bitmaporani
            height = değiştirilmişgenişlik.toInt()
        }else{
            //dikey
            height = maximumBoyut
            val değiştirilmişuzunluk = height * bitmaporani
            width = değiştirilmişuzunluk.toInt()
        }
        return Bitmap.createScaledBitmap(image,width,height,true)
    }
    fun galeriErisim(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            //Android 33+ -> READ_MEDİA_IMAGES
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view,"Foroğraflara Erişim İçin İzniniz Gerekiyor",Snackbar.LENGTH_INDEFINITE).setAction("İzin Veriyorum", View.OnClickListener {
                        permissionizinlauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }).show()
                } else {
                    permissionizinlauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intenttogaleri =Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intenttogaleri)
                //intent
            }
        }else{
            //Android 33- -> READ_EXTERNAl_STORAGE
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view,"Foroğraflara Erişim İçin İzniniz Gerekiyor",Snackbar.LENGTH_INDEFINITE).setAction("İzin Veriyorum", View.OnClickListener {
                        permissionizinlauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
                } else {
                    permissionizinlauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intenttogaleri =Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intenttogaleri)
                //intent
            }
        }
    }
    private fun launcherlarikayit() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val launcherkayitintent = result.data
                    if (launcherkayitintent != null) {
                        val resimverisi = launcherkayitintent.data
                        //binding.imageView.setImageURI(resimverisi)
                        if (resimverisi != null) {
                            try {
                                if (Build.VERSION.SDK_INT >= 28) {
                                    val source = ImageDecoder.createSource(
                                        this@YuklemeActivity.contentResolver,resimverisi
                                    )
                                    secilibitmap = ImageDecoder.decodeBitmap(source)
                                    binding.imageView.setImageBitmap(secilibitmap)
                                } else {
                                    secilibitmap = MediaStore.Images.Media.getBitmap(
                                        contentResolver,
                                        resimverisi
                                    )
                                    binding.imageView.setImageBitmap(secilibitmap)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        permissionizinlauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {result ->
            if (result){
                //izin verildi
                val intenttogaleri =Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intenttogaleri)
            }else {
                //izin reddedildi
                Toast.makeText(this@YuklemeActivity,"İzninize İhitiyacımız Var",Toast.LENGTH_LONG).show()
            }
        }

    }
}
