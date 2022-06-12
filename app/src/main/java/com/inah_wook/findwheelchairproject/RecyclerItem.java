package com.inah_wook.findwheelchairproject;



import android.icu.text.SimpleDateFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import android.os.Build;

import androidx.annotation.RequiresApi;

// import com.inah_wook.findwheelchairproject.databinding.RecyclerItemBinding;

import java.util.Date;

public class RecyclerItem {


    //TODO 각 아이템들 이미지 설정
    // 텍스트 뷰들
    String name ="";  //시설 이름
    String location;    //설치 장소
    String institutionName; //관리기간 명
    String institutionPhoneNumber;  // 관리기관 폰넘버
    String ctprvnNm; // 지역 이름
    String address; // 주소

    // 시설 위치값
    String lat; // 위도
    String lng;// 경도


    // 평일 영업시간
    String weekdayOperOpenHhmm;
    String weekdayOperColseHhmm;
    // 주말 영업시간
    String satOperOperOpenHhmm;
    String satOperCloseHhmm;
    // 공휴일 영업시간
    String holidayOperOpenHhmm;
    String holidayCloseOpenHhmm;

    int UserTime ;//현재 시간이 0000 형식으로 저장되어 이땀
    int UserDay;
    int[] runTime = new int[6];
    Boolean[] run24= new Boolean[3];

    // 아이템 색깔만 변경해야 함.
    String smtmUseCo; //동시 사용 가능 대수
    String airInjectorYn; //공기 주입 가능 여부
    String moblphonChrstnYn; // 휴대전화 충전 가능 여부

    // 토글 버튼으로구현 ㄱㄱ
    Boolean isOpen; //영업여부
    Boolean smUseB ; //동시 사용 가능 여부
    Boolean airB; //공기 주입 가능 여부
    Boolean phoneChargeB; // 휴대전화 충전 가능 여부


    // 이미지 뷰들
    int imgResId01;
    int imgResId02;
    int imgResId03;
    int imgResId04;



    @RequiresApi(api = Build.VERSION_CODES.O)
    public RecyclerItem(String name, String location, String address, String institutionName, String institutionPhoneNumber, String ctprvnNm, String smtmUseCo, String airInjectorYn, String moblphonChrstnYn, int imgResId01, int imgResId02, int imgResId03, int imgResId04 , String weekdayOperOpenHhmm,
                        String lat , String lng,
                        String weekdayOperColseHhmm,
                        // 주말 영업시간
                        String satOperOperOpenHhmm,
                        String satOperCloseHhmm,
                        // 공휴일 영업시간
                        String holidayOperOpenHhmm,
                        String holidayCloseOpenHhmm) {
        this.address = address; //주소
        this.name = name;
        this.location = location;
        this.lng = lng;
        this.lat = lat;
        this.institutionName = institutionName;
        this.institutionPhoneNumber = institutionPhoneNumber;
        this.ctprvnNm = ctprvnNm;
        this.smtmUseCo = smtmUseCo;
        this.airInjectorYn = airInjectorYn;
        this.moblphonChrstnYn = moblphonChrstnYn;
        this.UserDay = UserDay;

        this.weekdayOperOpenHhmm = weekdayOperOpenHhmm;
        this.weekdayOperColseHhmm = weekdayOperColseHhmm;
        this.satOperOperOpenHhmm = satOperOperOpenHhmm;
        this.satOperCloseHhmm = satOperCloseHhmm;
        this.holidayOperOpenHhmm = holidayOperOpenHhmm;
        this.holidayCloseOpenHhmm = holidayCloseOpenHhmm;

        this.imgResId01 = imgResId01;
        this.imgResId02 = imgResId02;
        this.imgResId03 = imgResId03;
        this.imgResId04 = imgResId04;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public RecyclerItem(){

        smUseB = false;

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    void setItemBooleans(){


        //---------------------------------------------------------------------------------------------------
        //calendar 의 단점
        //https://sleepyeyes.tistory.com/30

        //공휴일 가져오기 -  LocalDate 이용용
       // https://rangerang.tistory.com/71#:~:text=%EA%B3%B5%ED%9C%B4%EC%9D%BC%20%EC%B2%98%EB%A6%AC%20%ED%81%B4%EB%9E%98%EC%8A%A4,%EB%B9%A8%EB%A6%AC%20%ED%99%95%EC%9D%B8%ED%95%A0%20%EC%88%98%20%EC%9E%88%EB%8F%84%EB%A1%9D%20%ED%96%88%EB%8B%A4.
        // 기존 Data의 업그레이드 버전.
        // API 준다네.. 이걸로 시도해야 하는뎅 ㅜ 기찮..
        // 일단 자바 8 버전에선 캘린더의 업글판 LocalDate를 사용해서 날짜시간 구하고 비교까지 해보자~


//----------------------------------------다른 칩 버튼들 -----------------------------------------------------------
        smUseB = Integer.parseInt(smtmUseCo) == 1; // 동시사용여부
        airB = airInjectorYn.equals("Y"); // 공기주입
        phoneChargeB = moblphonChrstnYn.equals("Y"); //폰충전

//        for(int i = 0 ; i<xmlItems.runTime.length;i+=2 ){
//            xmlItems.run24= (xmlItems.runTime[i] == xmlItems.runTime[i+1])? true:false;
//        }
//
//        if( xmlItems.run24 ==false ){
//
//            // 영업중 여부 판단 코드 작성하기
//
//            if( 1<= Userday && Userday<=5 ) {
//                //평일
//
//                xmlItems.isOpen = ( xmlItems.runTime[0] <= uTime && uTime <=xmlItems.runTime[1])? true: false;
//
//
//            }else if(Userday == 6 ) {
//                //토요일
//                xmlItems.isOpen = ( xmlItems.runTime[2] <= uTime && uTime <=xmlItems.runTime[3])? true: false;
//            }
//            else if(Userday == 7 ){
//                // 일요일
//                xmlItems.isOpen = false;
//            }
//            //공휴일은 나중에 추가하쟝..


    }

}
