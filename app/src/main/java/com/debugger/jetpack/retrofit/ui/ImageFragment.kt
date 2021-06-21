package com.debugger.jetpack.retrofit.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.debugger.jetpack.R
import com.debugger.jetpack.databinding.FragmentImageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageFragment : Fragment(R.layout.fragment_image) {

    private val viewModel: ImageViewModel by viewModels()
    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentImageBinding.bind(view)

        val adapter = UnsplashPhotoAdapter()

        binding.apply {
            rvImages.setHasFixedSize(true)
            rvImages.adapter = adapter
        }

        viewModel.photos.observe(viewLifecycleOwner)
        {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}