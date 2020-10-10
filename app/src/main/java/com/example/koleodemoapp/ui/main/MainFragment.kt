package com.example.koleodemoapp.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.koleodemoapp.MainApplication
import com.example.koleodemoapp.R
import com.example.koleodemoapp.databinding.MainFragmentBinding
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    @Inject
    lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding
    private lateinit var disposable: Disposable
    private var destinationList = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

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

        //todo move to xml
        binding.searchFirstStation.queryHint = "Type here"
        binding.searchSecondStation.queryHint = "Type here"

        binding.scrollFirstSuggestions.adapter = this.adapter

        disposable = viewModel.getDestinationsLists()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { destinations ->
                //binding.listFirstStation.text = destinations.subList(0, 10).joinToString { it.name ?: "" }
                destinationList = destinations.map { it.name ?: "" }.toMutableList()
                this.adapter.addAll(destinationList)
            }

        binding.searchFirstStation.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (destinationList.contains(query)) {
                    adapter.filter.filter(query)
                } else {
                    //Timber.d("No match found")
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
