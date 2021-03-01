package com.ganawin.mifinca.ui.home

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ganawin.mifinca.R
import com.ganawin.mifinca.data.remote.terneros.TernerosDataSource
import com.ganawin.mifinca.databinding.FragmentHomeBinding
import com.ganawin.mifinca.domain.terneros.TerneroRepoImpl
import com.ganawin.mifinca.presentation.ternero.TerneroScreenViewModel
import com.ganawin.mifinca.presentation.ternero.TerneroScreenViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment(R.layout.fragment_home,) {

    private lateinit var binding: FragmentHomeBinding

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)
        binding.btncloseSession.setOnClickListener { signOff() }
        binding.btnTerneros.setOnClickListener { screenTerneros() }

    }

    private fun screenTerneros() {
        findNavController().navigate(R.id.action_homeFragment_to_ternerosFragment)
    }

    private fun signOff(){
        val activity: Activity?
        firebaseAuth.currentUser?.let {
            firebaseAuth.signOut()
            Toast.makeText(requireContext(), "Sesion finalizada", Toast.LENGTH_SHORT)
                .show()
        }
        activity = getActivity()
        activity.let {
            activity!!.finish()
        }
    }

}