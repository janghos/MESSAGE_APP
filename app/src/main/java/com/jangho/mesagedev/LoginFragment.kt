package com.jangho.mesagedev

import JoinFragment
import MainFragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.common.util.SharedPreferencesUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.jangho.mesagedev.databinding.FragmentLoginBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    data class User(val id: String?=null, val pw: String?=null, val count : Long?=null, val enable : Boolean ?=null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var activity =  requireActivity() as MainActivity
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        activity.bottomNavigation().visibility = View.GONE

        binding.etPw.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // 엔터 눌렀을 때 포커스를 다음 EditText로 이동
                binding.btnLogin.performClick()
                activity.hideKeyboard(requireContext())

                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.btnLogin.setOnClickListener {

            activity.returnFirebase().getReference("users")
                .orderByChild("id").equalTo(binding.etId.text.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (userSnapshot in snapshot.children) {
                                val user = userSnapshot.getValue(User::class.java) // User is a data class representing your user structure
                                if (user?.pw == binding.etPw.text.toString() && user?.enable!!) {
                                    activity.saveString("id", user.id.toString())
                                    activity.replaceFragment(MainFragment(),null)
                                    return
                                }else if(!user?.enable!!){
                                    val dialogBuilder = AlertDialog.Builder(requireContext())
                                    dialogBuilder.setMessage("관리자의 사용승인 후 이용 가능합니다.\n문의: 025467113")
                                        .setCancelable(false)
                                        .setPositiveButton("확인") { dialog, _ ->

                                        }
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                }else{
                                    val dialogBuilder = AlertDialog.Builder(requireContext())
                                    dialogBuilder.setMessage("비밀번호 틀립니다.\n" +
                                            "관리자에게 문의하세요.\n" +
                                            "문의:025467113")
                                        .setCancelable(false)
                                        .setPositiveButton("확인") { dialog, _ ->

                                        }
                                    val alert = dialogBuilder.create()
                                    alert.show()
                                }
                            }
                        }else{
                            Toast.makeText(requireContext(), "아이디가 없습니다", Toast.LENGTH_SHORT).show()
                        }
                        // User not found or incorrect password, handle accordingly
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle the error
                    }
                })

        }

        binding.btnJoin.setOnClickListener {
            activity.replaceFragment(JoinFragment(),null)
        }




        return view
    }
}