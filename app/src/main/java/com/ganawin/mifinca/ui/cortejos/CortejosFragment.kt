package com.ganawin.mifinca.ui.cortejos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.ganawin.mifinca.R
import com.ganawin.mifinca.core.CurrentUser
import com.ganawin.mifinca.databinding.FragmentCortejosBinding

class CortejosFragment : Fragment(R.layout.fragment_cortejos) {

    private lateinit var binding: FragmentCortejosBinding
    private lateinit var userUIDColecction: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCortejosBinding.bind(view)
        userUid()

        binding.fbtnAddCortejo.setOnClickListener { screenAddCortejo() }
    }

    private fun userUid() {
        userUIDColecction = "cortejos${CurrentUser().userUid()}"
    }

    private fun screenAddCortejo() {
        findNavController().navigate(R.id.action_cortejosFragment_to_addCortejoFragment,
            bundleOf("UID" to userUIDColecction))
    }
}