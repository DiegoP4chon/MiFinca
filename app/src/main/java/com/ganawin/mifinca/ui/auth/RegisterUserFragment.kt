package com.ganawin.mifinca.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ganawin.mifinca.R
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.remote.auth.LoginDataSource
import com.ganawin.mifinca.databinding.FragmentRegisterUserBinding
import com.ganawin.mifinca.domain.auth.LoginRepoImpl
import com.ganawin.mifinca.presentation.auth.LoginScreenViewModel
import com.ganawin.mifinca.presentation.auth.LoginScreenViewModelFactory

class RegisterUserFragment : Fragment(R.layout.fragment_register_user) {

    private lateinit var binding: FragmentRegisterUserBinding
    private val viewModel by viewModels<LoginScreenViewModel> { LoginScreenViewModelFactory(LoginRepoImpl(
        LoginDataSource()
    )) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterUserBinding.bind(view)

        binding.btnRegister.setOnClickListener { validateCredentials() }

    }

    private fun validateCredentials(){

        if(binding.etEmail.text.toString().trim().isNotEmpty() &&
            binding.etPass.text.toString().trim().isNotEmpty()){

                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPass.text.toString().trim()

                createUser(email, password)

        } else {
            Toast.makeText(requireContext(), getString(R.string.complete_registro), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun createUser(email: String, password: String){

        viewModel.createUser(email, password).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegister.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    findNavController().navigate(R.id.action_registerUserFragment_to_homeFragment)
                }
                is Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(requireContext(), getString(R.string.invalid_email), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })

    }
}