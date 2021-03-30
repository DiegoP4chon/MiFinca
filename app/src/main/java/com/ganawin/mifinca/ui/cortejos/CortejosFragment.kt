package com.ganawin.mifinca.ui.cortejos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ganawin.mifinca.R
import com.ganawin.mifinca.core.CurrentUser
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.remote.cortejos.CortejosDataSource
import com.ganawin.mifinca.databinding.FragmentCortejosBinding
import com.ganawin.mifinca.domain.cortejos.CortejosRepoImpl
import com.ganawin.mifinca.presentation.cortejos.CortejosScreenViewModel
import com.ganawin.mifinca.presentation.cortejos.CortejosScreenViewModelFactory
import com.ganawin.mifinca.ui.cortejos.adapter.CortejosScreenAdapter
import com.ganawin.mifinca.ui.cortejos.adapter.OnClickListenerCortejo
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CortejosFragment : Fragment(R.layout.fragment_cortejos), OnClickListenerCortejo {

    private lateinit var binding: FragmentCortejosBinding
    private lateinit var userUIDColecction: String

    private val viewModel by viewModels<CortejosScreenViewModel> { CortejosScreenViewModelFactory(
            CortejosRepoImpl(CortejosDataSource())
    ) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCortejosBinding.bind(view)
        userUid()
        getListCortejos()

        binding.fbtnAddCortejo.setOnClickListener { screenAddCortejo() }
    }

    private fun getListCortejos() {
        viewModel.getListCortejos(userUIDColecction).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    binding.rvCortejos.adapter = CortejosScreenAdapter(result.data, this)
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), "Ha ocurrido un error", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }

    override fun onClickItemCortejo(document: String) {
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_update)
                .setPositiveButton(R.string.btn_update) { dialogInterface, i ->
                    updateCortejo(document)
                }
                .setNegativeButton(R.string.btn_cancelar_delete, null)
                .show()
    }

    private fun updateCortejo(document: String) {
        findNavController().navigate(R.id.action_cortejosFragment_to_addCortejoFragment,
                bundleOf("document" to document , "UID" to userUIDColecction)
        )
    }

    private fun userUid() {
        userUIDColecction = "cortejos${CurrentUser().userUid()}"
    }

    private fun screenAddCortejo() {
        findNavController().navigate(R.id.action_cortejosFragment_to_addCortejoFragment,
            bundleOf("UID" to userUIDColecction))
    }
}