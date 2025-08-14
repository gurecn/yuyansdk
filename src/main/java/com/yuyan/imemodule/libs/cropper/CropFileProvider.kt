package com.yuyan.imemodule.libs.cropper

import androidx.core.content.FileProvider
import com.yuyan.imemodule.R

/**
 * Providing a custom {@code FileProvider} prevents manifest {@code <provider>} name collisions.
 *
 * See https://developer.android.com/guide/topics/manifest/provider-element.html for details.
 */
class CropFileProvider : FileProvider(R.xml.cropper_library_file_paths) {
  // This class intentionally left blank.
  // https://android-review.googlesource.com/c/platform/frameworks/support/+/1978527
}
