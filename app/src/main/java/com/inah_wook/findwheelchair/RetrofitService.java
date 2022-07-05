package com.inah_wook.findwheelchair;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {


    @GET("getVilageFcst?serviceKey=$API_KEY")
    Call<WeatherVO> getList(
            @Query("dataType") String dataType,
            @Query("numOfRows") int numOfRows,
            @Query("pageNo")    int pageNo,
            @Query("base_date") int base_date,
            @Query("base_time") int base_time,
            @Query("nx")        int nx,
            @Query("ny")        int ny
    );

}
