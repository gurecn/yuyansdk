package com.yuyan.imemodule.libs.cropper

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.yuyan.imemodule.libs.cropper.CropImageView.CropResult

/**
 * Helper to simplify crop image work like starting pick-image activity and handling camera/gallery
 * intents.<br></br>
 * The goal of the helper is to simplify the starting and most-common usage of image cropping and
 * not all-purpose all possible scenario one-to-rule-them-all code base. So feel free to use it as
 * is and as a wiki to make your own.<br></br>
 * Added value you get out-of-the-box is some edge case handling that you may miss otherwise, like
 * the stupid-ass Android camera result URI that may differ from version to version and from device
 * to device.
 */
object CropImage {

  /**
   * The key used to pass crop image source URI to [CropImageActivity].
   */
  const val CROP_IMAGE_EXTRA_SOURCE = "CROP_IMAGE_EXTRA_SOURCE"

  /**
   * The key used to pass crop image options to [CropImageActivity].
   */
  const val CROP_IMAGE_EXTRA_OPTIONS = "CROP_IMAGE_EXTRA_OPTIONS"

  /**
   * The key used to pass crop image bundle data to [CropImageActivity].
   */
  const val CROP_IMAGE_EXTRA_BUNDLE = "CROP_IMAGE_EXTRA_BUNDLE"

  /**
   * The key used to pass crop image result data back from [CropImageActivity].
   */
  const val CROP_IMAGE_EXTRA_RESULT = "CROP_IMAGE_EXTRA_RESULT"

  /**
   * The result code used to return error from [CropImageActivity].
   */
  const val CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE = 204

  /**
   * Result data of Crop Image Activity.
   */
  open class ActivityResult : CropResult, Parcelable {

    constructor(
      originalUri: Uri?,
      uriContent: Uri?,
      error: Exception?,
      cropPoints: FloatArray?,
      cropRect: Rect?,
      rotation: Int,
      wholeImageRect: Rect?,
      sampleSize: Int,
    ) : super(
      originalUri = originalUri,
      bitmap = null,
      uriContent = uriContent,
      error = error,
      cropPoints = cropPoints!!,
      cropRect = cropRect,
      wholeImageRect = wholeImageRect,
      rotation = rotation,
      sampleSize = sampleSize,
    )

    @Suppress("DEPRECATION")
    protected constructor(`in`: Parcel) : super(
      originalUri = `in`.readParcelable<Parcelable>(Uri::class.java.classLoader) as Uri?,
      bitmap = null,
      uriContent = `in`.readParcelable<Parcelable>(Uri::class.java.classLoader) as Uri?,
      error = `in`.readSerializable() as Exception?,
      cropPoints = `in`.createFloatArray()!!,
      cropRect = `in`.readParcelable<Parcelable>(Rect::class.java.classLoader) as Rect?,
      wholeImageRect = `in`.readParcelable<Parcelable>(Rect::class.java.classLoader) as Rect?,
      rotation = `in`.readInt(),
      sampleSize = `in`.readInt(),
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
      dest.writeParcelable(originalUri, flags)
      dest.writeParcelable(uriContent, flags)
      dest.writeSerializable(error)
      dest.writeFloatArray(cropPoints)
      dest.writeParcelable(cropRect, flags)
      dest.writeParcelable(wholeImageRect, flags)
      dest.writeInt(rotation)
      dest.writeInt(sampleSize)
    }

    override fun describeContents(): Int = 0

    companion object {

      @JvmField
      val CREATOR: Parcelable.Creator<ActivityResult?> =
        object : Parcelable.Creator<ActivityResult?> {
          override fun createFromParcel(`in`: Parcel): ActivityResult =
            ActivityResult(`in`)

          override fun newArray(size: Int): Array<ActivityResult?> = arrayOfNulls(size)
        }
    }
  }

  object CancelledResult : CropResult(
    originalUri = null,
    bitmap = null,
    uriContent = null,
    error = CropException.Cancellation(),
    cropPoints = floatArrayOf(),
    cropRect = null,
    wholeImageRect = null,
    rotation = 0,
    sampleSize = 0,
  )
}
