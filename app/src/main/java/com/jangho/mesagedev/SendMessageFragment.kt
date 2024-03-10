package com.jangho.mesagedev

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.jangho.mesagedev.databinding.FragmentJoinBinding
import com.jangho.mesagedev.databinding.FragmentLoginBinding
import com.jangho.mesagedev.databinding.FragmentSendMessageBinding

class SendMessageFragment : Fragment() {
    private var _binding: FragmentSendMessageBinding? = null
    private val binding get() = _binding!!
    private var mainActivity : MainActivity ?= null
    val groupList = mutableListOf<String>()
    private var groupAddListAdapter : GroupListAdapter ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSendMessageBinding.inflate(inflater, container, false)
        mainActivity = requireActivity() as MainActivity

        val selectedGroupList = ArrayList<String>()

        mainActivity?.let{
        for(i in 1..100) {
            if(it.getStringList("그룹$i") != null){
                groupList.add("그룹$i"+ " / ${it.getStringList("그룹$i")!!.size}명")
            }else {
                break
            }
        }
            var groupAdapter = GroupListAdapter(groupList, true)
            binding.rvGroupList.adapter = groupAdapter
            binding.rvGroupList.layoutManager = LinearLayoutManager(requireContext())

            groupAdapter.setOnCheckListener(object : GroupListAdapter.OnGroupSelectedListener{
                override fun onGroupSelected(group : String) {
                    if(selectedGroupList.contains(group)){
                        selectedGroupList.remove(group)
                    }else {
                        selectedGroupList.add(group)
                    }
                    if(groupAddListAdapter == null){
                        groupAddListAdapter = GroupListAdapter(selectedGroupList, false)
                        binding.rvSendList.adapter = groupAddListAdapter
                        binding.rvSendList.layoutManager = LinearLayoutManager(requireContext())
                    }else {
                        groupAddListAdapter = GroupListAdapter(selectedGroupList, false)
                        binding.rvSendList.adapter = groupAddListAdapter
                        binding.rvSendList.layoutManager = LinearLayoutManager(requireContext())
                    }
                    var num = 0
                    for(i in selectedGroupList) {

                        it.getStringList(i)?.let {
                            num += it.size
                        }
                    }
                    binding.countPerson.text = "$num" + "명"
                }

            })
        }



        binding.btnSend.setOnClickListener {
            mainActivity?.let{
                val bundle = Bundle()
                bundle.putStringArrayList("selectedList", selectedGroupList)
                it.replaceFragment(SendFragment(),bundle)
            }
        }

        binding.tvPoint.text = mainActivity!!.getSaveString("count") + " P"

        return binding.root
    }


}