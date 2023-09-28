package com.yildirimtechnologies.galeriyeerismevefotorafarayuzu

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.yildirimtechnologies.galeriyeerismevefotorafarayuzu.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var urunlist : ArrayList<urunmodel>
    private lateinit var urunAdapter: UrunAdapter
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        urunlist = ArrayList<urunmodel>()
        urunAdapter = UrunAdapter(urunlist)
        binding.RecyclerView.layoutManager = LinearLayoutManager(this)
        binding.RecyclerView.adapter = urunAdapter

        try {
            val database = this.openOrCreateDatabase("ürünler", MODE_PRIVATE,null)
            val cursor = database.rawQuery("SELECT * FROM ürünler",null)
            val urunIx = cursor.getColumnIndex("markaismi")
            val idIx = cursor.getColumnIndex("id")
            while (cursor.moveToNext()){
                val isim = cursor.getString(urunIx)
                val id = cursor.getInt(idIx)
                val urunmodel = urunmodel(isim, id)
                urunlist.add(urunmodel)
            }
            urunAdapter.notifyDataSetChanged()
            cursor.close()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
//bağlama işlemi görüntü ve kodu bağlama
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    val menuInflater = menuInflater
    menuInflater.inflate(R.menu.fotograf_menu,menu)
    return super.onCreateOptionsMenu(menu)


    }
//İteme tıklanınca ne olcağının belirlenip methoda işlenmesi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.fotografekle){
        val intent = Intent(this@MainActivity,YuklemeActivity::class.java)
        intent.putExtra("bilgi","yeni")
        startActivity(intent)
    }
        return super.onOptionsItemSelected(item)
    }
}