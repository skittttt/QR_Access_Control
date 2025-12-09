package ru.mpei.md.qrscanner.presentation.ui.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.mpei.md.qrscanner.databinding.FragmentEventDetailsBinding
import ru.mpei.md.qrscanner.presentation.viewmodels.EventDetailsViewModel
import ru.mpei.md.qrscanner.R
import ru.mpei.md.qrscanner.presentation.ui.fragments.EventDetailsFragmentArgs

@AndroidEntryPoint
class EventDetailsFragment : Fragment() {
    
    private var _binding: FragmentEventDetailsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: EventDetailsViewModel by viewModels()
    private val args: EventDetailsFragmentArgs by navArgs()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel.loadEventDetails(args.eventId)
        observeViewModel()
        
        binding.btnShowQr.setOnClickListener {
            // Generate and show QR code
            viewModel.generateQrCode("U12345", args.eventId) // Default user ID for demo
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    event?.let { e ->
                        binding.apply {
                            tvEventTitle.text = e.title
                            tvEventDateTime.text = e.dateTime
                            tvEventLocation.text = e.location
                            tvEventDescription.text = e.description
                        }
                    }
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.qrCodeContent.collect { qrContent ->
                    qrContent?.let { content ->
                        val bitmap = generateQrCodeBitmap(content)
                        binding.ivQrCode.setImageBitmap(bitmap)
                        binding.ivQrCode.visibility = View.VISIBLE
                        binding.tvQrHint.visibility = View.VISIBLE
                    }
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { isLoading ->
                    binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collect { error ->
                    error?.let {
                        // Show error message
                    }
                }
            }
        }
    }
    
    private fun generateQrCodeBitmap(content: String): Bitmap? {
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 500, 500)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.RGB_565)
            
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            return null
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}