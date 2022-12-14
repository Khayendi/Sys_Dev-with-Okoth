package com.code.tusome.ui.fragments.auth.frags

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images.Media
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.code.tusome.R
import com.code.tusome.databinding.FragmentSignUpBinding
import com.code.tusome.models.Role
import com.code.tusome.models.User
import com.code.tusome.ui.viewmodels.MainViewModel
import com.code.tusome.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var imageUri: Uri
    private lateinit var mRole:Role
    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                val intent = Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI).apply {
                    type = "image/*"
                }
                getResults.launch(intent)
            } else {
                Utils.snackBar(binding.root, "This permission is required")
            }
        }

    private val getResults =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                imageUri = it.data?.data!!
                try {
                    val bm = Media.getBitmap(requireActivity().contentResolver, imageUri)
                    binding.profileIv.setImageBitmap(bm)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileIv.setOnClickListener {
            if (checkGalleryPermission()) {
                /**
                 * This is an external service - Implicit intent
                 */
                val intent = Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI).apply {
                    type = "image/*"
                }
                getResults.launch(intent)
            } else {
                requestPermissions.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        binding.registerBtn.setOnClickListener {
            binding.registerBtn.isEnabled = false
            binding.progressBar.visibility = VISIBLE
            val username = binding.usernameEt.text.toString().trim()
            val email = binding.emailEt.text.toString().trim()
            val password = binding.passwordEt.text.toString().trim()
            val cPassword = binding.confirmPasswordEt.text.toString().trim()
            if (username.isBlank()) {
                binding.registerBtn.isEnabled = true
                binding.progressBar.visibility = GONE
                binding.usernameEtl.error = "Username is required"
                return@setOnClickListener
            }
            if (email.isBlank()) {
                binding.registerBtn.isEnabled = true
                binding.progressBar.visibility = GONE
                binding.emailEtl.error = "Email is required"
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.registerBtn.isEnabled = true
                binding.progressBar.visibility = GONE
                binding.emailEtl.error = "Invalid email address"
                return@setOnClickListener
            }
            if (password.isBlank()) {
                binding.registerBtn.isEnabled = true
                binding.progressBar.visibility = GONE
                binding.passwordEtl.error = "Password is required"
                return@setOnClickListener
            }
            if (password.length < 8) {
                binding.registerBtn.isEnabled = true
                binding.progressBar.visibility = GONE
                binding.passwordEtl.error = "Password is too short"
                return@setOnClickListener
            }
            if (cPassword.isBlank()) {
                binding.registerBtn.isEnabled = true
                binding.progressBar.visibility = GONE
                binding.confirmPasswordEtl.error = "Cannot be empty"
                return@setOnClickListener
            }
            if (password != cPassword) {
                binding.registerBtn.isEnabled = true
                binding.progressBar.visibility = GONE
                binding.passwordEtl.error = "Passwords do not match"
                binding.confirmPasswordEtl.error = "Passwords do not match"
                return@setOnClickListener
            }
            if (imageUri.path == null) {
                Utils.snackBar(binding.root, "Select profile image to continue")
                return@setOnClickListener
            }
            val role = binding.radioGroup.checkedRadioButtonId
            mRole = if (role == R.id.student_radio) {
                Role("student")
            } else {
                Role("staff")
            }
            viewModel.register(
                username,
                email,
                password,
                imageUri,
                mRole,
                binding.root
            ).observe(viewLifecycleOwner) {
                if (it) {
                    binding.registerBtn.isEnabled = true
                    binding.progressBar.visibility = GONE
                } else {
                    binding.registerBtn.isEnabled = true
                    binding.progressBar.visibility = GONE
                }
            }
        }
    }

    /**
     * This checks storage permission of the application
     */
    private fun checkGalleryPermission(): Boolean = ActivityCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun signUp(
        username: String,
        email: String,
        password: String,
        imageUri: Uri,
        mRole: Role
    ) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val fileName = UUID.randomUUID().toString()
                val ref = FirebaseStorage.getInstance().getReference("images/$fileName")
                ref.putFile(imageUri).addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        val uid = FirebaseAuth.getInstance().uid
                        val user = User(uid!!, username, email, imageUrl, mRole)
                        FirebaseDatabase.getInstance().getReference("users/$uid")
                            .setValue(user).addOnSuccessListener {
                                binding.registerBtn.isEnabled = true
                                binding.progressBar.visibility = GONE
                                Utils.snackBar(binding.root, "Details saved successfully")
                            }.addOnFailureListener {
                                binding.registerBtn.isEnabled = true
                                binding.progressBar.visibility = GONE
                                Utils.snackBar(binding.root, it.message.toString())
                            }
                    }.addOnFailureListener {
                        binding.registerBtn.isEnabled = true
                        binding.progressBar.visibility = GONE
                        Utils.snackBar(binding.root,it.message.toString())
                    }
                }
            }.addOnFailureListener {
                binding.registerBtn.isEnabled = true
                binding.progressBar.visibility = GONE
                Utils.snackBar(binding.root, it.message.toString())
            }
    }

    companion object {
        val TAG: String = SignUpFragment::class.java.simpleName
    }
}