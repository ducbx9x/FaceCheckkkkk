package vn.vmg.lendingapp.facechecklib.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class DataUpLoad(
    var message:String,
    var data: UploadImageResponse
)
@Parcelize
data class UploadImageResponse(
    var id: String?,
    var fileName: String?,
    var fileDownloadUri: String?,
    var fileType: String?,
    var size: Int?,
) : Parcelable
