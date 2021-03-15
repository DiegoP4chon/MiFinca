package com.ganawin.mifinca.ui.leche.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ganawin.mifinca.core.BaseViewHolder
import com.ganawin.mifinca.data.model.Leche
import com.ganawin.mifinca.data.model.Ternero
import com.ganawin.mifinca.databinding.ItemLecheBinding

class LecheScreenAdapter(private val lecheList: List<Leche>): RecyclerView.Adapter<BaseViewHolder<*>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding = ItemLecheBinding.inflate(LayoutInflater.from(parent.context), parent,
                false)
        return LecheScreenViewHolder(itemBinding, parent.context)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is LecheScreenViewHolder -> holder.bind(lecheList[position])
        }
    }

    override fun getItemCount(): Int = lecheList.size

    private inner class LecheScreenViewHolder(
            val binding: ItemLecheBinding,
            val context: Context
    ) : BaseViewHolder<Leche>(binding.root) {
        override fun bind(item: Leche) {
            binding.tvFechaConsulta.text = item.fecha
            binding.tvLitrosConsulta.text = item.litros.toString()
        }
    }
}