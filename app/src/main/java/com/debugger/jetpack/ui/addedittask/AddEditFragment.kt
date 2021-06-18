package com.debugger.jetpack.ui.addedittask

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.debugger.jetpack.R
import com.debugger.jetpack.databinding.FragmentAddEditBinding
import com.debugger.jetpack.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditFragment : Fragment(R.layout.fragment_add_edit) {

    private val viewModel: AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bindings = FragmentAddEditBinding.bind(view)

        bindings.apply {
            etTask.setText(viewModel.taskName)
            cbTask.isChecked = viewModel.taskImp
            cbTask.jumpDrawablesToCurrentState()

            tvCreated.isVisible = viewModel.task != null
            tvCreated.text = viewModel.task?.createdDateFormat

            etTask.addTextChangedListener {
                viewModel.taskName = it.toString()
            }

            cbTask.setOnCheckedChangeListener { _, isChecked ->
                viewModel.taskImp = isChecked
            }

            fabDone.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.addEdItTaskEventFlow.collect { event ->
                when (event) {
                    is AddEditTaskViewModel.AddEditTaskEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    is AddEditTaskViewModel.AddEditTaskEvent.NavigateBackResult -> {
                        bindings.etTask.clearFocus()
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                }.exhaustive
            }
        }
    }
}