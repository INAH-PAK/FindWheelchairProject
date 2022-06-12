package com.inah_wook.findwheelchairproject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import retrofit2.http.Body;

public class WeatherVO {

    //단기 강수형태 json 데이터 저장용 클래스

        String fcstValue;// 강수 형태 코드
                        /* (초단기) 없음(0), 비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7) */

        public static class Response implements Serializable{
                @Expose
                @SerializedName("body")
                private Body body;

        }





}
