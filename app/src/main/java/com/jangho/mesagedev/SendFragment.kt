package com.jangho.mesagedev

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jangho.mesagedev.databinding.FragmentMainBinding
import com.jangho.mesagedev.databinding.FragmentSendBinding
import java.lang.StringBuilder


class SendFragment : Fragment() {
    private var _binding: FragmentSendBinding? = null
    private val binding get() = _binding!!

    private var mainActivity : MainActivity ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSendBinding.inflate(inflater, container, false)

        mainActivity = requireActivity() as MainActivity

        arguments?.let {
            var list = StringBuilder()
            var count = 0
            it.getStringArrayList("selectedList")?.let {selecteList ->
                for(i in selecteList) {
                    for(i in mainActivity!!.getStringList(i)!!){
                        list.append("${i.split(",")[1]};")
                        count++
                    }
                }

                binding.btnSubmit.setOnClickListener {
                    mainActivity!!.saveString("count", (mainActivity!!.getSaveString("count")!!.toInt() - count).toString())

                    val smsUri = Uri.parse("smsto:$list") //phonNumber에는 01012345678과 같은 구성.
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.setData(smsUri)
                    intent.putExtra("sms_body", binding.etSendText.text.toString()) //해당 값에 전달하고자 하는 문자메시지 전달
                    startActivity(intent)


                }
            }
        }

        return binding.root
    }

}