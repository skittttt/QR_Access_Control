package ru.mpei.md.qrscanner.presentation.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.mpei.md.qrscanner.databinding.FragmentScannerBinding
import ru.mpei.md.qrscanner.presentation.viewmodels.ScannerViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class ScannerFragment : Fragment() {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ScannerViewModel by viewModels()

    private var cameraExecutor: ExecutorService? = null

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val TAG = "ScannerFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated called")

        if (allPermissionsGranted()) {
            Log.d(TAG, "Permissions already granted, starting camera")
            startCamera()
        } else {
            Log.d(TAG, "Requesting permissions")
            requestPermissions()
        }

        binding.btnFlashlight.setOnClickListener {
            // Toggle flashlight
        }

        binding.btnSubmit.setOnClickListener {
            viewModel.processAccessDecision(true)
        }

        binding.btnDecline.setOnClickListener {
            viewModel.processAccessDecision(false)
        }

        observeViewModel()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                Log.d(TAG, "Permissions granted, starting camera")
                startCamera()
            } else {
                Log.e(TAG, "Permissions denied")
                // Handle the case where permissions were denied
            }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        Log.d(TAG, "startCamera called")
        cameraExecutor = Executors.newSingleThreadExecutor()
        
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                Log.d(TAG, "Camera provider obtained")

                // Preview
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.previewView.surfaceProvider)
                    }

                // Image Analysis
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor!!, QrCodeAnalyzer { qrCode ->
                            // Handle QR code detection
                            Log.d(TAG, "QR Code detected: $qrCode")
                            // Hide previous results and buttons when new QR is detected
                            binding.resultLayout.visibility = View.GONE
                            binding.btnSubmit.visibility = View.GONE
                            binding.btnDecline.visibility = View.GONE
                            binding.tvCounter.visibility = View.GONE
                            viewModel.processQrCode(qrCode)
                        })
                    }

                // Select back camera
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                val camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
                
                Log.d(TAG, "Camera successfully bound")
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.scanResult.collect { result ->
                    Log.d(TAG, "Scan result updated: $result")
                    result?.let { decision ->
                        Log.d(TAG, "Displaying scan result: User=${decision.userId}, Event=${decision.eventId}, Status=${decision.status}, Counter=${decision.counter}")
                        binding.tvResult.text = "User: ${decision.userId}\nEvent: ${decision.eventId}\nStatus: ${decision.status.name}"

                        // Update the counter TextView - this shows how many people entered BEFORE this person
                        binding.tvCounter.text = "Total People Entered: ${decision.counter}"
                        binding.tvCounter.visibility = View.VISIBLE

                        binding.resultLayout.visibility = View.VISIBLE
                        binding.btnSubmit.visibility = View.VISIBLE
                        binding.btnDecline.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { isLoading ->
                    Log.d(TAG, "Loading state changed: $isLoading")
                    binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collect { error ->
                    Log.d(TAG, "Error received: $error")
                    error?.let { errorMsg ->
                        binding.tvResult.text = "Error: $errorMsg"
                        binding.tvCounter.visibility = View.GONE
                        binding.resultLayout.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.accessDecisionResult.collect { decisionResult ->
                    Log.d(TAG, "Access decision result: $decisionResult")
                    decisionResult?.let {
                        binding.tvResult.text = "Decision: $decisionResult"
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor?.shutdown()
        _binding = null
    }
}