package com.inah_wook.findwheelchairproject;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.inah_wook.findwheelchairproject.databinding.ActivityMainBinding;
import com.inah_wook.findwheelchairproject.databinding.BottomSheetLayoutBinding;
import com.inah_wook.findwheelchairproject.databinding.RecyclerItemBinding;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.VH> {


    Context context;
    ArrayList<RecyclerItem> items;
    Activity activity;


    public RecyclerAdapter(Context context, ArrayList<RecyclerItem> items, Activity activity) {
        this.context = context;
        this.items = items;
        this.activity = activity;

    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new VH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

        int recyclerPosition = holder.getAdapterPosition();

        holder.bindItem(items.get(position));

        // 이미지 색깔 변경
        // https://leveloper.tistory.com/166
        holder.itemBinding.ivAir.setColorFilter(Color.parseColor("#FFFFFF"));

    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    //뷰홀더
    class VH extends RecyclerView.ViewHolder {
        RecyclerItemBinding itemBinding;
        ActivityMainBinding binding;
        BottomSheetLayoutBinding bottomSheetBinding;
        MainActivity mainActivity = (MainActivity) context;
        BottomSheetDialog bottomSheetDialog;
        FragmentManager fragmentManager;

        FusedLocationProviderClient providerClient; // 위치 정보를 획득해오는 제공자 객체
        public Location location; // 위치정보

        public VH(@NonNull View itemView) {
            super(itemView);
            itemBinding = RecyclerItemBinding.bind(itemView);
            fragmentManager = mainActivity.getSupportFragmentManager();

            if (itemView == null) ; //TODO A. 다이알로그 빈 화면으로 하나 만들기 + B. 메인화면 데이터 없다고 텍스트 하나 만들기


            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    RecyclerItem item = items.get(pos);

                    String callNum = item.institutionPhoneNumber;
                    Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+callNum));

                    creatBottomSheet(item);

                    bottomSheetDialog.show();
                    notifyItemChanged(pos);
                }
            }); // itemView 클릭 리스너
        }//생성자


        private void creatBottomSheet(RecyclerItem item) {


            View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_layout, null);
            bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetBinding = BottomSheetLayoutBinding.bind(bottomSheetView);

            bottomSheetBinding.btmTv01.setText("시설명" + item.name);
            bottomSheetBinding.btmTv02.setText(item.location); //설치 장소

            bottomSheetBinding.btmTv04.setText("주소" + item.address);
            bottomSheetBinding.btmTv06.setText("         평일 : " + item.weekdayOperOpenHhmm + " ~ " + item.weekdayOperColseHhmm); // 평일 영업시간
            bottomSheetBinding.btmTv07.setText("         토요일 : " + item.satOperOperOpenHhmm + " ~ " + item.satOperCloseHhmm);// 토요일 영업시간
            bottomSheetBinding.btmTv08.setText("         공휴일 : " + item.holidayOperOpenHhmm + " ~ " + item.holidayCloseOpenHhmm);// 주말 영업시간

            bottomSheetBinding.btmTv09.setText("공기 주입 가능여부 : " + ((item.airInjectorYn.equals("Y")) ? "가능" : "불가능"));
            bottomSheetBinding.btmTv10.setText("휴대폰 충전 가능여부 : "+((item.phoneChargeB)?"가능":"불가능"));
            bottomSheetBinding.btmTv11.setText("동시 주입 가능여부 : "+  ((item.smUseB)?"가능":"불가능"));
            bottomSheetBinding.btmTv12.setText(item.institutionName);// 관리기관 명
            bottomSheetBinding.btmTv13.setText(item.institutionPhoneNumber);// 관리기관 핸드폰 번호

            bottomSheetDialog.setContentView(bottomSheetView);

            fragmentManager = mainActivity.getSupportFragmentManager();
            SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
            mapFragment.getMapAsync(new OnMapReadyCallback() {

                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }


                    googleMap.setMyLocationEnabled(false);

                    LatLng fcltyLatLng = new LatLng((Double.parseDouble(item.lat)), (Double.parseDouble(item.lng)));


                    googleMap.addMarker(new MarkerOptions()
                            .position(fcltyLatLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_storm))
                            .draggable(false) // 사용자의 마커 드래그 불허한다.
                            .title(item.name));

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fcltyLatLng, 18)); //줌 1~25


                }


            }); // map

            bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {

                    fragmentManager.beginTransaction().remove(mapFragment).commit();

                }
            });

            bottomSheetBinding.btmCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String tel = "tel:"+item.institutionPhoneNumber;
                    Log.i("다이알로그 폰넘버 클릭", tel);
                    context.startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                }
            });

        }



            public void bindItem (RecyclerItem item){

                // 아이템뷰 1개 안에 있는 자식뷰들에게 전달받은 아이템의 값들을 연결하는 기능 메소드
                //뷰 바인딩으로 이미 자식뷰들의 참조값들이 모두 연결되어 있음.

                // RecyclerItem.java 에서 정의한 변수들과, 내가 만든 xml의 여러 뷰들을 연결하는 작업.
                itemBinding.tv01.setText(item.name);
                itemBinding.tv02.setText(item.location);

                //  LocalTime targetTime = LocalTime.of(int hour, int minute, int second, int nanoOfSecond);
// 숫자로 시간 데이터 설정


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    LocalTime now = LocalTime.now();
                    int hour = now.getHour();
                    int min = now.getMinute();


                }


                //                                        if(item.smUseB== true) itemBinding.tv06.setText("동시가능");
                //
                //                                        if(item.run24[0] == true) itemBinding.tv03.setText("영업중");
                //                                        else {
                //                                            if (1 <= item.UserDay && item.UserDay <= 5) {
                //                                                //평일
                //                                                if(item.runTime[0] <= item.UserTime && item.UserTime <= item.runTime[1])itemBinding.tv03.setText("영업중");
                //
                //                                            } else if (item.UserDay == 6) {
                //                                                //토요일
                //                                                if(item.runTime[2] <= item.UserTime && item.UserTime <= item.runTime[3])itemBinding.tv03.setText("영업중");
                //                                            }
                //                                                itemBinding.tv03.setText("영업끝");
                //
                //                                                         }

                // if(item.isOpen==true) itemBinding.tv03.setText("영업중");
                //  else itemBinding.tv03.setText("영업끝");
                //                                        if(item.airB==true)itemBinding.tv04.setText("주입가능");
                //                                        else itemBinding.tv04.setText("주입불가");
                //                                        if (item.phoneChargeB == true) itemBinding.tv05.setText("충전가능");
                //                                        else itemBinding.tv05.setText("충전불가");

                //영업시간이면 이미지 바꾸기~
                //                                        if(item.isOpen) itemBinding.btnToggle01.setPressed(true);
                //                                        else itemBinding.btnToggle01.setPressed(false);
                //
                //                                        if(item.airB) itemBinding.btnToggle02.setPressed(true);
                //                                        if(item.phoneChargeB) itemBinding.btnToggle03.setPressed(true);
                //                                        if(item.smUseB) itemBinding.btnToggle04.setPressed(true);


            }


        }


    }

