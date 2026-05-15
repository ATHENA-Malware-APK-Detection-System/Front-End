package com.athena.skripsi

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.athena.skripsi.databinding.FragmentUploadBinding
import java.io.File
import android.widget.Toast

class UploadFragment : Fragment() {

    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding!!

    private var selectedFileUri: Uri? = null
    private var selectedFileName: String = ""
    private var selectedFileSize: Long = 0L

    private lateinit var apiClient: ApiClient

    // State machine
    enum class UiState { IDLE, FILE_SELECTED, UPLOADING, SCANNING }
    private var currentState = UiState.IDLE

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                handleFileSelected(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        updateUiState(UiState.IDLE)

        apiClient = ApiClient(requireContext())
    }

    private fun setupClickListeners() {
        binding.btnChooseFile.setOnClickListener { openFilePicker() }
        binding.uploadDropZone.setOnClickListener {
            if (currentState == UiState.IDLE) openFilePicker()
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/vnd.android.package-archive"
            addCategory(Intent.CATEGORY_OPENABLE)
//            // Also allow any file type as fallback
//            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
//                "application/vnd.android.package-archive",
//                "application/octet-stream",
//                "*/*"
//            ))
        }
        filePickerLauncher.launch(Intent.createChooser(intent, "Select APK File"))
    }

    private fun handleFileSelected(uri: Uri) {
        // Ambil nama file & ukuran dari ContentResolver
        var fileName = ""
        var fileSize = 0L

        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                val sizeIndex = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
                if (nameIndex >= 0) fileName = it.getString(nameIndex) ?: ""
                if (sizeIndex >= 0) fileSize = it.getLong(sizeIndex)
            }
        }

        // Fallback jika nama kosong
        if (fileName.isEmpty()) {
            fileName = uri.lastPathSegment ?: "selected_file.apk"
        }

        // Validasi ekstensi .apk
        if (!fileName.endsWith(".apk", ignoreCase = true)) {
            Toast.makeText(requireContext(), "Please select a valid .APK file", Toast.LENGTH_SHORT).show()
            return
        }

        // Simpan ke variable & lanjut proses
        selectedFileUri = uri
        selectedFileName = fileName
        selectedFileSize = fileSize

        updateUiState(UiState.UPLOADING)
        simulateUploadThenScan()
    }

    private fun simulateUploadThenScan() {
        var uploadProgress = 0
        val uploadHandler = Handler(Looper.getMainLooper())
        val uploadRunnable = object : Runnable {
            override fun run() {
                uploadProgress += 5
                binding.progressBarUpload.progress = uploadProgress
                binding.tvUploadProgress.text = "$uploadProgress%"

                if (uploadProgress < 100) {
                    uploadHandler.postDelayed(this, 75)
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        updateUiState(UiState.SCANNING)
                        runInference() // ← panggil API
                    }, 300)
                }
            }
        }
        uploadHandler.post(uploadRunnable)
    }

//    private fun simulateScanning() {
//        // Simulate scanning (2.5s) then navigate to result
//        Handler(Looper.getMainLooper()).postDelayed({
//            navigateToResult()
//        }, 2500)
//    }

    private fun runInference() {
        apiClient.scan(
            uri = selectedFileUri!!,
            fileName = selectedFileName,
            onSuccess = { result ->
                requireActivity().runOnUiThread {
                    navigateToResult(result)
                }
            },
            onError = { errorMsg ->
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
                    updateUiState(UiState.IDLE)
                }
            }
        )
    }

    private fun navigateToResult(result: ApiClient.ScanResult) {
        val fileSizeKb = selectedFileSize / 1024
        val fileSizeMb = fileSizeKb / 1024.0

        val action = UploadFragmentDirections.actionUploadFragmentToResultFragment(
            isMalware = result.isMalware,
            fileName = selectedFileName,
            fileSize = if (fileSizeMb >= 1.0) String.format("%.2f MB", fileSizeMb)
            else "$fileSizeKb KB",
            scanDuration = "—",
            confidence = result.confidence
        )
        findNavController().navigate(action)
    }

    private fun updateUiState(state: UiState) {
        currentState = state
        when (state) {
            UiState.IDLE -> showIdleState()
            UiState.FILE_SELECTED -> showFileSelectedState()
            UiState.UPLOADING -> showUploadingState()
            UiState.SCANNING -> showScanningState()
        }
    }

    private fun showIdleState() {
        binding.apply {
            groupIdle.visibility = View.VISIBLE
            groupUploading.visibility = View.GONE
            groupScanning.visibility = View.GONE
            btnChooseFile.visibility = View.VISIBLE
        }
    }

    private fun showFileSelectedState() {
        showIdleState()
    }

    private fun showUploadingState() {
        binding.apply {
            groupIdle.visibility = View.GONE
            groupUploading.visibility = View.VISIBLE
            groupScanning.visibility = View.GONE
            btnChooseFile.visibility = View.GONE
            tvUploadingFileName.text = selectedFileName
            progressBarUpload.progress = 0
            tvUploadProgress.text = "0%"
        }
    }

    private fun showScanningState() {
        binding.apply {
            groupIdle.visibility = View.GONE
            groupUploading.visibility = View.GONE
            groupScanning.visibility = View.VISIBLE
            btnChooseFile.visibility = View.GONE
            tvScanningFileName.text = selectedFileName

            // Start pulse animation on scanning circle
            val pulse = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse)
            scanningCircle.startAnimation(pulse)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}