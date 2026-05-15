package com.athena.skripsi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.athena.skripsi.databinding.FragmentResultBinding

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private val args: ResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupResult()
        setupClickListeners()
        playEntryAnimation()
    }

    private fun setupResult() {
        if (args.isMalware) {
            showMalwareResult()
        } else {
            showBenignResult()
        }

        // Populate file info card
        binding.tvFileName.text = args.fileName
        binding.tvFileSize.text = args.fileSize
//        binding.tvScanDuration.text = args.scanDuration
        binding.tvConfidence.text = args.confidence

        // Summary card
        binding.tvResultSummary.text = if (args.isMalware)
            "This file has been identified as potentially malicious. Avoid installing this APK on your device."
        else
            "This file appears to be safe. No malicious patterns were detected during analysis."
    }

    private fun showMalwareResult() {
        binding.apply {
            // Icon
            ivResultIcon.setImageResource(R.drawable.warning)
//            ivResultIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.dark_pink))
//            resultIconBackground.setCardBackgroundColor(
//                ContextCompat.getColor(requireContext(), R.color.dark_pink)
//            )

            // Status text
            tvResultStatus.text = "MALWARE"
            tvResultStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.black80))

//            // Verdict badge
//            tvVerdictBadge.text = "MALWARE"
//            tvVerdictBadge.backgroundTintList =
//                ContextCompat.getColorStateList(requireContext(), R.color.dark_pink)
//            tvVerdictBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_pink))

//            // Threat type
//            tvThreatType.visibility = View.VISIBLE
//            tvThreatTypeLabel.visibility = View.VISIBLE
//            tvThreatType.text = "Trojan.AndroidOS"
        }
    }

    private fun showBenignResult() {
        binding.apply {
            // Icon
            ivResultIcon.setImageResource(R.drawable.checklist)
//            ivResultIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.light_pink))
//            resultIconBackground.setCardBackgroundColor(
//                ContextCompat.getColor(requireContext(), R.color.light_pink)
//            )

            // Status text
            tvResultStatus.text = "BENIGN"
            tvResultStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.black80))

//            // Verdict badge
//            tvVerdictBadge.text = "BENIGN"
//            tvVerdictBadge.backgroundTintList =
//                ContextCompat.getColorStateList(requireContext(), R.color.light_pink)
//            tvVerdictBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_pink))

//            // No threat type for benign
//            tvThreatType.visibility = View.GONE
//            tvThreatTypeLabel.visibility = View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.btnScanAnother.setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_uploadFragment)
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun playEntryAnimation() {
        val fadeSlideIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_in)
        binding.cardResultIcon.startAnimation(fadeSlideIn)

        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_delayed)
        binding.cardFileInfo.startAnimation(fadeIn)
        binding.cardSummary.startAnimation(fadeIn)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}