package tech.sumato.utility360.presentation.fragments.tasks

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import tech.sumato.utility360.R
import tech.sumato.utility360.databinding.DemoTaskViewBinding
import tech.sumato.utility360.presentation.fragments.base.listing.DemoListingFragment
import tech.sumato.utility360.presentation.fragments.base.listing.ListingFragment

/*
@AndroidEntryPoint
class TasksFragment : ListingFragment() {


    private val viewModel by viewModels<TasksFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun <T> getViewModel(): T? {
        return viewModel as T
    }

}*/


@AndroidEntryPoint
class TasksFragment : DemoListingFragment() {

    // TODO: replace with live

    data class DemoTask(
        val id: Int = 0,
        val heading: String = "",
        val description: String = "",
        val status: String = "Pending"
    )


    private val demoTasks = listOf(
        DemoTask(
            id = 1,
            heading = "Meter installation",
            description = "Install meter at Tony Stark residency",
            status = "Pending"
        ),
        DemoTask(
            id = 2,
            heading = "Meter installation",
            description = "Install meter at Avengers headquarter",
            status = "Pending"
        ),
        DemoTask(
            id = 3,
            heading = "Site inspection",
            description = "Inspect site at Thanos universe for new connection",
            status = "Pending"
        ),
        DemoTask(
            id = 4,
            heading = "Meter reading",
            description = "Meter reading of customers at Zone 3",
            status = "Completed"
        ),
        DemoTask(
            id = 4,
            heading = "Pipe line service",
            description = "Service of newly install pipeline at Vormir",
            status = "Pending"
        ),
        DemoTask(
            id = 5,
            heading = "Meter installation",
            description = "Install meter at Hydra headquarter",
            status = "Completed"
        ),
    )

    private var filteredList = demoTasks.subList(0, demoTasks.size)

    override fun getItemsCount(): Int {
        return filteredList.size
    }

    override fun buildItem(parent: ViewGroup, position: Int): View {
        return DataBindingUtil.inflate<DemoTaskViewBinding>(
            LayoutInflater.from(parent.context),
            R.layout.demo_task_view,
            parent,
            false
        ).root
    }

    override fun bindItem(parent: View, position: Int) {
        super.bindItem(parent, position)

        val demoTask = filteredList[position]

        DataBindingUtil.bind<DemoTaskViewBinding>(parent)!!.apply {
            task = demoTask
        }

    }

    override fun getTitle(): String {
        return "Tasks"
    }


    var stopRefreshJob: Job? = null
    override fun onRefreshing() {
        super.onRefreshing()
        stopRefreshJob?.cancel()
        stopRefreshJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(1000 * 2)
            stopRefreshing()
        }
    }

    override fun onSortClicked() {
        super.onSortClicked()
    }

    private fun handleFilter() {
        filterQueryIndex = tmpQueryIndex
        if (filterQuery.equals("all", true)) {
            filteredList = demoTasks.subList(0, demoTasks.size)
            setItemCount(filteredList.size)
            return
        }
        filteredList = demoTasks.filter {
            it.status.equals(filterQuery, true)
        }
        setItemCount(filteredList.size)
    }

    private var filterQuery = ""
    private var filterQueryIndex = 0
    private var tmpQueryIndex = 0

    override fun onFilterClicked() {
        super.onFilterClicked()

        val items = arrayOf("All", "Pending", "Completed")
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Filter")
            setSingleChoiceItems(
                items, filterQueryIndex
            ) { p0, p1 ->

                tmpQueryIndex = p1
                filterQuery = items[p1]

            }
            setPositiveButton("Filter") { d, i ->
                //handle filter
                handleFilter()
                d.dismiss()
            }

            setNegativeButton("Cancel") { d, i ->
                d.dismiss()

            }

        }.show()


    }

}