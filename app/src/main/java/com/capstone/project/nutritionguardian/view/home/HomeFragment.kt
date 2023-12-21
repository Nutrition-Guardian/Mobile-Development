package com.capstone.project.nutritionguardian.view.home

import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project.nutritionguardian.R
import com.capstone.project.nutritionguardian.adapters.HomeHorAdapter
import com.capstone.project.nutritionguardian.data.Food
import com.capstone.project.nutritionguardian.databinding.FragmentHomeBinding
import com.capstone.project.nutritionguardian.view.logbook.AddLogbookFragment
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var adapter: HomeHorAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var newArrayList: ArrayList<Food>
    lateinit var strDate: String
    lateinit var strDateNow: String
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var imageId: Array<Int>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        dataInitialSize()

        val dateNow = Calendar.getInstance().time
        strDate = DateFormat.format("EEEE", dateNow) as String
        strDateNow = DateFormat.format("d MMMM yyyy", dateNow) as String

        binding.date.setText(strDateNow)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView = view.findViewById(R.id.rv_img)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = HomeHorAdapter(newArrayList)
        recyclerView.adapter = adapter



        binding.addLog.setOnClickListener{
            val addLogbookFragment = AddLogbookFragment()
            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction().apply {
               replace(R.id.frame_container,addLogbookFragment, AddLogbookFragment::class.java.simpleName)
                addToBackStack(null)
                commit()
             }
        }
    }

    private fun dataInitialSize() {
        newArrayList = arrayListOf<Food>()

        imageId = arrayOf(
            R.drawable.fruity,
            R.drawable.almond,
            R.drawable.healtysnack,
            R.drawable.capcay,
            R.drawable.fruitt,
            R.drawable.spinach,
        )

        for (i in imageId.indices){
            val food = Food(imageId[i])
            newArrayList.add(food)
        }
    }
}