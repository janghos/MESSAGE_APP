package com.jangho.mesagedev

import MainFragment
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jangho.mesagedev.databinding.FragmentSendBinding
import java.io.File
import java.lang.StringBuilder

class SendFragment : Fragment() {
    private var _binding: FragmentSendBinding? = null
    private val binding get() = _binding!!

    private var mainActivity: MainActivity? = null
    var mPath : Uri ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSendBinding.inflate(inflater, container, false)
        mainActivity = requireActivity() as MainActivity

        arguments?.let {
            var list = StringBuilder()
            var count = 0
            it.getStringArrayList("selectedList")?.let { selectedList ->
                for (i in selectedList) {
                    for (i in mainActivity!!.getStringList(i)!!) {
                        list.append("${i.split(",")[1]};")
                        count++
                    }
                }

                binding.btnSubmit.setOnClickListener {
                    mainActivity!!.saveString("count", (mainActivity!!.getSaveString("count")!!.toInt() - count).toString())
                    val messageBody = binding.etSendText.text.toString()
                    sendVideoMMS(mPath, messageBody, list)
                }
            }

            binding.tvWriter.text = mainActivity!!.getSaveString("id")

            binding.btnVideo.setOnClickListener {
                // 파일 선택 Intent 실행
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "video/*" // 비디오 파일 유형 허용
                startActivityForResult(intent, 111)
            }
        }

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