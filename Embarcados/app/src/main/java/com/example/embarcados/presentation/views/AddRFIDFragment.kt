package com.example.embarcados.presentation.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.embarcados.databinding.FragmentAddRFIDBinding
import com.example.embarcados.presentation.AddBoardViewModel
import com.example.embarcados.presentation.AddRFIDViewModel

class AddRFIDFragment : Fragment() {

    private lateinit var binding: FragmentAddRFIDBinding
    private val viewModel : AddRFIDViewModel by viewModels()
    private lateinit var boardName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddRFIDBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sendAddRfidPkgBtn.setOnClickListener {
            onAddRFIDClick()
        }
        boardName = arguments?.getString("boardName") ?: ""
    }

    private fun onAddRFIDClick(){
        viewModel.addRFID(binding.rfidName.text.toString(), boardName)
        requireActivity().onBackPressedDispatcher.onBackPressed()
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }
}