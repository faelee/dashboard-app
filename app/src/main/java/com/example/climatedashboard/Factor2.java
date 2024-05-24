package com.example.climatedashboard;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.Intent;
import android.os.Bundle;

public class Factor2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factor2);

        // register all the ImageButtons with their appropriate IDs
        ImageButton back = findViewById(R.id.backB);

        // Handle each of the ImageButtons with the OnClickListener
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Factor2.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}