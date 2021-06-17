package com.debugger.jetpack.ui.addedittask

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.debugger.jetpack.R
import com.debugger.jetpack.databinding.FragmentAddEditBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditFragment : Fragment(R.layout.fragment_add_edit) {

    private val addEditTaskViewModel: AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bindings = FragmentAddEditBinding.bind(view)

        bindings.apply {
            etTask.setText(addEditTaskViewModel.taskName)
            cbTask.isChecked = addEditTaskViewModel.taskImp
            cbTask.jumpDrawablesToCurrentState()

            tvCreated.isVisible = addEditTaskViewModel.task != null
            tvCreated.text = addEditTaskViewModel.task?.createdDateFormat

            etTask.addTextChangedListener {
                addEditTaskViewModel.taskName = it.toString()
            }

            cbTask.setOnCheckedChangeListener { _, isChecked ->
                addEditTaskViewModel.taskImp = isChecked
            }

            fabDone.setOnClickListener {
                addEditTaskViewModel.onSaveClick()
            }
        }
    }
}