package com.ganawin.mifinca.ui.sreensapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
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
import com.ganawin.mifinca.ui.sreensapp.adapter.TerneroSreenAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.rpc.context.AttributeContext

class TernerosFragment : Fragment(R.layout.fragment_terneros) {

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
                    binding.rvTerneros.adapter = TerneroSreenAdapter(result.data)
                    Log.d("FireTerneroError", "data: ${result.data}")
                }
                is Resource.Failure -> {
                    Log.d("FireTerneroError", "Error ${result.exception}")
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