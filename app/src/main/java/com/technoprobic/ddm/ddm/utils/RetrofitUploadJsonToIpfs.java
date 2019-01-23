package com.technoprobic.ddm.ddm.utils;

import com.technoprobic.ddm.ddm.model.InfuraIpfsResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitUploadJsonToIpfs {

    @Multipart
    @POST("api/v0/add?pin=false")
    Call<ResponseBody> uploadJson(@Part MultipartBody.Part file, @Part("name") RequestBody requestBody);

    @Multipart
    @POST("api/v0/add?pin=true")
    Call<ResponseBody> uploadAttachment(@Part MultipartBody.Part filePart);

    @Multipart
    @POST("api/v0/add?pin=true")
    Call<InfuraIpfsResponse> uploadAttachment2(@Part MultipartBody.Part filePart);

}
