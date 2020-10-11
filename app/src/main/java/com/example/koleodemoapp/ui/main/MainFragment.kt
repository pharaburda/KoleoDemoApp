package com.example.koleodemoapp.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.example.koleodemoapp.MainApplication
import com.example.koleodemoapp.R
import com.example.koleodemoapp.databinding.MainFragmentBinding
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject
import com.example.koleodemoapp.entities.Destination
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import timber.log.Timber


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    @Inject
    lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding
    private lateinit var disposable: Disposable
    private var destinationList = mutableListOf<Destination>()
    private lateinit var adapter: ArrayAdapter<Destination>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.main_fragment, container, false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity?.applicationContext as MainApplication).appComponent.inject(this)
        //AndroidInjection.inject(this)
    }

    override fun onStart() {
        super.onStart()
        adapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_list_item_1,
            destinationList
        )

        binding.searchFirstStation.isActivated = true
        binding.searchSecondStation.isActivated = true

        binding.scrollFirstSuggestions.adapter = this.adapter

        binding.scrollFirstSuggestions.onItemClickListener =
            AdapterView.OnItemClickListener { parent: AdapterView<*>?, _: View?, position: Int, _: Long ->
                val destinationName = (parent?.getItemAtPosition(position) as Destination).name
                binding.searchFirstStation.setQuery(destinationName, true)
            }

        binding.searchFirstStation.setOnSearchClickListener {
            disposable = viewModel.getDestinationsLists()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { destinations ->
                    destinationList = destinations.toMutableList()
                    destinationList.sortWith(kotlin.Comparator { dest1, dest2 ->  (dest2.hits ?: 0) - (dest1.hits ?: 0)  })
                    adapter.addAll(destinationList)
                }
        }

        val searchCloseButtonId = binding.searchFirstStation.context.resources
            .getIdentifier("android:id/search_close_btn", null, null)
        val closeButton = binding.searchFirstStation.findViewById(searchCloseButtonId) as ImageView
        closeButton.setOnClickListener {
            adapter.clear()
            binding.scrollFirstSuggestions.visibility = View.INVISIBLE
            binding.searchFirstStation.setQuery("", false)
        }

        binding.searchFirstStation.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                binding.scrollFirstSuggestions.visibility = View.VISIBLE
                if(adapter.isEmpty) {
                    adapter.addAll(destinationList)
                }
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                val destinationNames = destinationList.map { it.name?: "" }
                val destination = destinationNames.find { it.contains(query ?: "", true) }
                if (destination != null) {
                    binding.searchFirstStation.setQuery(destination, false)
                    adapter.clear()
                    binding.scrollFirstSuggestions.visibility = View.INVISIBLE

                } else {
                    Toast.makeText(context, "No Match found", Toast.LENGTH_LONG).show()
                }
                return false
            }
        })

    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

}
