package com.debugger.jetpack.ui.tasks

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.debugger.jetpack.NavTaskGraphDirections
import com.debugger.jetpack.R
import com.debugger.jetpack.data.SortedOrder
import com.debugger.jetpack.data.Task
import com.debugger.jetpack.databinding.FragmentTaskBinding
import com.debugger.jetpack.ui.adapter.TaskAdapter
import com.debugger.jetpack.ui.adapter.TaskAdapter.OnItemClickListener
import com.debugger.jetpack.util.exhaustive
import com.debugger.jetpack.util.onQueryTextChanged
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskFragment : Fragment(R.layout.fragment_task), OnItemClickListener {

    private val viewModel: TasksViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var searchView: SearchView

    @FlowPreview
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTaskBinding.bind(view)

        taskAdapter = TaskAdapter(this)

        binding.apply {
            rvTasks.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = taskAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskSwipe(task)
                }

            }).attachToRecyclerView(rvTasks)

            fabTask.setOnClickListener {
                viewModel.onAddNewTaskClick()
            }
        }

        setFragmentResultListener("add_edit_request") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }

        viewModel.task.observe(viewLifecycleOwner)
        {
            taskAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.taskEvents.collect { event ->
                when (event) {
                    is TasksViewModel.TaskEvents.ShowUndoDeleteMessage -> {
                        Snackbar.make(requireView(), "Task deleted", Snackbar.LENGTH_SHORT)
                            .setAction("UNDO")
                            {
                                viewModel.onUndoClick(event.task)
                            }.show()
                    }

                    is TasksViewModel.TaskEvents.NavigateToAddTaskScreen -> {
                        val action = TaskFragmentDirections.actionTaskFragmentToAddEditFragment(
                            null,
                            "New Task"
                        )
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TaskEvents.NavigateToEditTaskScreen -> {
                        val action =
                            TaskFragmentDirections.actionTaskFragmentToAddEditFragment(
                                event.task,
                                "Edit Task"
                            )
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TaskEvents.ShowSaveTaskMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    TasksViewModel.TaskEvents.NavigateToDeleteAllScreen -> {
                        val action = NavTaskGraphDirections.actionGlobalDeleteAllCompletedDialogFragment()
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_fragment_tasks, menu)

        val searchItem = menu.findItem(R.id.action_search)

        searchView = searchItem.actionView as SearchView

        val pendingQuery = viewModel.searchQuery.value
        if(pendingQuery!=null && pendingQuery.isNotEmpty())
        {
            searchView.onActionViewExpanded()
            searchView.setQuery(pendingQuery, false)
        }
        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide).isChecked =
                viewModel.preferencesFlow.first().hideComplete
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                viewModel.onDeleteAllClick()
                true
            }
            R.id.action_hide -> {
                item.isChecked = !item.isChecked
                viewModel.onHideCompletedSelected(item.isChecked)
                true
            }

            R.id.action_sort_by_date_created -> {
                viewModel.onSortOrderSelected(SortedOrder.SORT_BY_DATE)
                true
            }
            R.id.action_sort_by_name -> {
                viewModel.onSortOrderSelected(SortedOrder.SORT_BY_NAME)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(task: Task) {
        viewModel.onTaskCompleted(task)
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        viewModel.onTaskCheckedChange(task, isChecked)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }
}