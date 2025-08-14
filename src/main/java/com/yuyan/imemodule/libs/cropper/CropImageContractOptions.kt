package com.yuyan.imemodule.libs.cropper

import android.net.Uri

data class CropImageContractOptions(
    val uri: Uri?,
    val cropImageOptions: CropImageOptions,
)
