package com.capstone.project.nutritionguardian.view.home

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project.nutritionguardian.R
import com.capstone.project.nutritionguardian.adapters.HomeHorAdapter
import com.capstone.project.nutritionguardian.data.Food
import com.capstone.project.nutritionguardian.data.Logbook
import com.capstone.project.nutritionguardian.databinding.FragmentHomeBinding
import com.capstone.project.nutritionguardian.view.logbook.AddLogbookFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import java.util.Calendar

class HomeFragment : Fragment() {

    private lateinit var foodAdapter: HomeHorAdapter
    private lateinit var logbookAdapter: HomeHorAdapter
    private lateinit var foodRecyclerView: RecyclerView
    private lateinit var logbookRecyclerView: RecyclerView
    private lateinit var newFoodArrayList: ArrayList<Food>
    private lateinit var newLogbookArrayList: ArrayList<Logbook>
    private lateinit var strDate: String
    private lateinit var strDateNow: String
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageId: Array<Int>
    private lateinit var logbookReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataInitialSize()

        val dateNow = Calendar.getInstance().time
        strDate = DateFormat.format("EEEE", dateNow) as String
        strDateNow = DateFormat.format("d MMMM yyyy", dateNow) as String

        binding.date.text = strDateNow

        foodAdapter = HomeHorAdapter(newFoodArrayList)
        logbookAdapter = HomeHorAdapter(newLogbookArrayList)

        val layoutManagerFood = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val layoutManagerLogbook = LinearLayoutManager(context)

        foodRecyclerView = binding.rvImg
        foodRecyclerView.layoutManager = layoutManagerFood
        foodRecyclerView.setHasFixedSize(true)
        foodRecyclerView.adapter = foodAdapter

        logbookRecyclerView = binding.rvLogBook
        logbookRecyclerView.layoutManager = layoutManagerLogbook
        logbookRecyclerView.setHasFixedSize(true)
        logbookRecyclerView.adapter = logbookAdapter

        fetchFoodData()

        fetchLogbookData()

        binding.addLog.setOnClickListener {
            val addLogbookFragment = AddLogbookFragment()
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.frame_container, addLogbookFragment, AddLogbookFragment::class.java.simpleName)
                addToBackStack(null)
                commit()
            }
        }
    }

    private fun dataInitialSize() {
        newFoodArrayList = arrayListOf()
        newLogbookArrayList = arrayListOf()

        imageId = arrayOf(
            R.drawable.fruity,
            R.drawable.almond,
            R.drawable.healtysnack,
            R.drawable.capcay,
            R.drawable.fruitt,
            R.drawable.spinach,
        )

        for (i in imageId.indices) {
            val food = Food(imageId[i])
            newFoodArrayList.add(food)
        }
    }

    private fun fetchFoodData() {

    }

    private fun fetchLogbookData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        logbookReference = FirebaseDatabase.getInstance().getReference("logbooks").child(userId ?: "")


        logbookReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                newLogbookArrayList.clear()
                for (logSnapshot in snapshot.children) {
                    val logbook = logSnapshot.getValue(Logbook::class.java)
                    if (logbook != null) {
                        newLogbookArrayList.add(logbook)
                    }
                }

                newLogbookArrayList.sortByDescending { it.timestamp }

                logbookAdapter.updateData(newLogbookArrayList)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}

