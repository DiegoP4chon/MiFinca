package com.ganawin.mifinca.ui.sreensapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.os.persistableBundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ganawin.mifinca.R
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.remote.terneros.TernerosDataSource
import com.ganawin.mifinca.databinding.FragmentTernerosBinding
import com.ganawin.mifinca.domain.terneros.TerneroRepoImpl
import com.ganawin.mifinca.presentation.ternero.TerneroScreenViewModel
import com.ganawin.mifinca.presentation.ternero.TerneroScreenViewModelFactory
import com.ganawin.mifinca.ui.sreensapp.adapter.OnClickListener
import com.ganawin.mifinca.ui.sreensapp.adapter.TerneroSreenAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.rpc.context.AttributeContext

class TernerosFragment : Fragment(R.layout.fragment_terneros), OnClickListener {

    private lateinit var binding: FragmentTernerosBinding
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var userUIDColecction: String

    private val viewModel by viewModels<TerneroScreenViewModel> { TerneroScreenViewModelFactory(
        TerneroRepoImpl(TernerosDataSource())
    ) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userUid()
        binding = FragmentTernerosBinding.bind(view)
        binding.fbtnAddTernero.setOnClickListener { addTerneroFragment() }
        showTerneros()
    }


    private fun showTerneros() {
        viewModel.fetchListTerneros(userUIDColecction).observe(viewLifecycleOwner, Observer { result ->
            when(result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    binding.rvTerneros.adapter = TerneroSreenAdapter(result.data, this)
                    Log.d("FireTerneroError", "data: ${result.data}")
                }
                is Resource.Failure -> {
                    Log.d("FireTerneroError", "Error ${result.exception}")
                }
            }
        })
    }

    override fun onLongClick(document: String, itemsTernero: MutableList<String>) {
        val items = resources.getStringArray(R.array.array_options_item)

        MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.dialog_OnLongClick))
                .setItems(items) { dialogInterface, i ->
                    when(i){
                        0 -> deleteTernero(document)
                        1 -> updateTernero(document, itemsTernero)
                    }
                }.show()

    }

    private fun updateTernero(document: String, itemsTernero: MutableList<String>) {
        Log.d("ModificadoDeRegistros", "${itemsTernero}")
    }

    fun deleteTernero(document: String){
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirm_delete)
                .setPositiveButton(R.string.btn_delete) { dialogInterface, i ->
                    confirmDeleteTernero(document)
                }
                .setNegativeButton(R.string.btn_cancelar_delete_ternero, null)
                .show()
    }

    fun confirmDeleteTernero(document: String){
        viewModel.deleteItemTernero(userUIDColecction, document).observe(viewLifecycleOwner, Observer { result ->
            when(result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    Log.d("EliminandoRegistros", "resultado: ${result.data}")
                    showTerneros()
                }
                is Resource.Failure -> {
                    Log.d("EliminandoRegistros", "Error ${result.exception}")
                }
            }
        })
    }

    private fun addTerneroFragment() {
        findNavController().navigate(R.id.action_ternerosFragment_to_addTerneroFragment,
            bundleOf("UID" to userUIDColecction))
    }

    private fun userUid() {
        firebaseAuth.currentUser?.let {
            userUIDColecction = "terneros${firebaseAuth.currentUser!!.uid}"
        }
    }
}