package com.example.climatedashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.switchmaterial.SwitchMaterial;
import android.content.Context;
import android.content.SharedPreferences;

public class Settings4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings4);

        // register all the ImageButtons with their appropriate IDs
        ImageButton back = findViewById(R.id.backB);
        SwitchMaterial active = findViewById(R.id.activeswitch);
        SwitchMaterial wifi = findViewById(R.id.wifswitch);


        // Handle each of the ImageButtons with the OnClickListener
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings4.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}