package com.ganawin.mifinca.ui.leche.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ganawin.mifinca.core.BaseViewHolder
import com.ganawin.mifinca.data.model.Leche
import com.ganawin.mifinca.databinding.ItemLecheBinding

class LecheScreenAdapter(private val lecheList: List<Leche>, private var listener: OnClickListenerLeche): RecyclerView.Adapter<BaseViewHolder<*>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding = ItemLecheBinding.inflate(LayoutInflater.from(parent.context), parent,
                false)
        return LecheScreenViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is LecheScreenViewHolder -> holder.bind(lecheList[position])
        }
    }

    override fun getItemCount(): Int = lecheList.size

    private inner class LecheScreenViewHolder(
            val binding: ItemLecheBinding
    ) : BaseViewHolder<Leche>(binding.root) {
        override fun bind(item: Leche) {
            binding.tvFechaConsulta.text = item.fecha
            binding.tvLitrosConsulta.text = item.litros.toString()

            val itemLeche = mutableListOf<String>()
            itemLeche.add(item.fecha)
            itemLeche.add(item.id.toString())
            itemLeche.add(item.litros.toString())

            binding.root.setOnClickListener { listener.onCLickItemLeche(item.document, itemLeche) }
        }
    }
}