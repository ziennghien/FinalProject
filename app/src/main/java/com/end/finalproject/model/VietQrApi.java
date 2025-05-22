package com.end.finalproject.model;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface VietQrApi {
    @Headers({
            "x-client-id: 4f9bc775-b351-4e10-aa0e-95a3cf40c4e1",
            "x-api-key: e6415764-e350-455f-a87b-98dce6f4036a",
            "Content-Type: application/json"
    })
    @POST("v2/lookup")
    Call<AccountLookupResponse> lookupAccount(@Body AccountLookupRequest body);

    @GET("v2/banks")
    Call<BankResponse> getBanks();

    @POST("transfer")
    Call<TransferResponse> transfer(@Body TransferRequest request);

}
