package com.binar.c5team.gotravel.view.dialog

import android.content.ContentResolver
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentUploadPaymentBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class PaymentDialog : DialogFragment() {
    lateinit var binding : FragmentUploadPaymentBinding

    //image
    private var imageMultiPart: MultipartBody.Part? = null
    private var imageUri: Uri? = Uri.EMPTY
    private var imageFile: File? = null

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

        dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        binding.imageUploadLayout.setOnClickListener {
            openGallery()
        }

        binding.btnSendImg.setOnClickListener {
            Toast.makeText(context, "Send to Database", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery(){
        getContent.launch("image/*")
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val contentResolver: ContentResolver = requireActivity().contentResolver
                val type = contentResolver.getType(it)
                imageUri = it

                val fileNameimg = "${System.currentTimeMillis()}.png"
                //masukin img ke iv
                binding.addImage.setImageURI(it)

                val tempFile = File.createTempFile("payment", fileNameimg, null)
                imageFile = tempFile
                val inputstream = contentResolver.openInputStream(uri)
                tempFile.outputStream().use    { result ->
                    inputstream?.copyTo(result)
                }
                val requestBody: RequestBody = tempFile.asRequestBody(type?.toMediaType())
                imageMultiPart = MultipartBody.Part.createFormData("image", tempFile.name, requestBody)
            }
        }
}