import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.jangho.mesagedev.LoginFragment
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
        // Realtime Database에서 중복된 ID 확인
        activity?.returnFirebase()?.reference?.child("users")?.child(id)
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // 중복된 ID가 이미 존재하는 경우
                        Toast.makeText(requireContext(), "이미 사용 중인 ID입니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        // 중복된 ID가 없는 경우 회원가입 진행
                        val user = hashMapOf(
                            "id" to id,
                            "pw" to pw,
                            "count" to 10000,
                            "enable" to false
                        )

                        activity?.returnFirebase()?.reference?.child("users")?.child(id)?.setValue(user)
                            ?.addOnSuccessListener {
                                Toast.makeText(requireContext(), "회원가입 성공", Toast.LENGTH_SHORT).show()
                                activity?.replaceFragment(LoginFragment(), null)
                            }
                            ?.addOnFailureListener { e ->
                                Toast.makeText(
                                    requireContext(),
                                    "Realtime Database에 데이터 저장 실패",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // 에러 처리
                    Toast.makeText(requireContext(), "데이터베이스 오류: $error", Toast.LENGTH_SHORT).show()
                }
            })
    }
}