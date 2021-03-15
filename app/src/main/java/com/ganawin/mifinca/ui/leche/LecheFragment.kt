package com.ganawin.mifinca.ui.leche

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ganawin.mifinca.R
import com.ganawin.mifinca.core.CurrentUser
import com.ganawin.mifinca.core.GenerateId
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Leche
import com.ganawin.mifinca.data.remote.leche.LecheDataSource
import com.ganawin.mifinca.databinding.FragmentLecheBinding
import com.ganawin.mifinca.domain.leche.LecheRepoImpl
import com.ganawin.mifinca.presentation.leche.LecheScreenViewModel
import com.ganawin.mifinca.presentation.leche.LecheScreenViewModelFactory
import com.ganawin.mifinca.ui.leche.adapter.LecheScreenAdapter
import java.util.*

class LecheFragment : Fragment(R.layout.fragment_leche) {

    private lateinit var binding: FragmentLecheBinding
    private val viewModel by viewModels<LecheScreenViewModel>
            { LecheScreenViewModelFactory(LecheRepoImpl(LecheDataSource())) }
    private lateinit var UserUIDCollection: String
    private lateinit var calendar: Calendar
    private var year = 0
    private var month = 0
    private var day = 0
    private lateinit var lecheListFilter: List<Leche>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLecheBinding.bind(view)
        userUID()
        obtenerLeche()
        fechaActual()
        binding.btnFecha.setOnClickListener { showDatePicker(0) }
        binding.btnFilterLeche.setOnClickListener { activeFilter() }
        binding.btnFilterFechaInicio.setOnClickListener { showDatePicker(1) }
        binding.btnFilterFechaFin.setOnClickListener { showDatePicker(2) }
        binding.btnFiltrar.setOnClickListener { validarFechasFiltro() }
        binding.btnConsultarPago.setOnClickListener { calcularProducido() }
        binding.btnRegisterLeche.setOnClickListener { registrarLeche() }

    }

    private fun calcularProducido() {
        var totalLitros = 0
        for (filterleche in lecheListFilter){
            totalLitros += filterleche.litros
        }
        findNavController().navigate(R.id.action_lecheFragment_to_producidoFragment,
        bundleOf("litros" to totalLitros))
    }

    private fun validarFechasFiltro() {
        if(binding.btnFilterFechaInicio.text.toString() == getString(R.string.Fecha_Inicial)
            || binding.btnFilterFechaFin.text.toString() == getString(R.string.fecha_fin)){
            Toast.makeText(requireContext(), getString(R.string.alert_select_date),
                Toast.LENGTH_LONG).show()
        } else {
            val fechaInicio = GenerateId().generateID(binding.btnFilterFechaInicio.text.toString())
            val fechaFin = GenerateId().generateID(binding.btnFilterFechaFin.text.toString())
            if(fechaInicio <= fechaFin) {
                getListFilter(fechaInicio, fechaFin)
            } else {
                Toast.makeText(requireContext(), getString(R.string.alert_date_range),
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getListFilter(fechaInicio: Int, fechaFin: Int) {
        viewModel.getListLecheFilter(UserUIDCollection, fechaInicio, fechaFin)
            .observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    lecheListFilter = result.data
                    binding.rvLeche.adapter = LecheScreenAdapter(lecheListFilter)
                    binding.cvFiltro.visibility = View.GONE
                    binding.btnConsultarPago.visibility = View.VISIBLE
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), "No se encontro registros", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

    private fun activeFilter() {
        if (binding.cvFiltro.visibility == View.GONE){
            binding.cvFiltro.visibility = View.VISIBLE
        } else {
            binding.cvFiltro.visibility = View.GONE
        }
    }

    private fun obtenerLeche() {
        viewModel.getListLeche(UserUIDCollection).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    binding.rvLeche.adapter = LecheScreenAdapter(result.data)
                }
                is Resource.Failure -> {
                    Log.d("VerLeche", "Error ${result.exception}")
                }
            }
        })
    }

    private fun fechaActual() {
        calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)

        val fecha = "$day/${month+1}/$year"
        binding.btnFecha.text = fecha
    }

    private fun showDatePicker(item: Int){
        val datePickerDialog = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{
                view, year, month, dayOfMonth ->
            val newFecha = "$dayOfMonth/${month+1}/$year"
            if(item == 0) {
                binding.btnFecha.text = newFecha
            }else if(item == 1){
                binding.btnFilterFechaInicio.text = newFecha
            }else if(item == 2){
                binding.btnFilterFechaFin.text = newFecha
            }
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun userUID() {
        UserUIDCollection = "leche${CurrentUser().userUid()}"
    }

    private fun registrarLeche() {
        val fecha = binding.btnFecha.text.toString().trim()
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