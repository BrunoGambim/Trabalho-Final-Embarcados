package com.example.embarcados.presentation.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.embarcados.data.database.UserRepository
import com.example.embarcados.presentation.adapters.BoardCardListAdapter
import com.example.embarcados.presentation.BoardListViewModel
import com.example.embarcados.presentation.view_state.BoardListViewState
import com.example.embarcados.databinding.FragmentBoardListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BoardListFragment : Fragment() {

    private val viewModel : BoardListViewModel by viewModels()
    private val adapter = BoardCardListAdapter(::onBoardConfigClick, ::onOpenClick, ::onCloseClick)
    private lateinit var binding: FragmentBoardListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBoardListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        viewModel.viewState.observe(viewLifecycleOwner) {
                viewState -> updateUI(viewState)
        }
        viewModel.loadBoardList()

        binding.refreshBtn.setOnClickListener {
            viewModel.refreshList()
        }

        binding.editNameBtn.setOnClickListener {
            onEditUsernameClick()
        }

        binding.addBoardBtn.setOnClickListener {
            onAddBoardClick()
        }

        lifecycleScope.launch(Dispatchers.IO) {
            binding.username.text = UserRepository.INSTANCE.getUser().name
        }
    }

    private fun updateUI(viewState : BoardListViewState) {
        when(viewState){
            is BoardListViewState.Content -> {
                adapter.setData(viewState.boardList)
            }
        }
    }

    private fun onOpenClick(id: String){
        viewModel.sendOpenPackage(id)
    }

    private fun onCloseClick(id: String){
        viewModel.sendClosePackage(id)
    }

    private fun onBoardConfigClick(id: String){
        val action = BoardListFragmentDirections.actionBoardListFragmentToBoardConfigFragment(id)
        this.findNavController().navigate(action)
    }

    private fun onAddBoardClick(){
        val action = BoardListFragmentDirections.actionBoardListFragmentToAddBoardFragment()
        this.findNavController().navigate(action)
    }

    private fun onEditUsernameClick(){
        val action = BoardListFragmentDirections.actionBoardListFragmentToEditUsernameFragment(binding.username.text.toString())
        this.findNavController().navigate(action)
    }
}