package com.cyuan.bimibimi.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.cyuan.bimibimi.R
import com.facebook.shimmer.Shimmer


object ShimmerUtils {
  fun getGrayShimmer(context: Context): Shimmer {
    return Shimmer.ColorHighlightBuilder()
      .setBaseColor(ContextCompat.getColor(context, R.color.shimmerBase0))
      .setHighlightColor(ContextCompat.getColor(context, R.color.shimmerHighlight0))
      .setBaseAlpha(0.8f)
      .setHighlightAlpha(0.8f)
      .build()
  }
}
