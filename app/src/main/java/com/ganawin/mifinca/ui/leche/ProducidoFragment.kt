package com.ganawin.mifinca.ui.leche

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.ganawin.mifinca.R
import com.ganawin.mifinca.databinding.FragmentProducidoBinding

class ProducidoFragment : Fragment(R.layout.fragment_producido) {

    private var totalLitros = 0
    private lateinit var binding: FragmentProducidoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {  bundle ->
            totalLitros = bundle.getInt("litros", 0)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProducidoBinding.bind(view)
        val litros = "${getString(R.string.total_producido)} $totalLitros ${getString(R.string.litros)}"
        binding.tvProducidoLeche.text = litros

        binding.btnCalcularPaga.setOnClickListener { calcularPago() }
    }

    private fun calcularPago() {
        val precio = totalLitros * binding.etPrice.text.toString().toInt()
        val pago = "${getString(R.string.paga_leche)}: $precio ${getString(R.string.pesos)}"
        binding.tvTitleProducido.text = pago
    }
}