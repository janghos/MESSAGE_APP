package com.jangho.mesagedev

import JoinFragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.jangho.mesagedev.databinding.FragmentLoginBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

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

            activity.visibleNavigation()
        }

        binding.btnJoin.setOnClickListener {
            activity.replaceFragment(JoinFragment(),null)
        }




        return view
    }
}