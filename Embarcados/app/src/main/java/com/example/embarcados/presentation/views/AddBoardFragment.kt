package com.example.embarcados.presentation.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.embarcados.data.database.UserRepository
import com.example.embarcados.databinding.FragmentAddBoardBinding
import com.example.embarcados.presentation.AddBoardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddBoardFragment : Fragment() {
    private lateinit var binding: FragmentAddBoardBinding
    private val viewModel : AddBoardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBoardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sendAddBoardPkgBtn.setOnClickListener {
            addBoard()
        }
    }

    private fun addBoard(){
        lifecycleScope.launch(Dispatchers.IO){
            viewModel.addBoard(binding.boardName.text.toString(), binding.wifiName.text.toString(),
                binding.wifiPassword.text.toString())
        }
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }
}