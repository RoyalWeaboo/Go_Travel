package com.binar.c5team.gotravel.view.fragment

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentProfileBinding
import com.binar.c5team.gotravel.room.ProfileData
import com.binar.c5team.gotravel.room.ProfileDatabase
import com.binar.c5team.gotravel.viewmodel.FlightViewModel
import com.bumptech.glide.Glide
import com.google.android.material.transition.MaterialFadeThrough
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    //Shared Preferences
    private lateinit var sharedPref: SharedPreferences

    private var token: String = ""

    private var fullName: String = ""

    //image
    private var imageMultiPart: MultipartBody.Part? = null
    private var imageUri: Uri? = Uri.EMPTY
    private var imageFile: File? = null

    //progressbar
    var progressView: ViewGroup? = null
    private var isProgressShowing = false

    //connection
    private var connection : Boolean = false

    //room
    var profileDB: ProfileDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileDB = ProfileDatabase.getInstance(requireActivity())

        sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)
        //getting token
        token = sharedPref.getString("token", "").toString()

        val viewModel = ViewModelProvider(this)[FlightViewModel::class.java]
        viewModel.loading.observe(viewLifecycleOwner) {
            when (it) {
                true -> showProgressingView()
                false -> hideProgressingView()
            }
        }

        //checking connection
        checkConnection()
        Log.d("connection status", connection.toString())

        //getting profile
        profileData()

        binding.editProfile.setOnClickListener {
            checkConnection()
            if (connection) {
                val username = binding.tvUsername.text.toString()
                val email = binding.email.text.toString()
                val noKtp = binding.tvKtp.text.toString()
                val gender = binding.gender.text.toString()
                val address = binding.address.text.toString()
                val dateOfBirth = binding.birthDate.text.toString()

                val bun = Bundle()
                bun.putString("username", username)
                bun.putString("email", email)
                bun.putString("noKtp", noKtp)
                bun.putString("gender", gender)
                bun.putString("dateOfBirth", dateOfBirth)
                bun.putString("address", address)
                bun.putString("fullName", fullName)

                Navigation.findNavController(view)
                    .navigate(R.id.action_profileFragment_to_editProfileFragment, bun)
            }else{
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }

        binding.csCard.setOnClickListener {
            checkConnection()
            if (connection) {
                startSupportChat()
            }else{
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }

        binding.helpCard.setOnClickListener {
            //open help page
        }

        binding.webCard.setOnClickListener {
            checkConnection()
            if (connection) {
                //open web
            }else{
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }


        binding.btnAddImage.setOnClickListener {
            checkConnection()
            if (connection) {
                checkingPermissions()
            }else{
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            val saveData = sharedPref.edit()
            saveData.clear()
            saveData.apply()
            Navigation.findNavController(view)
                .navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }

    private fun checkConnection() {
        val cm = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val res = cm.activeNetwork
        connection = res != null
    }

    private fun profileData() {
        if (connection) {
            val viewModel = ViewModelProvider(this)[FlightViewModel::class.java]
            viewModel.callProfileApi(token)
            viewModel.getProfileData().observe(viewLifecycleOwner) {
                if (it != null) {
                    val gender = it.gender
                    if (gender == "L") {
                        binding.gender.text = "Male"
                    } else {
                        binding.gender.text = "Female"
                    }

                    if (it.image != null) {
                        Glide
                            .with(requireContext())
                            .load(it.image)
                            .centerCrop()
                            .into(binding.imgProfile)
                    } else {
                        Glide
                            .with(requireContext())
                            .load(R.drawable.blank_user)
                            .centerCrop()
                            .into(binding.imgProfile)
                        Toast.makeText(context, "No Profile Image Found", Toast.LENGTH_SHORT).show()
                    }
                    binding.tvUsername.text = it.username
                    binding.tvKtp.text = it.noKtp
                    binding.email.text = it.email
                    binding.address.text = it.address

                    fullName = it.name

                    //setting date
                    val date = it.dateOfBirth //string
                    val birthDateSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val xBirthDateSdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    val xBirthDate = birthDateSdf.parse(date) //to date
                    val formattedBirthDate =
                        xBirthDateSdf.format(xBirthDate)//to string with second format

                    binding.birthDate.text = formattedBirthDate

                    //save to room
                    addNote(it.id, it.noKtp, it.gender, it.dateOfBirth, it.address, it.email, it.name, it.username)

                } else {
                    Toast.makeText(context, "Failed to read profile data", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }else{
            getOfflineProfile()
        }
    }

    private fun getProfileImage(token: String) {
        if (connection) {
            val viewModel = ViewModelProvider(this)[FlightViewModel::class.java]
            viewModel.callProfileApi(token)
            viewModel.getProfileData().observe(viewLifecycleOwner) {
                if (it.image != null) {
                    Glide
                        .with(requireContext())
                        .load(it.image)
                        .centerCrop()
                        .into(binding.imgProfile)
                } else {
                    Toast.makeText(context, "No Profile Image Found", Toast.LENGTH_SHORT).show()
                    Log.d("Profile Image Response :", it.toString())
                }
            }
        } else {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startSupportChat() {
        try {
            val headerReceiver = "Halo Saya Butuh Bantuan" // Replace with your message.
            val bodyMessageFormal = " Mengenai Aplikasi Go Travel" // Replace with your message.
            val whatsappContain = headerReceiver + bodyMessageFormal
            val trimToNumner = "+6281280524466" //10 digit number
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://wa.me/$trimToNumner/?text=$whatsappContain")
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun postProfileImage(token: String, imageMultiPart: MultipartBody.Part) {
        val viewModel = ViewModelProvider(this)[FlightViewModel::class.java]
        viewModel.putProfileImageData().observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(
                    context,
                    "Profile Picture Successfully Changed !",
                    Toast.LENGTH_SHORT
                ).show()
                getProfileImage(token)
            }
        }
        viewModel.putProfileImage(token, imageMultiPart)
    }

    private fun checkingPermissions() {
        if (isGranted(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                97,
            )
        ) {
            openGallery()
        }
    }

    private fun isGranted(
        activity: Activity,
        permission: String,
        permissions: Array<String>,
        request: Int,
    ): Boolean {
        val permissionCheck = ActivityCompat.checkSelfPermission(activity, permission)
        return if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(activity, permissions, request)
            }
            false
        } else {
            true
        }
    }


    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireActivity())
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton(
                "App Settings"
            ) { _, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", context?.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun openGallery() {
        getContent.launch("image/*")
    }


    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val contentResolver: ContentResolver = requireActivity().contentResolver
                val type = contentResolver.getType(it)
                imageUri = it

                val fileNameimg = "${System.currentTimeMillis()}.png"

                val tempFile = File.createTempFile("payment", fileNameimg, null)
                imageFile = tempFile
                val inputstream = contentResolver.openInputStream(uri)
                tempFile.outputStream().use { result ->
                    inputstream?.copyTo(result)
                }
                val requestBody: RequestBody = tempFile.asRequestBody(type?.toMediaType())
                imageMultiPart =
                    MultipartBody.Part.createFormData("file", tempFile.name, requestBody)

                postProfileImage(token, imageMultiPart!!)
            }
        }

    private fun showProgressingView() {
        if (!isProgressShowing) {
            isProgressShowing = true
            progressView = layoutInflater.inflate(R.layout.progress_bar, null) as ViewGroup
            val v: View = requireView().rootView
            val viewGroup = v as ViewGroup
            viewGroup.addView(progressView)
        }
    }

    private fun hideProgressingView() {
        val v: View = requireView().rootView
        val viewGroup = v as ViewGroup
        viewGroup.removeView(progressView)
        isProgressShowing = false
    }

    private fun getOfflineProfile() {
            getProfileImage(token)

            val data = profileDB?.profileDao()?.getProfile()

            binding.tvUsername.text = data?.username
            binding.tvKtp.text = data?.no_ktp
            binding.email.text = data?.email
            binding.address.text = data?.address

            //setting date
            val date = data?.date_of_birth
            val birthDateSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val xBirthDateSdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val xBirthDate = birthDateSdf.parse(date) //to date
            val formattedBirthDate = xBirthDateSdf.format(xBirthDate)//to string with second format

            binding.birthDate.text = formattedBirthDate


    }

    private fun addNote(id : Int, no_ktp: String, gender : String, date_of_birth :String, address: String, email : String, name : String, username : String){
            profileDB!!.profileDao().insertProfile(ProfileData(id, no_ktp, gender, date_of_birth, address, email, name, username))
    }
}