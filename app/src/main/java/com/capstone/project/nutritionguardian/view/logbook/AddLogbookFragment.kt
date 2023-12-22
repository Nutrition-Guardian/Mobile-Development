package com.capstone.project.nutritionguardian.view.logbook

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.capstone.project.nutritionguardian.R
import com.capstone.project.nutritionguardian.data.Logbook
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Suppress("NAME_SHADOWING")
class AddLogbookFragment : Fragment() {

    private lateinit var edtLogbook: EditText
    private lateinit var buttonSubmit: Button
    private lateinit var textDate: Button
    private var selectedDate: String? = null

    private lateinit var databaseReference: DatabaseReference

    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_logbook, container, false)

        edtLogbook = view.findViewById(R.id.edtLogbook)
        buttonSubmit = view.findViewById(R.id.buttonSubmit)
        textDate = view.findViewById(R.id.textDate) // Initialize textDate

        databaseReference = FirebaseDatabase.getInstance().getReference("logbooks")

        textDate.setOnClickListener {
            openDatePicker()
        }

        val submitButton: Button = view.findViewById(R.id.buttonSubmit)
        submitButton.setOnClickListener {
            try {
                saveLogbookToFirebase()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "Error saving logbook: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return view
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                selectedDate = dateFormat.format(selectedCalendar.time)
                textDate.text = selectedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun saveLogbookToFirebase() {
        val logbookText = edtLogbook.text.toString()

        if (logbookText.isNotEmpty() && !selectedDate.isNullOrBlank()) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            val userLogbookReference = databaseReference.child(userId!!)

            val logbookKey = userLogbookReference.push().key

            val logbook = Logbook(logbookKey, logbookText, selectedDate!!, System.currentTimeMillis())

            logbookKey?.let {
                userLogbookReference.child(it).setValue(logbook)
            }

            edtLogbook.text.clear()

            Toast.makeText(requireContext(), "Logbook saved successfully", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(requireContext(), "Logbook text and date cannot be empty", Toast.LENGTH_SHORT)
                .show()
        }
    }
}
