package com.inah_wook.findwheelchair;

import android.Manifest;
import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.inah_wook.findwheelchair.databinding.ActivityMainBinding;
import com.inah_wook.findwheelchair.databinding.BottomSheetLayoutBinding;
import com.inah_wook.findwheelchair.databinding.RecyclerItemBinding;

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

        // ????????? ?????? ??????
        // https://leveloper.tistory.com/166
        // ?????? : (????????????) setColorFilter > imageTintList

        if(G.airChrge) holder.itemBinding.ivAir.setColorFilter(Color.GREEN);
        else  holder.itemBinding.ivAir.setColorFilter(Color.RED);

        if(G.phoneChrge) holder.itemBinding.ivPhone.setColorFilter(Color.GREEN);
        else holder.itemBinding.ivPhone.setColorFilter(Color.RED);

        if(G.useP) holder.itemBinding.ivUse.setColorFilter(Color.GREEN);
        else holder.itemBinding.ivUse.setColorFilter(Color.RED);



    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    //?????????
    class VH extends RecyclerView.ViewHolder {
        RecyclerItemBinding itemBinding;
        ActivityMainBinding binding;
        BottomSheetLayoutBinding bottomSheetBinding;
        MainActivity mainActivity = (MainActivity) context;
        BottomSheetDialog bottomSheetDialog;
        FragmentManager fragmentManager;

        FusedLocationProviderClient providerClient; // ?????? ????????? ??????????????? ????????? ??????
        public Location location; // ????????????

        public VH(@NonNull View itemView) {
            super(itemView);
            itemBinding = RecyclerItemBinding.bind(itemView);
            fragmentManager = mainActivity.getSupportFragmentManager();

            if (itemView == null) ; //TODO A. ??????????????? ??? ???????????? ?????? ????????? + B. ???????????? ????????? ????????? ????????? ?????? ?????????


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
            }); // itemView ?????? ?????????
        }//?????????


        private void creatBottomSheet(RecyclerItem item) {


            View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_layout, null);
            bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetBinding = BottomSheetLayoutBinding.bind(bottomSheetView);

            bottomSheetBinding.btmTv01.setText("?????????" + item.name);
            bottomSheetBinding.btmTv02.setText(item.location); //?????? ??????

            bottomSheetBinding.btmTv04.setText("??????" + item.address);
            bottomSheetBinding.btmTv06.setText("         ?????? : " + item.weekdayOperOpenHhmm + " ~ " + item.weekdayOperColseHhmm); // ?????? ????????????
            bottomSheetBinding.btmTv07.setText("         ????????? : " + item.satOperOperOpenHhmm + " ~ " + item.satOperCloseHhmm);// ????????? ????????????
            bottomSheetBinding.btmTv08.setText("         ????????? : " + item.holidayOperOpenHhmm + " ~ " + item.holidayCloseOpenHhmm);// ?????? ????????????

            bottomSheetBinding.btmTv09.setText("?????? ?????? ???????????? : " + ((item.airInjectorYn.equals("Y")) ? "??????" : "?????????"));
            bottomSheetBinding.btmTv10.setText("????????? ?????? ???????????? : "+((item.phoneChargeB)?"??????":"?????????"));
            bottomSheetBinding.btmTv11.setText("?????? ?????? ???????????? : "+  ((item.smUseB)?"??????":"?????????"));
            bottomSheetBinding.btmTv12.setText(item.institutionName);// ???????????? ???
            bottomSheetBinding.btmTv13.setText(item.institutionPhoneNumber);// ???????????? ????????? ??????

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
                            .draggable(false) // ???????????? ?????? ????????? ????????????.
                            .title(item.name));

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fcltyLatLng, 18)); //??? 1~25


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
                    Log.i("??????????????? ????????? ??????", tel);
                    context.startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                }
            });

        }



            public void bindItem (RecyclerItem item){

                // ???????????? 1??? ?????? ?????? ?????????????????? ???????????? ???????????? ????????? ???????????? ?????? ?????????
                //??? ??????????????? ?????? ??????????????? ??????????????? ?????? ???????????? ??????.

                // RecyclerItem.java ?????? ????????? ????????????, ?????? ?????? xml??? ?????? ????????? ???????????? ??????.
                itemBinding.tv01.setText(item.name);
                itemBinding.tv02.setText(item.location);

                //  LocalTime targetTime = LocalTime.of(int hour, int minute, int second, int nanoOfSecond);
// ????????? ?????? ????????? ??????


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    LocalTime now = LocalTime.now();
                    int hour = now.getHour();
                    int min = now.getMinute();


                }


                //                                        if(item.smUseB== true) itemBinding.tv06.setText("????????????");
                //
                //                                        if(item.run24[0] == true) itemBinding.tv03.setText("?????????");
                //                                        else {
                //                                            if (1 <= item.UserDay && item.UserDay <= 5) {
                //                                                //??????
                //                                                if(item.runTime[0] <= item.UserTime && item.UserTime <= item.runTime[1])itemBinding.tv03.setText("?????????");
                //
                //                                            } else if (item.UserDay == 6) {
                //                                                //?????????
                //                                                if(item.runTime[2] <= item.UserTime && item.UserTime <= item.runTime[3])itemBinding.tv03.setText("?????????");
                //                                            }
                //                                                itemBinding.tv03.setText("?????????");
                //
                //                                                         }

                // if(item.isOpen==true) itemBinding.tv03.setText("?????????");
                //  else itemBinding.tv03.setText("?????????");
                //                                        if(item.airB==true)itemBinding.tv04.setText("????????????");
                //                                        else itemBinding.tv04.setText("????????????");
                //                                        if (item.phoneChargeB == true) itemBinding.tv05.setText("????????????");
                //                                        else itemBinding.tv05.setText("????????????");

                //?????????????????? ????????? ?????????~
                //                                        if(item.isOpen) itemBinding.btnToggle01.setPressed(true);
                //                                        else itemBinding.btnToggle01.setPressed(false);
                //
                //                                        if(item.airB) itemBinding.btnToggle02.setPressed(true);
                //                                        if(item.phoneChargeB) itemBinding.btnToggle03.setPressed(true);
                //                                        if(item.smUseB) itemBinding.btnToggle04.setPressed(true);


            }


        }


    }

