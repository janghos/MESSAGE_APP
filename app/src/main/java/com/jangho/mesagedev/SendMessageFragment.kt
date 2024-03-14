package com.jangho.mesagedev

import MainFragment
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.jangho.mesagedev.databinding.FragmentJoinBinding
import com.jangho.mesagedev.databinding.FragmentLoginBinding
import com.jangho.mesagedev.databinding.FragmentSendMessageBinding
import java.lang.StringBuilder

class SendMessageFragment : Fragment() {
    private var _binding: FragmentSendMessageBinding? = null
    private val binding get() = _binding!!
    private var mainActivity : MainActivity ?= null
    val groupList = mutableListOf<String>()
    private var groupAddListAdapter : GroupListAdapter ?= null
    var mPath : Uri ?= null
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

        binding.btnSubmit.setOnClickListener {
            var count = 0
            var list = StringBuilder()
            for (i in selectedGroupList) {
                for (i in mainActivity!!.getStringList(i)!!) {
                    list.append("${i.split(",")[1]};")
                    count++
                }
            }
            if(count < 1) {
                Toast.makeText(requireContext(), "1개 이상 그룹을 선택해주세요", Toast.LENGTH_SHORT).show()
            } else if(mainActivity!!.getSaveString("count")!!.toInt() < 0) {
                Toast.makeText(requireContext(), "포인트가 없습니다.", Toast.LENGTH_SHORT).show()
            } else{
                mainActivity!!.saveString("count", (mainActivity!!.getSaveString("count")!!.toInt() - count).toString())
                val messageBody = binding.etSendText.text.toString()
                sendVideoMMS(mPath, messageBody, list)
            }
        }

        binding.btnVideo.setOnClickListener {
            // 파일 선택 Intent 실행
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "video/*" // 비디오 파일 유형 허용
            startActivityForResult(intent, 111)
        }

        binding.tvPoint.text = mainActivity!!.getSaveString("count") + " P"

        return binding.root
    }

    private fun sendVideoMMS(videoUri: Uri?, messageBody: String, recipientNumbers: StringBuilder) {

        if(videoUri != null) {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_STREAM, videoUri)
            sendIntent.type = "video/*"
            sendIntent.putExtra("sms_body", messageBody)
            sendIntent.putExtra("address", recipientNumbers.toString())
            startActivity(sendIntent)
        }else {
            val smsUri = Uri.parse("smsto:$recipientNumbers") //phonNumber에는 01012345678과 같은 구성.
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.setData(smsUri)
            intent.putExtra("sms_body", binding.etSendText.text.toString()) //해당 값에 전달하고자 하는 문자메시지 전달
            startActivity(intent)
        }
        mainActivity!!.replaceFragment(MainFragment(),null)

    }

    private fun getFileName(uri: Uri): String {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            return it.getString(nameIndex)
        }
        return ""
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { videoUri ->
                val videoFileName = getFileName(videoUri)
                binding.tvAddFile.text = "첨부 파일: $videoFileName"
                mPath = videoUri
            }
        }
    }


}