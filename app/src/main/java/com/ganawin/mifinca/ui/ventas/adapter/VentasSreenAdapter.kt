package com.ganawin.mifinca.ui.ventas.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ganawin.mifinca.core.BaseViewHolder
import com.ganawin.mifinca.data.model.Venta
import com.ganawin.mifinca.databinding.ItemVentasBinding

class VentasSreenAdapter(private val ventasList: List<Venta>): RecyclerView.Adapter<BaseViewHolder<*>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding = ItemVentasBinding.inflate(LayoutInflater.from(parent.context), parent,
            false)
        return VentasScreenViewHolder(itemBinding, parent.context)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is VentasSreenAdapter.VentasScreenViewHolder -> holder.bind(ventasList[position])
        }
    }

    override fun getItemCount(): Int = ventasList.size

    private inner class VentasScreenViewHolder(
        val binding: ItemVentasBinding,
        val context: Context
    ): BaseViewHolder<Venta>(binding.root) {
        override fun bind(item: Venta) {
            binding.tvIdentificacionVenta.text = item.descripVenta
            binding.tvFechaVenta.text = item.fecha_venta
            binding.tvCompradorVenta.text = item.comprador
            binding.tvPrecioVenta.text = item.valorVenta.toString()
            Glide.with(context)
                .load(item.url_Photo)
                .centerCrop()
                .into(binding.imgVenta)
        }
    }

}