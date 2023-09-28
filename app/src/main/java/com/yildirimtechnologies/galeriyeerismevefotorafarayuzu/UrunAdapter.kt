package com.yildirimtechnologies.galeriyeerismevefotorafarayuzu

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yildirimtechnologies.galeriyeerismevefotorafarayuzu.databinding.RecyclerRowBinding

class UrunAdapter (val urunlist:ArrayList<urunmodel>): RecyclerView.Adapter<UrunAdapter.UrunHolder>() {
    class UrunHolder(val binding : RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrunHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  UrunHolder(binding)
    }

    override fun getItemCount(): Int {
        return urunlist.size
    }

    override fun onBindViewHolder(holder: UrunHolder, position: Int) {
        holder.binding.RecyclerViewTextview.text = urunlist.get(position).isim
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,YuklemeActivity::class.java)
            intent.putExtra("bilgi","eski")
            intent.putExtra("id",urunlist.get(position).id)
            holder.itemView.context.startActivity(intent)
        }
    }
}