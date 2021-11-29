package vn.vmg.lendingapp.facechecklib.retrofit

import android.util.Log
import android.widget.Toast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vn.vmg.lendingapp.facechecklib.FaceCheckView.Companion.mFaceCheckRes
import vn.vmg.lendingapp.facechecklib.FaceCheckView.Companion.mUploadFile
import vn.vmg.lendingapp.facechecklib.model.DataUpLoad
import vn.vmg.lendingapp.facechecklib.model.FrontCheckRequest
import vn.vmg.lendingapp.facechecklib.model.UploadImageResponse
import java.io.File

class BaseRetrofit {

    val builder = Retrofit.Builder().baseUrl("http://113.190.246.14:48081")
        .addConverterFactory(GsonConverterFactory.create())
    val retrofit = builder.build()
    val client = retrofit.create(RetrofitClient::class.java)

    // 1 api bất kì
    fun faceCheck(auth: String, frontCheckRequest: FrontCheckRequest) {
        val call = client.faceCheck(auth, frontCheckRequest)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                mFaceCheckRes.value = response.body()
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
//                TODO("Not yet implemented")
            }


        })
    }

    fun uploadFile(auth: String, file: File, ticketCode: String) {
        val requestFile: RequestBody =
            file.absoluteFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val ticketCode2 = ticketCode.toRequestBody()
        val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val call = client.uploadImage(
            auth = auth,
            file = multipartBody,
            ticketCode = ticketCode2
        )

        call.enqueue(object : Callback<DataUpLoad> {
            override fun onResponse(
                call: Call<DataUpLoad>,
                response: Response<DataUpLoad>
            ) {
                mUploadFile.value = response.body()
            }

            override fun onFailure(call: Call<DataUpLoad>, t: Throwable) {
                Log.e("loi: ",t.message.toString())

            }


        })
    }
}