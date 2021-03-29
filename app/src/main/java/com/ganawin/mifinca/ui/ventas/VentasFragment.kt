package com.ganawin.mifinca.ui.ventas

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ganawin.mifinca.R
import com.ganawin.mifinca.core.CurrentUser
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.remote.ventas.VentasDataSource
import com.ganawin.mifinca.databinding.FragmentVentasBinding
import com.ganawin.mifinca.domain.ventas.VentasRepoImpl
import com.ganawin.mifinca.presentation.ventas.VentasScreenViewModel
import com.ganawin.mifinca.presentation.ventas.VentasScreenViewModelFactory
import com.ganawin.mifinca.ui.ventas.adapter.VentasSreenAdapter

class VentasFragment : Fragment(R.layout.fragment_ventas) {

    private lateinit var binding: FragmentVentasBinding
    private lateinit var userUIDColecction: String

    private val viewModel by viewModels<VentasScreenViewModel> { VentasScreenViewModelFactory(
        VentasRepoImpl(VentasDataSource())
    ) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentVentasBinding.bind(view)
        userUid()
        getListVentas()
        binding.fbtnAddVenta.setOnClickListener { screenAddVenta() }
    }

    private fun getListVentas() {
        viewModel.getListVenta(userUIDColecction).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    binding.rvVentas.adapter = VentasSreenAdapter(result.data)
                    Log.d("datosVentas", "data: ${result.data}")
                }
                is Resource.Failure -> {
                    Log.d("datosVentas", "Error ${result.exception}")
                }
            }
        })
    }

    private fun screenAddVenta() {
        findNavController().navigate(R.id.action_ventasFragment_to_addVentaFragment,
            bundleOf("UID" to userUIDColecction)
        )
    }

    private fun userUid() {
        userUIDColecction = "ventas${CurrentUser().userUid()}"
    }
}