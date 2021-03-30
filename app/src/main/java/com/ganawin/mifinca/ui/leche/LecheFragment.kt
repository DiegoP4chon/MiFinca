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
import com.ganawin.mifinca.ui.leche.adapter.OnClickListenerLeche
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*
import kotlin.collections.HashMap

class LecheFragment : Fragment(R.layout.fragment_leche), OnClickListenerLeche {

    private lateinit var binding: FragmentLecheBinding
    private val viewModel by viewModels<LecheScreenViewModel>
            { LecheScreenViewModelFactory(LecheRepoImpl(LecheDataSource())) }
    private lateinit var UserUIDCollection: String
    private lateinit var calendar: Calendar
    private var year = 0
    private var month = 0
    private var day = 0
    private lateinit var lecheListFilter: List<Leche>
    private var isModified = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLecheBinding.bind(view)
        userUID()
        obtenerLeche()
        fechaActual()
        configureButtonAndTitle()
        binding.btnFecha.setOnClickListener { showDatePicker(0) }
        binding.btnFilterLeche.setOnClickListener { activeFilter() }
        binding.btnFilterFechaInicio.setOnClickListener { showDatePicker(1) }
        binding.btnFilterFechaFin.setOnClickListener { showDatePicker(2) }
        binding.btnFiltrar.setOnClickListener { validarFechasFiltro() }
        binding.btnConsultarPago.setOnClickListener { calcularProducido() }
        binding.btnRegisterLeche.setOnClickListener { registrarLeche() }

    }

    private fun configureButtonAndTitle() {
        if(isModified){
            binding.tvTitleLeche.text = getString(R.string.registros_leche_modified)
            binding.btnRegisterLeche.text = getString(R.string.update)
        } else {
            binding.tvTitleLeche.text = getString(R.string.registros_leche)
            binding.btnRegisterLeche.text = getString(R.string.btn_registrar)
        }
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
                    binding.rvLeche.adapter = LecheScreenAdapter(lecheListFilter, this)
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
                    binding.rvLeche.adapter = LecheScreenAdapter(result.data, this)
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
        val datePickerDialog = DatePickerDialog(requireContext(), {
                view, year, month, dayOfMonth ->
            val newFecha = "$dayOfMonth/${month+1}/$year"
            when (item) {
                0 -> {
                    binding.btnFecha.text = newFecha
                }
                1 -> {
                    binding.btnFilterFechaInicio.text = newFecha
                }
                2 -> {
                    binding.btnFilterFechaFin.text = newFecha
                }
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
                        Toast.makeText(requireContext(), "${result.exception}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } else {
            binding.tilEntregaLeche.error = getString(R.string.require)
        }

    }

    override fun onCLickItemLeche(document: String, itemLeche: MutableList<String>) {
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_update)
                .setPositiveButton(R.string.btn_update) { dialogInterface, i ->
                    updateRegistroLeche(document, itemLeche)
                }
                .setNegativeButton(R.string.btn_cancelar_delete, null)
                .show()
    }

    private fun updateRegistroLeche(document: String, itemLeche: MutableList<String>) {
        isModified = true
        configureButtonAndTitle()
        binding.btnFecha.text = itemLeche[0]
        binding.etEntregaLeche.setText(itemLeche[2])


            binding.btnRegisterLeche.setOnClickListener {
                if(isModified){
                    if(binding.etEntregaLeche.text.toString().trim().isEmpty()){
                        binding.tilEntregaLeche.error = getString(R.string.require)
                    } else {
                        val mapLeche: HashMap<String, Any> = hashMapOf()
                        mapLeche["fecha"] = binding.btnFecha.text.toString()
                        mapLeche["id"] = GenerateId().generateID(binding.btnFecha.text.toString())
                        mapLeche["litros"] = binding.etEntregaLeche.text.toString().trim().toInt()

                        updateLeche(mapLeche, document)
                    }
                } else {registrarLeche()}

            }
    }

    private fun updateLeche(mapLeche: HashMap<String, Any>, document: String) {
        viewModel.updateRegistroLeche(UserUIDCollection, document, mapLeche)
                .observe(viewLifecycleOwner, { result ->
                    when(result){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            isModified = false
                            binding.etEntregaLeche.setText("")
                            configureButtonAndTitle()
                            obtenerLeche()
                            Toast.makeText(requireContext(), "${result.data}", Toast.LENGTH_SHORT)
                                    .show()
                        }
                        is Resource.Failure -> {
                            Toast.makeText(requireContext(), "Ha ocurrido un error", Toast.LENGTH_SHORT)
                                    .show()
                        }
                    }
                })
    }
}
