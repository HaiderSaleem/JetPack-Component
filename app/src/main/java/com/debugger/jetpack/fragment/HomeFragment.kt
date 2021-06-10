package com.debugger.jetpack.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.debugger.jetpack.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bTest.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToProfileFragment("Haider")
            findNavController().navigate(action)
        }
    }

}