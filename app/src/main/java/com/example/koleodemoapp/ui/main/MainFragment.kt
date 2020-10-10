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

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    @Inject
    lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding
    private lateinit var disposable: Disposable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.main_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity?.applicationContext as MainApplication).appComponent.inject(this)
        //AndroidInjection.inject(this)
    }

    override fun onStart() {
        super.onStart()
        disposable = viewModel.getDestinationsLists().subscribe { destinations ->
            binding.listFirstStation.text = destinations.subList(0, 10).joinToString { it.name ?: "" }
        }
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

}
