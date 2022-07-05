package com.inah_wook.findwheelchair;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.inah_wook.findwheelchair.databinding.ActivityMainBinding;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    NavigationView nav;
    ActionBarDrawerToggle drawerToggle;
    DrawerLayout drawerLayout;

    //장애인 휠체어 충전소 위치정보 받아오는 API ~
    String apiKey = "Pmg4W4DEq17wNe%2F4EabMJ28ZLWNet3tl3qwsXcm%2B8f6%2FMoQOlAenP3cBwR5lAF4z3g%2F5HnkFXgai%2Fhnzcw5G7Q%3D%3D";
    String ctprvnNm =""; //지역 이름
    String institutionPhoneNumber = ""; // 폰넘버

    ArrayList<RecyclerItem> items= new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    //spinner
    Spinner spinner;

    // 전화 퍼미션
    public int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1000;

    //바텀시트
    BottomSheetBehavior bottomSheetBehavior;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int permssionCheck = ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE);

        // 액션바
        androidx.appcompat.widget.Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.layout_drawer);
        drawerToggle= new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open, R.string.drawer_close);
        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);

        drawerToggle= new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open, R.string.drawer_close);
        //삼선모양이 보이도록 토글버튼의 동기 맞추기..
        drawerToggle.syncState();
        //삼선아이콘모양과 화살표아이콘 모양이 자동으로 변환되도록
        drawerLayout.addDrawerListener(drawerToggle);

        nav= findViewById(R.id.nav);
        //네비게이션뷰의 아이템이 선택되었을때 반응하는 리스너
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch ( item.getItemId() ){
                    case R.id.menu_aa:
                        // 플레이스토어 등록정보 이동
                        // 스토어에 등록된 내 앱 딥링크
                        String playstoreUri = "http://play.google.com/store/apps/details?id=com.inah_wook.findwheelchair";
                        Intent intentStore = new Intent(Intent.ACTION_VIEW);
                        intentStore.setData(Uri.parse(playstoreUri));
                        intentStore.setPackage("com.android.vending");
                        startActivity(intentStore);
                        break;

                    case R.id.menu_bb:
                        // 이메일 보내기 이동
                        Intent intentMail = new Intent(Intent.ACTION_SEND);
                        intentMail.setType("*/*");
                        intentMail.putExtra(Intent.EXTRA_EMAIL, "inahpakkr@gmail.com");
                        intentMail.putExtra(Intent.EXTRA_SUBJECT, "[휠체어 앱 문의사항]"); // 메일 제목
                        if (intentMail.resolveActivity(getPackageManager()) != null) {
                            startActivity(intentMail);
                        }
                        break;
                }

                //Drawer 뷰를 닫기..
                drawerLayout.closeDrawer(nav,true);

                return false;
            }
        });

        // 전화 퍼미션
        if (permssionCheck!= PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this,"권한 승인이 필요합니다",Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE))
            {
                Toast.makeText(this,"전화 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 10);
                Toast.makeText(this,"전화 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            }
        }


        // 동적퍼미션
        String[] permissions= new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        int checkResult= checkSelfPermission(permissions[0]);
        if(checkResult == PackageManager.PERMISSION_DENIED){
            requestPermissions(permissions, 10);
        }
//        // 동적퍼미션
//        String[] permissionsCall= new String[]{Manifest.permission.CALL_PHONE};
//        int checkResultCall= checkSelfPermission(permissions[0]);
//        if(checkResultCall == PackageManager.PERMISSION_DENIED){
//            requestPermissions(permissions, 1000);
//        }

        recyclerView = binding.recycler;
        recyclerAdapter = new RecyclerAdapter(this,items,MainActivity.this);
        recyclerView.setAdapter(recyclerAdapter);

        // 스피너 시작
        spinner = binding.spinner;
        spinner();

        loadXmlData("");
        loadJsonData();


    }//on


    // 동적퍼미션 콜백 메소드 - 위치정보 사용에 대한 결과 알려줌
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "위치정보 사용 가능", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "위치정보 사용 불가", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode==1000)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this,"전화 기능 승은 완료",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(this,"전화 기능을 사용할 수 없습니다.",Toast.LENGTH_LONG).show();

            }
        }

    }


    private void spinner() {

        ArrayAdapter spinnerAdapter;
        ArrayList<String> spinner_items;

        spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.spinner_city,R.layout.spinner);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String name = "";
                try {
                    name = URLEncoder.encode(spinner.getSelectedItem().toString(),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ctprvnNm = "&ctprvnNm=" + name;

                Log.println(Log.INFO,"이 값이 바꼈어 ~",ctprvnNm);

                loadXmlData(""); // 여기서
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    } //spinner()

    Handler handler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(@NonNull Message msg) {
            Intent intent = new Intent(MainActivity.this,BackActivity.class);
            startActivity(intent);
            finish();
        }
    }; //핸들러 객체

    @RequiresApi(api = Build.VERSION_CODES.O)


    void loadXmlData(String chip){

        String address="http://api.data.go.kr/openapi/tn_pubr_public_electr_whlchairhgh_spdchrgr_api"
                +"?serviceKey=" + apiKey
                +"&pageNo=" + "0"
                +"&numOfRows=100"
                +"&type=xml"
                +ctprvnNm
                +chip;

        ArrayList<LocalDate> dateTimes = new ArrayList<LocalDate>(); // 0,1 -> 평일, 2,3 -> 토요일 , 4,5 -> 홀리데이~~우왕
        ArrayList<LocalTime> localTimes = new ArrayList<>();
        String[] tmpTime = new String[6];

        items.clear(); // 로드 함수 시작시 기존 데이터 모두 클리어.
        if(recyclerAdapter != null)  recyclerAdapter.notifyDataSetChanged();

        Thread t= new Thread(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {

                try {
                    URL url = new URL(address);

                    InputStream is= url.openStream(); //바이트 스트림
                    InputStreamReader isr= new InputStreamReader(is);//문자 스트림

                    XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
                    XmlPullParser xpp= factory.newPullParser();
                    xpp.setInput(isr);

                    //xpp를 이용하여 설정된 서버에 xml문서를 분석하는 작업 수행

                    int eventType= xpp.getEventType();

                    RecyclerItem xmlItems = null;

                    //xml문서의 끝까지 반복적으로 읽어와서 분석
                    while (eventType != XmlPullParser.END_DOCUMENT){

                        //eventType에 따라 원하는 작업코드 수행
                        switch (eventType){
                            case XmlPullParser.START_DOCUMENT:
                                //화면(UI)변경 작업은 main(ui) thread 만 할 수 있음.
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //이 영역안에서는 UI작업 가능함
                                        Log.i("파싱", "파싱 시작");
                                        binding.progressbar.setVisibility(View.VISIBLE);
                                    }
                                });
                                break;

                            case XmlPullParser.START_TAG:
                                String tagName= xpp.getName();
                                String[] tmp = new String[2];

                                DecimalFormat df = new DecimalFormat("00");
                                int UserTime = Integer.parseInt(df.format(LocalDateTime.now().getHour()) + "" + df.format(LocalDateTime.now().getMinute()));//현재 시간이 0000 형식으로 저장되어 이땀
                                int UserDay =  LocalDate.now().getDayOfWeek().getValue(); //현재 사용자 요일

                                if(tagName.equals("item")){
                                    xmlItems= new RecyclerItem();
                                }else if(tagName.equals("fcltyNm")){
                                    xpp.next();
                                    if(xmlItems!=null) xmlItems.name= xpp.getText();
                                }else if(tagName.equals("instlLcDesc")){
                                    xpp.next();
                                    if(xmlItems!=null) xmlItems.location= xpp.getText();
                                }else if(tagName.equals("institutionNm")){
                                    xpp.next();
                                    if(xmlItems!=null) xmlItems.institutionName= xpp.getText();
                                }
                                else if(tagName.equals("rdnmadr")){
                                    xpp.next();
                                    if(xmlItems!=null) xmlItems.address= xpp.getText(); //주소
                                }
                                else if(tagName.equals("latitude")){
                                    xpp.next();
                                    if(xmlItems!=null) xmlItems.lat= xpp.getText(); //위도
                                }
                                else if(tagName.equals("longitude")){
                                    xpp.next();
                                    if(xmlItems!=null) xmlItems.lng= xpp.getText(); //경도
                                }
                                else if(tagName.equals("institutionPhoneNumber")){
                                    xpp.next();
                                    if(xmlItems!=null){
                                        xmlItems.institutionPhoneNumber= xpp.getText();
                                    }
                                }else if(tagName.equals("ctprvnNm")){
                                    xpp.next();
                                    if(xmlItems!=null) xmlItems.ctprvnNm= xpp.getText();
                                }else if(tagName.equals("smtmUseCo")){ //동시사용 대수
                                    xpp.next();
                                    if(xmlItems!=null) xmlItems.smtmUseCo= xpp.getText();
                                    xmlItems.smUseB =(xmlItems.smtmUseCo.equals("2")) ? true: false;
                                    G.useP = (xmlItems.smUseB)?true:false;

                                }else if(tagName.equals("airInjectorYn")){ //공기주입가능여부
                                    xpp.next();
                                    if(xmlItems!=null) xmlItems.airInjectorYn= xpp.getText();
                                    xmlItems.airB = ( xmlItems.airInjectorYn.equals("Y"))? true:false;
                                    G.airChrge =(xmlItems.airB)? true:false;
                                }else if(tagName.equals("moblphonChrstnYn")){ //핸드폰 충전 가능 여부
                                    xpp.next();
                                    if(xmlItems!=null) xmlItems.moblphonChrstnYn= xpp.getText();
                                    xmlItems.phoneChargeB = (xmlItems.moblphonChrstnYn.equals("Y"))? true:false;
                                    G.phoneChrge = (xmlItems.phoneChargeB)?true:false;
                                    //시간 설정--------------------------------------------------------------
                                }else if(tagName.equals("weekdayOperOpenHhmm")){
                                    xpp.next();
                                    if(xmlItems!=null) xmlItems.weekdayOperOpenHhmm= xpp.getText();
                                    tmp = xmlItems.weekdayOperOpenHhmm.split(":");
                                    xmlItems.runTime[0] = Integer.parseInt(tmp[0]+tmp[1]);

                                }else if(tagName.equals("weekdayOperColseHhmm")){
                                    xpp.next();
                                    if(xmlItems!=null) xmlItems.weekdayOperColseHhmm= xpp.getText();
                                    tmp = xmlItems.weekdayOperColseHhmm.split(":");
                                    xmlItems.runTime[1] = Integer.parseInt(tmp[0]+tmp[1]);

                                    xmlItems.run24[0] = (xmlItems.runTime[0] == xmlItems.runTime[1])? true : false;

                                }else if(tagName.equals("satOperOperOpenHhmm")){
                                    xpp.next();
                                    if(xmlItems!=null) xmlItems.satOperOperOpenHhmm= xpp.getText();
                                    tmp = xmlItems.satOperOperOpenHhmm.split(":");
                                    xmlItems.runTime[2] = Integer.parseInt(tmp[0]+tmp[1]);
                                }else if(tagName.equals("satOperCloseHhmm")){
                                    xpp.next();
                                    if(xmlItems!=null) xmlItems.satOperCloseHhmm= xpp.getText();
                                    tmp = xmlItems.satOperCloseHhmm.split(":");
                                    xmlItems.runTime[3] = Integer.parseInt(tmp[0]+tmp[1]);
                                    xmlItems.run24[1] = (xmlItems.runTime[2] == xmlItems.runTime[3])? true : false;
                                }else if(tagName.equals("holidayOperOpenHhmm")){
                                    xpp.next();
                                    if(xmlItems!=null) xmlItems.holidayOperOpenHhmm= xpp.getText();
                                    tmp = xmlItems.holidayOperOpenHhmm.split(":");
                                    xmlItems.runTime[4] = Integer.parseInt(tmp[0]+tmp[1]);
                                }else if(tagName.equals("holidayCloseOpenHhmm")){
                                    xpp.next();
                                    if(xmlItems!=null) xmlItems.holidayCloseOpenHhmm= xpp.getText();
                                    tmp = xmlItems.holidayCloseOpenHhmm.split(":");
                                    xmlItems.runTime[5] = Integer.parseInt(tmp[0]+tmp[1]);
                                    xmlItems.run24[2] = (xmlItems.runTime[4] == xmlItems.runTime[5])? true : false;
                                }
                                break;

                            case XmlPullParser.END_TAG:
                                String tagName2= xpp.getName();
                                binding.progressbar.setVisibility(View.INVISIBLE);
                                if(tagName2.equals("item")){
                                    if(xmlItems!=null) items.add(xmlItems);
                                }
                                break;
                        }

                        eventType= xpp.next();
                    }//while..


//     단, 화면갱신은 반드시 ui thread 에서만 해야 함.

                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            recyclerAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }

        };
        t.start();

    }
    void loadJsonData(){
        // Json 을 파싱해오기 위해 Gson 과 Retrofit 사용

        JsonArray jsonArray = new JsonArray();
        Gson gson = new Gson();


    }





}//액티비티