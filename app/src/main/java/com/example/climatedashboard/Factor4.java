package com.example.climatedashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class Factor4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factor4);

        // register all the ImageButtons with their appropriate IDs
        ImageButton back = findViewById(R.id.backB);

        // Handle each of the ImageButtons with the OnClickListener
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Factor4.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}