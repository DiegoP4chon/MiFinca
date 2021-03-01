package com.ganawin.mifinca.ui.sreensapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ganawin.mifinca.R
import com.ganawin.mifinca.core.BaseViewHolder
import com.ganawin.mifinca.core.CalculateEdad
import com.ganawin.mifinca.data.model.Ternero
import com.ganawin.mifinca.databinding.ItemTerneroBinding

class TerneroSreenAdapter(private val ternerosList: List<Ternero>): RecyclerView.Adapter<BaseViewHolder<*>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding = ItemTerneroBinding.inflate(LayoutInflater.from(parent.context), parent,
            false)
        return TerneroScreenViewHolder(itemBinding, parent.context)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is TerneroScreenViewHolder -> holder.bind(ternerosList[position])
        }
    }

    override fun getItemCount(): Int = ternerosList.size


    private inner class TerneroScreenViewHolder(
        val binding: ItemTerneroBinding,
        val context: Context
    ): BaseViewHolder<Ternero>(binding.root){
        override fun bind(item: Ternero) {
            binding.tvDateNacimiento.text = "Fecha de nacimeinto: ${item.date_nacimiento}"
            binding.tvEdad.text = CalculateEdad().calcularEdad(item.date_nacimiento)
            binding.tvRaza.text = "Raza:${item.raza}"
            binding.tvSexo.text = "Sexo: ${item.sexo}"
            binding.tvMadre.text = "Madre: ${item.madre}"
            binding.tvPadre.text = "Padre ${item.padre}"
            Glide.with(context)
                .load(item.url_photo)
                .centerCrop()
                .into(binding.imgFotoTernero)
        }
    }
}