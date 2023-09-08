package com.example.embarcados.presentation.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.embarcados.databinding.FragmentBoardConfigBinding
import com.example.embarcados.presentation.adapters.BoardConfigCardAdapter
import com.example.embarcados.presentation.BoardConfigViewModel
import com.example.embarcados.presentation.view_state.BoardConfigViewState

class BoardConfigFragment : Fragment() {

    private val viewModel : BoardConfigViewModel by viewModels()
    private val adapter = BoardConfigCardAdapter(::onGiveAccessClick, ::onRemoveUserClick)
    private lateinit var binding: FragmentBoardConfigBinding
    private lateinit var boardName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        println("oncreate bord config fragment")
        binding = FragmentBoardConfigBinding.inflate(layoutInflater)
        println("oncreate bord config fragment end")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("onViewCreated bord config fragment")
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        viewModel.viewState.observe(viewLifecycleOwner) {
                viewState -> updateUI(viewState)
        }

        boardName = arguments?.getString("id") ?: ""
        viewModel.loadBoardList(boardName)
        view.findNavController()

        binding.addRfidBtn.setOnClickListener {
            onAddRFIDBClick()
        }
        println("end")
    }

    private fun updateUI(viewState : BoardConfigViewState) {
        when(viewState){
            is BoardConfigViewState.Content -> {
                println("update ui")
                viewState.config.forEach {
                    print(it.name)
                }
                adapter.setData(viewState.config)
            }
        }
    }

    private fun onAddRFIDBClick(){
        val action = BoardConfigFragmentDirections.actionBoardConfigFragmentToAddRFIDFragment(boardName)
        this.findNavController().navigate(action)
    }

    private fun onGiveAccessClick(username: String){
        viewModel.giveAccess(boardName, username)
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun onRemoveUserClick(username: String){
        viewModel.removeUser(boardName, username)
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }
}