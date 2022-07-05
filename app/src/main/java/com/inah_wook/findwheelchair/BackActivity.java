package com.inah_wook.findwheelchair;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.inah_wook.findwheelchair.databinding.ActivityBackBinding;

public class BackActivity extends AppCompatActivity {
    // 뒤로가기 키를 누르면 나올 화면
    ActivityBackBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public void onBackPressed() {
        //  super.onBackPressed(); 이거 때문에 바로 꺼지는거임. 부모의 이메소드는 바로 꺼지도록 만들어져있음 이거 지우고 하고픈거 고
        new AlertDialog.Builder(this)
                .setMessage(" 진짜 나갈껴?  ?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("NO", null)
                .create();
    }
}
