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
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    @Inject
    lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding
    private var destinationsListDisposable: Disposable? = null

    private var firstDestinationList = mutableListOf<Destination>()
    private var secondDestinationList = mutableListOf<Destination>()

    private lateinit var firstDestinationAdapter: ArrayAdapter<Destination>
    private lateinit var secondDestinationAdapter: ArrayAdapter<Destination>

    private var firstChosenDestination: Destination? = null
    private var secondChosenDestination: Destination? = null

    private val submittingSubject: PublishSubject<Unit> = PublishSubject.create()
    private var submittingSubjectDisposable: Disposable? = null


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
    }

    override fun onStart() {
        super.onStart()
        firstDestinationAdapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_list_item_1,
            firstDestinationList
        )

        secondDestinationAdapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_list_item_1,
            secondDestinationList
        )

        submittingSubjectDisposable = submittingSubject.subscribe {
            val firstDestination = firstChosenDestination
            val secondDestination = secondChosenDestination
            if (firstDestination != null && secondDestination != null) {
                val distanceInKm = viewModel.calculateDistanceBetweenDestinations(
                    firstDestination.latitude,
                    firstDestination.longitude,
                    secondDestination.latitude,
                    secondDestination.longitude
                )

                binding.distance.text = String.format("$distanceInKm km")
            }
        }

        binding.searchFirstStation.isActivated = true
        binding.searchSecondStation.isActivated = true

        binding.scrollFirstSuggestions.adapter = this.firstDestinationAdapter
        binding.scrollSecondSuggestions.adapter = this.secondDestinationAdapter

        val loadDestinationsListToFirstAdapter = {
            if (firstDestinationList.isEmpty()) {
                destinationsListDisposable = viewModel.getDestinationsList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { destinations ->
                            firstDestinationList = destinations.toMutableList()
                            firstDestinationList.sortWith(kotlin.Comparator { dest1, dest2 ->
                                (dest2.hits ?: 0) - (dest1.hits ?: 0)
                            })
                            firstDestinationAdapter.addAll(firstDestinationList)
                        },
                        { error ->
                            Timber.e(error)
                            Toast.makeText(context, "Some error occurred", Toast.LENGTH_LONG).show()
                        }
                    )
            } else {
                firstDestinationAdapter.clear()
                firstDestinationAdapter.addAll(firstDestinationList)
            }
        }

        val loadDestinationsListToSecondAdapter = {
            if (secondDestinationList.isEmpty()) {
                destinationsListDisposable = viewModel.getDestinationsList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ destinations ->
                        secondDestinationList = destinations.toMutableList()
                        secondDestinationList.sortWith(kotlin.Comparator { dest1, dest2 ->
                            (dest2.hits ?: 0) - (dest1.hits ?: 0)
                        })
                        secondDestinationAdapter.addAll(secondDestinationList)
                    },
                        { error ->
                            Timber.e(error)
                            Toast.makeText(context, "Some error occurred", Toast.LENGTH_LONG).show()
                        })
            } else {
                secondDestinationAdapter.clear()
                secondDestinationAdapter.addAll(secondDestinationList)
            }
        }

        val onFirstScrollItemClickListener =
            AdapterView.OnItemClickListener { parent: AdapterView<*>?,
                                              _: View?, position: Int, _: Long ->
                val destinationName = (parent?.getItemAtPosition(position) as Destination).name
                binding.searchFirstStation.setQuery(destinationName, true)
            }

        val onSecondScrollItemClickListener =
            AdapterView.OnItemClickListener { parent: AdapterView<*>?,
                                              _: View?, position: Int, _: Long ->
                val destinationName = (parent?.getItemAtPosition(position) as Destination).name
                binding.searchSecondStation.setQuery(destinationName, true)
            }

        val onFirstSearchQueryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                firstDestinationAdapter.filter.filter(newText)
                binding.scrollFirstSuggestions.visibility = View.VISIBLE
                if (firstDestinationAdapter.isEmpty) {
                    loadDestinationsListToFirstAdapter.invoke()
                }
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                val destinationKeywords = firstDestinationList.flatMap {
                    it.keywords ?: emptyList()
                }
                val destinationKeywordName = destinationKeywords.find {
                    it.contains(query ?: "", true)
                }
                if (destinationKeywordName != null) {
                    val destination = firstDestinationList.find {
                        it.keywords?.contains(destinationKeywordName) ?: false
                    }
                    binding.searchFirstStation.setQuery(destination?.name, false)
                    firstDestinationAdapter.clear()
                    binding.scrollFirstSuggestions.visibility = View.INVISIBLE
                    firstChosenDestination = destination
                    submittingSubject.onNext(Unit)

                } else {
                    Toast.makeText(context, "No Match found", Toast.LENGTH_LONG).show()
                }
                return false
            }
        }

        val onSecondSearchQueryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                secondDestinationAdapter.filter.filter(newText)
                binding.scrollSecondSuggestions.visibility = View.VISIBLE
                if (secondDestinationAdapter.isEmpty) {
                    loadDestinationsListToSecondAdapter.invoke()
                }
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                val destinationKeywords = secondDestinationList.flatMap {
                    it.keywords ?: emptyList()
                }
                val destinationKeywordName = destinationKeywords.find {
                    it.contains(query ?: "", true)
                }
                if (destinationKeywordName != null) {
                    val destination = secondDestinationList.find {
                        it.keywords?.contains(destinationKeywordName) ?: false
                    }
                    binding.searchSecondStation.setQuery(destination?.name, false)
                    secondDestinationAdapter.clear()
                    binding.scrollSecondSuggestions.visibility = View.INVISIBLE
                    secondChosenDestination = destination
                    submittingSubject.onNext(Unit)

                } else {
                    Toast.makeText(context, "No Match found", Toast.LENGTH_LONG).show()
                }
                return false
            }
        }

        val onFirstSearchCloseClickListener = {
            firstDestinationAdapter.clear()
            binding.scrollFirstSuggestions.visibility = View.INVISIBLE
            binding.searchFirstStation.setQuery("", false)
        }

        val onSecondSearchCloseClickListener = {
            secondDestinationAdapter.clear()
            binding.scrollSecondSuggestions.visibility = View.INVISIBLE
            binding.searchSecondStation.setQuery("", false)
        }

        binding.searchFirstStation.setOnSearchClickListener {
            loadDestinationsListToFirstAdapter.invoke()
        }
        binding.searchFirstStation.setOnQueryTextListener(onFirstSearchQueryTextListener)
        binding.scrollFirstSuggestions.onItemClickListener = onFirstScrollItemClickListener

        val searchCloseFirstButtonId = binding.searchFirstStation.context.resources
            .getIdentifier("android:id/search_close_btn", null, null)
        val firstCloseButton =
            binding.searchFirstStation.findViewById(searchCloseFirstButtonId) as ImageView
        firstCloseButton.setOnClickListener {
            onFirstSearchCloseClickListener.invoke()
        }

        binding.searchSecondStation.setOnSearchClickListener {
            loadDestinationsListToSecondAdapter.invoke()
        }
        binding.searchSecondStation.setOnQueryTextListener(onSecondSearchQueryTextListener)
        binding.scrollSecondSuggestions.onItemClickListener = onSecondScrollItemClickListener

        val searchCloseSecondButtonId = binding.searchSecondStation.context.resources
            .getIdentifier("android:id/search_close_btn", null, null)
        val secondCloseButton =
            binding.searchSecondStation.findViewById(searchCloseSecondButtonId) as ImageView
        secondCloseButton.setOnClickListener {
            onSecondSearchCloseClickListener.invoke()
        }
    }

    override fun onDestroy() {
        destinationsListDisposable?.dispose()
        submittingSubjectDisposable?.dispose()
        super.onDestroy()
    }
}
