import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jangho.mesagedev.ContactsAdapter
import com.jangho.mesagedev.MainActivity
import com.jangho.mesagedev.databinding.FragmentMainBinding
import java.io.FileNotFoundException
import java.io.InputStream


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var contentResolver: ContentResolver? = null
    private val PICK_FILE_REQUEST_CODE = 123
    val menuItems = mutableListOf<String>()
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                // 선택한 파일의 URI를 사용하여 파일의 내용을 읽어오는 메서드 호출
                readFileContent(uri)
            }
        }
    }

    private var activity : MainActivity ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        contentResolver = requireContext().contentResolver
        activity = requireActivity() as MainActivity
        val spinner = binding.spinnerMenu

        activity?.let {
            it.visibleNavigation()
        }


        for(i in 1..100) {
            activity?.let{
                if(it.getStringList("그룹$i") != null) {
                    menuItems.add("그룹$i")
                }
            }
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, menuItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // 스피너 아이템 선택 이벤트 처리
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                // 선택된 메뉴 항목 처리
                displayContactsForGroup("그룹${position+1}")
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        }

        activity?.let {
            binding.tvPoint.text = it.getSaveString("count") + " P"
        }

        binding.btnUpload.setOnClickListener {

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*" // 모든 파일 유형 허용
            getContent.launch(intent)
        }
        displayContactsForGroup("그룹1")


        return binding.root
    }
    private fun displayContactsForGroup(groupName: String) {
        // 선택된 그룹에 속하는 연락처 목록을 가져와서 RecyclerView에 표시
        activity?.let {
            it.getStringList(groupName)?.let{ group ->
                val adapter = ContactsAdapter(group)
                binding.recyclerView.adapter = adapter
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            }

        }

    }
    private fun readFileContent(uri: Uri) {
        try {
            val inputStream: InputStream? = contentResolver?.openInputStream(uri)
            if (inputStream != null) {
                // InputStream에서 데이터를 읽어와서 문자열로 변환
                var content = inputStream.bufferedReader().use { it.readText() }

                // 읽어온 데이터를 처리하는 메서드 호출
                processFileContent(content)

                // 반드시 파일 스트림을 닫아야 합니다.
                inputStream.close()

                content = ""
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun processFileContent(content: String) {
        // 데이터를 줄 단위로 분리
        val lines = content.split("\n")

        // 각 줄에서 이름과 번호를 추출하여 로그에 출력
        val list = mutableListOf<String>()
        for (line in lines) {
            val tokens = line.split(",")
            if (tokens.size == 2) {
                val name = tokens[0].trim()
                val phoneNumber = tokens[1].trim()
                Log.d("FileContent", "Name: $name, Phone Number: $phoneNumber")
                list.add(name + "," + phoneNumber)
            }
        }
        activity?.let {
            for(i in 1..100) {
                if(it.getStringList("그룹$i") == null) {
                    it.saveStringList("그룹$i", list)
                    menuItems.add("그룹$i")
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, menuItems)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerMenu.adapter = adapter
                    adapter.notifyDataSetChanged()
                    binding.spinnerMenu.setSelection(i-1)

                    displayContactsForGroup("그룹$i")
                    break
                }
            }
        }
    }
}