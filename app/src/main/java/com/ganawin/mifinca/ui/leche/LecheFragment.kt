package com.ganawin.mifinca.ui.leche

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.ganawin.mifinca.R
import com.ganawin.mifinca.core.CurrentUser
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.remote.leche.LecheDataSource
import com.ganawin.mifinca.databinding.FragmentLecheBinding
import com.ganawin.mifinca.domain.leche.LecheRepoImpl
import com.ganawin.mifinca.presentation.leche.LecheScreenViewModel
import com.ganawin.mifinca.presentation.leche.LecheScreenViewModelFactory
import com.ganawin.mifinca.ui.leche.adapter.LecheScreenAdapter
import com.ganawin.mifinca.ui.terneros.adapter.TerneroSreenAdapter
import java.util.*

class LecheFragment : Fragment(R.layout.fragment_leche) {

    private lateinit var binding: FragmentLecheBinding
    private val viewModel by viewModels<LecheScreenViewModel>
            { LecheScreenViewModelFactory(LecheRepoImpl(LecheDataSource())) }
    private lateinit var UserUIDCollection: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLecheBinding.bind(view)
        userUID()
        obtenerLeche()
        fechaActual()
        binding.btnRegisterLeche.setOnClickListener { registrarLeche() }

    }

    private fun obtenerLeche() {
        viewModel.getListLeche(UserUIDCollection).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    //binding.rvTerneros.adapter = TerneroSreenAdapter(result.data, this)
                    binding.rvLeche.adapter = LecheScreenAdapter(result.data)
                    Log.d("VerLeche", "data: ${result.data}")
                }
                is Resource.Failure -> {
                    Log.d("VerLeche", "Error ${result.exception}")
                }
            }
        })
    }

    private fun fechaActual() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.etFechaActual.setOnClickListener {
            val datePickerDialog = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{
                view, year, month, dayOfMonth ->
                val newFecha = "$dayOfMonth/${month+1}/$year"
                binding.etFechaActual.setText(newFecha)
            }, year, month, day)

            datePickerDialog.show()
        }

        val fecha = "$day/${month+1}/$year"
        binding.etFechaActual.setText(fecha)
    }

    private fun userUID() {
        UserUIDCollection = "leche${CurrentUser().userUid()}"
    }

    private fun registrarLeche() {
        val fecha = binding.etFechaActual.text.toString().trim()
        if(binding.etEntregaLeche.text.toString().trim().isNotEmpty()){
            val litros = binding.etEntregaLeche.text.toString().trim().toInt()
            viewModel.registrarLeche(litros, fecha, UserUIDCollection).observe(viewLifecycleOwner, { result ->
                when(result){
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        Toast.makeText(requireContext(), result.data, Toast.LENGTH_SHORT).show()
                        binding.etEntregaLeche.setText("")
                        obtenerLeche()
                    }
                    is Resource.Failure -> {
                        Toast.makeText(requireContext(), "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } else {
            binding.tilEntregaLeche.error = "Requerido"
        }

    }
}