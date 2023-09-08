package com.example.embarcados.presentation.views

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.embarcados.data.database.UserRepository
import com.example.embarcados.databinding.FragmentEditUsernameBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditUsernameFragment : Fragment() {
    private lateinit var binding: FragmentEditUsernameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditUsernameBinding.inflate(layoutInflater)

        binding.editUsernameBtn.setOnClickListener {
            onEditUsernameClick()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        binding.editUsername.text = Editable.Factory.getInstance().newEditable(arguments?.getString("username") ?: "Username")
    }

    private fun onEditUsernameClick(){
        lifecycleScope.launch(Dispatchers.IO) {
            var user = UserRepository.INSTANCE.getUser()
            user.name = binding.editUsername.text.toString()
            UserRepository.INSTANCE.updateUser(user)
        }
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }
}