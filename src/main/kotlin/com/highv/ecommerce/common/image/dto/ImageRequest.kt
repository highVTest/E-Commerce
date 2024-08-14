package com.highv.ecommerce.common.image.dto

import com.highv.ecommerce.common.image.entity.UsagePath

data class ImageRequest(

    var usagePath: UsagePath,

    var imageName: String,

    )
