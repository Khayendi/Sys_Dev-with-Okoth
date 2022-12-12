package com.code.tusome.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.code.tusome.adapters.AssignmentsAdapter
import com.code.tusome.databinding.FragmentAssignmentsBinding
import com.code.tusome.ui.viewmodels.AssignmentViewModel
import com.code.tusome.utils.Utils

class AssignmentsFragment : Fragment() {
    private lateinit var binding: FragmentAssignmentsBinding
    private val viewModel by viewModels<AssignmentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate: Fragment created")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAssignments("Computer Technology",binding.root).observe(viewLifecycleOwner){
            if (it.isEmpty()){
                binding.emptyBoxIv.visibility = VISIBLE
                binding.emptyBoxTv.visibility = VISIBLE
                binding.assignmentRecycler.visibility = GONE
                Utils.snackBar(binding.root,"Very empty")
            }else{
                binding.emptyBoxIv.visibility = GONE
                binding.emptyBoxTv.visibility = GONE
                binding.assignmentRecycler.visibility = VISIBLE
                Utils.snackBar(binding.root,"Not empty")
                val mAdapter = AssignmentsAdapter(it)
                binding.assignmentRecycler.apply {
                    adapter = mAdapter
                    layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
                }
                mAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssignmentsBinding.inflate(inflater, container, false)
        return binding.root
    }


    companion object {
        private val TAG = AssignmentsFragment::class.java.simpleName
    }
}