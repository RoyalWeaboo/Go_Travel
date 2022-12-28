package com.binar.c5team.gotravel.view.dialog

import android.Manifest
import android.app.*
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentUploadPaymentBinding
import com.binar.c5team.gotravel.view.MainActivity
import com.binar.c5team.gotravel.viewmodel.FlightViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*


class PaymentDialog : DialogFragment() {
    lateinit var binding: FragmentUploadPaymentBinding

    //Shared Preferences
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sharedPrefBooking: SharedPreferences

    private var token: String = ""

    //image
    private var imageMultiPart: MultipartBody.Part? = null
    private var imageUri: Uri? = Uri.EMPTY
    private var imageFile: File? = null

    private var bookingIds = ArrayList<Int>()

    //viewmodel
    private lateinit var viewModel: FlightViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUploadPaymentBinding.inflate(inflater, container, false)
        dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //SharedPref for user data
        sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)
        //SharedPref for booking data
        sharedPrefBooking =
            requireActivity().getSharedPreferences("bookingInfo", Context.MODE_PRIVATE)

        bookingIds = arguments?.getIntegerArrayList("bookingIds") as ArrayList<Int>
        Log.d("booking id", bookingIds.toString())

        //getting user data
        token = sharedPref.getString("token", "").toString()

        dialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        viewModel = ViewModelProvider(this)[FlightViewModel::class.java]

        binding.imageUploadLayout.setOnClickListener {
            checkingPermissions()
        }

        binding.btnSendImg.setOnClickListener {
            Log.d("booking id size", bookingIds.size.toString())
            if (bookingIds.size != 0) {
                for (i in bookingIds) {
                    postImage(token, i, imageMultiPart!!)
                    Log.d("id uploaded", i.toString())
                }
                viewModel.postConfirmationLD().observe(viewLifecycleOwner) {
                    if (it != null) {
                    }
                    notification()
                }

                val builder = android.app.AlertDialog.Builder(context)
                builder.setTitle("Upload Success !")
                builder.setMessage("Proof of Payment successfully uploaded !, Please wait for ticket approval from Admin")

                builder.setPositiveButton("Close") { _, _ ->
                    val navControllerDialog = Navigation.findNavController(requireParentFragment().requireView())
                    navControllerDialog.navigate(R.id.action_paymentDialog_to_homeFragment)

//                    val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
//                    val item = navBar.menu.findItem(R.id.historyFragment)
//                    val navController = Navigation.findNavController(requireParentFragment().requireView())
//                    NavigationUI.onNavDestinationSelected(item, navController)

//                    restartSelf()
                }
                builder.show()

            } else {
                Toast.makeText(context, "Error, cannot retrieve booking id", Toast.LENGTH_SHORT)
                    .show()
                Navigation.findNavController(view)
                    .navigate(R.id.action_paymentDialog_to_homeFragment)
            }
        }
    }

    private fun postImage(token: String, id: Int, imageMultiPart: MultipartBody.Part) {
        viewModel.postConfirmationPaymentImage(token, id, imageMultiPart)
    }

    private fun restartSelf() {
        val am = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am[AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis + 500] =
            PendingIntent.getActivity(
                activity,
                0,
                requireActivity().intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_CANCEL_CURRENT
            )
        val i = requireActivity().baseContext.packageManager
            .getLaunchIntentForPackage(requireActivity().baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
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

                binding.addImage.setImageURI(it)

                val tempFile = File.createTempFile("payment", fileNameimg, null)
                imageFile = tempFile
                val inputstream = contentResolver.openInputStream(uri)
                tempFile.outputStream().use { result ->
                    inputstream?.copyTo(result)
                }
                val requestBody: RequestBody = tempFile.asRequestBody(type?.toMediaType())
                imageMultiPart =
                    MultipartBody.Part.createFormData("file", tempFile.name, requestBody)
            }
        }

    private fun notification() {
        val mBuilder = NotificationCompat.Builder(requireContext().applicationContext, "1")
        val ii = Intent(requireContext().applicationContext, MainActivity::class.java)
        ii.putExtra("redirect", "historyFragment")
        val pendingIntent =
            PendingIntent.getActivity(requireContext().applicationContext, 0, ii, 0)

        val bigText = NotificationCompat.BigTextStyle()
        bigText.setBigContentTitle("Booking Succesful !")
        bigText.setSummaryText("Successful Booking")

        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setSmallIcon(R.drawable.airplane_icon)
        mBuilder.setContentTitle("Upload Succesful !")
        mBuilder.setContentText("Proof of Payment successfully uploaded !, Please wait for ticket approval from Admin")
        mBuilder.setStyle(bigText)
        mBuilder.setDefaults(Notification.DEFAULT_ALL)

        val mNotificationManager: NotificationManager =
            requireContext().applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "1"
        val channel = NotificationChannel(
            channelId,
            "Success Booking Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        mNotificationManager.createNotificationChannel(channel)
        mBuilder.setChannelId(channelId)

        mNotificationManager.notify(0, mBuilder.build())
    }
}