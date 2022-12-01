package com.binar.c5team.gotravel.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    lateinit var binding : FragmentRegisterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // for dropdown menu gender
//        val items = listOf("Male", "Female")
//        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
//        (textField.editText as? AutoCompleteTextView)?.setAdapter(adapter)

    }

}