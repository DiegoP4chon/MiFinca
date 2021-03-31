package com.ganawin.mifinca.ui.cortejos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ganawin.mifinca.core.BaseViewHolder
import com.ganawin.mifinca.data.model.Cortejo
import com.ganawin.mifinca.databinding.ItemCortejosBinding

class CortejosScreenAdapter(private val listCortejos: List<Cortejo>,
                        private val listener: OnClickListenerCortejo):
                        RecyclerView.Adapter<BaseViewHolder<*>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding = ItemCortejosBinding.inflate(LayoutInflater.from(parent.context), parent,
            false)
        return  CortejoScreenViewHolder(itemBinding, parent.context)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder){
            is CortejosScreenAdapter.CortejoScreenViewHolder -> holder.bind(listCortejos[position])
        }
    }

    override fun getItemCount(): Int = listCortejos.size

    private inner class CortejoScreenViewHolder(
            val binding: ItemCortejosBinding,
            val context: Context
    ) : BaseViewHolder<Cortejo>(binding.root) {
        override fun bind(item: Cortejo) {
            binding.tvFechaCortejo.text = item.fecha_cortejo
            binding.tvDescripMacho.text = item.caract_macho
            binding.tvRazaMachoCortejo.text = item.raza_macho
            binding.tvDescripHembra.text = item.caract_hembra
            binding.tvRazaHembraCortejo.text = item.raza_hembra
            Glide.with(context)
                    .load(item.urlPhoto_macho)
                    .centerCrop()
                    .into(binding.imgMachoCortejo)
            Glide.with(context)
                    .load(item.urlPhoto_hembra)
                    .centerCrop()
                    .into(binding.imgHembraCortejo)

            val listIdPhotos = mutableListOf(item.idPhoto_macho, item.idPhoto_hembra)

            binding.root.setOnClickListener { listener.onClickItemCortejo(item.document, listIdPhotos) }
        }
    }
}