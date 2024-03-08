import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jangho.mesagedev.MainActivity
import com.jangho.mesagedev.databinding.FragmentJoinBinding

class JoinFragment : Fragment() {
    private var _binding: FragmentJoinBinding? = null
    private val binding get() = _binding!!
    private var activity : MainActivity ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity =  requireActivity() as MainActivity
        // Inflate the layout for this fragment
        _binding = FragmentJoinBinding.inflate(inflater, container, false)
        val view = binding.root


        binding.btnJoin.setOnClickListener {
            val id = binding.etId.text.toString()
            val pw = binding.etPw.text.toString()

            if (id.isEmpty() || pw.isEmpty()) {
                Toast.makeText(requireContext(), "아이디와 비밀번호를 모두 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                saveUserData(id, pw)
            }
        }

        return view
    }

    private fun saveUserData(id: String, pw: String) {
        // Realtime Database에 사용자 정보 저장
        val user = hashMapOf(
            "id" to id,
            "pw" to pw,
            "count" to 10000
        )

        activity?.returnFirebase()?.reference?.child("users")?.child(id)?.setValue(user)
            ?.addOnSuccessListener {
                Toast.makeText(requireContext(), "회원가입 성공", Toast.LENGTH_SHORT).show()
            }
            ?.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Realtime Database에 데이터 저장 실패", Toast.LENGTH_SHORT).show()
            }
    }
}