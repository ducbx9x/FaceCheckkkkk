package vn.vmg.lendingapp.facechecklib.retrofit

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import vn.vmg.lendingapp.facechecklib.model.DataUpLoad
import vn.vmg.lendingapp.facechecklib.model.FrontCheckRequest
import vn.vmg.lendingapp.facechecklib.model.UploadImageResponse

interface RetrofitClient {
//    @GET("https://api/v1/ticket/{ticket}/fillpayment/bank")
//    fun fillBank(
//        @Header("Authorization") auth: String,
//        @Path("ticket", encoded = true) ticket: String?,
//        @Body
//        frontCheckRequest: FrontCheckRequest,
//    ): Call<FrontCheckRequest>

    @POST("/api/v1/ocr/facecheck")
    fun faceCheck(
        @Header("Authorization") auth: String,
        @Body
        frontCheckRequest: FrontCheckRequest
    ): Call<String>

    @Multipart
    @POST("/api/v1/uploadFile")
     fun uploadImage(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        @Part("ticketCode") ticketCode: RequestBody,
    ): Call<DataUpLoad>
}