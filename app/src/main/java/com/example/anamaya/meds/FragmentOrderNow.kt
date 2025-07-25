package com.example.anamaya.meds

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.anamaya.R

class FragmentOrderNow : Fragment() {

    private lateinit var imageGalleryBanner: ImageView
    private lateinit var imageGalleryBanner2: ImageView
    private lateinit var adImages: List<Int>
    private var currentImageIndex = 0
    private val handler = Handler(Looper.getMainLooper())
    private val delayMillis = 5000L

    private val imageChanger = object : Runnable {
        override fun run() {
            currentImageIndex = (currentImageIndex + 1) % adImages.size
            imageGalleryBanner.setImageResource(adImages[currentImageIndex])
            imageGalleryBanner2.setImageResource(adImages[currentImageIndex])
            handler.postDelayed(this, delayMillis)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.meds_fragment_order_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageGalleryBanner = view.findViewById(R.id.imageGalleryBanner)
        imageGalleryBanner2 = view.findViewById(R.id.imageGalleryBanner2)

        adImages = listOf(
                R.drawable.ad_1,
        R.drawable.ad_2,
        R.drawable.ad_3,
        R.drawable.ad_4
        )

//        adImages2 = listOf(
//            R.drawable.ad_1,
//            R.drawable.ad_2,
//            R.drawable.ad_3,
//            R.drawable.ad_4
//        )

        if (adImages.isNotEmpty()) {
            imageGalleryBanner.setImageResource(adImages[currentImageIndex])
            imageGalleryBanner2.setImageResource(adImages[currentImageIndex])
        }

        val adClickListener = View.OnClickListener {
            Toast.makeText(requireContext(), "Ad banner clicked", Toast.LENGTH_SHORT).show()
        }

        imageGalleryBanner.setOnClickListener(adClickListener)
        imageGalleryBanner2.setOnClickListener(adClickListener)
    }

    override fun onResume() {
        super.onResume()
        if (adImages.isNotEmpty()) {
            handler.postDelayed(imageChanger, delayMillis)
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(imageChanger)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(imageChanger)
    }
}
