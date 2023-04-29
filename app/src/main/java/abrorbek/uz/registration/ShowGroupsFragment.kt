package abrorbek.uz.registration

import abrorbek.Object.Object
import abrorbek.adapters.StudentsAdapter
import abrorbek.models.Students
import abrorbek.uz.registration.databinding.DialogDeleteBinding
import abrorbek.uz.registration.databinding.FragmentShowGroupsBinding
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import developer.abdusamid.codial_app.db.MyDBHelper

class ShowGroupsFragment : Fragment() {

    private lateinit var bindingDelete: DialogDeleteBinding
    private lateinit var dialogDelete: AlertDialog
    lateinit var binding: FragmentShowGroupsBinding
    lateinit var myDBHelper: MyDBHelper
    private lateinit var arrayListStudents: ArrayList<Students>
    private lateinit var adapterStudents: StudentsAdapter
    var booleanAntiBag = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShowGroupsBinding.inflate(layoutInflater)

        binding.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.cardStart.setOnClickListener {
            abrorbek.Object.Object.groups.open = true
            val boolean = myDBHelper.startLessonGroup(abrorbek.Object.Object.groups, requireActivity())
            if (boolean) {
                binding.cardStart.visibility = View.GONE
            }
        }

        binding.lyAdd.setOnClickListener {
            abrorbek.Object.Object.editStudent = false
            findNavController().navigate(R.id.action_showGroupsFragment_to_addEditStudentFragment)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadData()
        showActivity()
        binding.recyclerViewStudents.adapter = adapterStudents
    }

    private fun loadData() {
        myDBHelper = MyDBHelper(requireActivity())
        arrayListStudents = ArrayList()
        arrayListStudents = myDBHelper.showStudents(abrorbek.Object.Object.groups.id!!, abrorbek.Object.Object.groups.open!!)
        adapterStudents = StudentsAdapter(requireActivity(),
            arrayListStudents,
            object : StudentsAdapter.RVClickStudents {
                override fun editStudents(students: Students) {
                    abrorbek.Object.Object.editStudent = true
                    abrorbek.Object.Object.students = students
                    findNavController().navigate(R.id.action_showGroupsFragment_to_addEditStudentFragment)
                }

                override fun deleteStudents(students: Students) {
                    if (booleanAntiBag) {
                        buildDialogDelete(students)
                        booleanAntiBag = false
                    }
                }
            })
    }

    @SuppressLint("SetTextI18n")
    private fun showActivity() {
        binding.tvGroupName.text =Object.groups.name
        binding.tvGroupName2.text = "Guruh nomi: ${Object.groups.name}"
        binding.tvTime.text = "Vaqti: ${Object.groups.times}"
        binding.tvDay.text = "Kunlari: ${Object.groups.days}"
        binding.tvMentor.text =
            "Mentor: ${Object.groups.mentors!!.name} ${Object.groups.mentors!!.surname}"
        binding.tvCountStudent.text = "O’quvchilar soni: ${arrayListStudents.size} ta"
        if (Object.groups.open!!) {
            binding.cardStart.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun buildDialogDelete(students: Students) {
        val alertDialog = AlertDialog.Builder(activity)
        bindingDelete = DialogDeleteBinding.inflate(layoutInflater)
        bindingDelete.tvDescription.text = "Do you want to delete this student ?"

        bindingDelete.tvCancel.setOnClickListener {
            dialogDelete.cancel()
        }

        bindingDelete.tvDelete.setOnClickListener {
            val boolean = myDBHelper.deleteStudents(students)
            if (boolean) {
                Toast.makeText(requireActivity(), "Successfully Delete!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireActivity(), "Failed to Delete", Toast.LENGTH_SHORT).show()
            }
            dialogDelete.cancel()
            onResume()
        }

        alertDialog.setOnCancelListener {
            booleanAntiBag = true
        }

        alertDialog.setView(bindingDelete.root)
        dialogDelete = alertDialog.create()
        dialogDelete.window!!.attributes.windowAnimations = R.style.MyAnimation
        dialogDelete.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogDelete.show()
    }
}