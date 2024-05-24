package com.example.climatedashboard;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission_group.LOCATION;
import static android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity { //implements FragmentManager.OnBackStackChangedListener
    private static final int READ_WAIT_MILLIS = 2000;
    private ArrayList<View> inflated = new ArrayList<View>();
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if(intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION) ||
//                    intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION) ||
//                    intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION) ||
//                    intent.getAction().equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION) ||
//                    intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
//                setWifiIndicator();
//            }
//        }
//    };

    private void setWifiIndicator(View view) {
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        //getApplicationContext solves memory leak issues prior to Android N (must use Application Context to get wifi system service.
        WifiManager wifiMgr = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        TextView wifiSSID = (TextView)view.findViewById(R.id.image_view20ci);

        try {
            if (wifiMgr != null) {
                if (wifiMgr.isWifiEnabled()) {
                    //wifi is enabled.  toggle on wifi indicator.
                    String ssid = wifiMgr.getConnectionInfo().getSSID();
                    if (ssid != null) {
                        //Log.v(this.getClass().getSimpleName(), "SSID: " + ssid + "  Supplicant State: " + info.getSupplicantState());
                        wifiSSID.setText(ssid);
                    }
                } else {
                    //wifi is disabled.  toggle off wifi indicator.
                    wifiSSID.setText("");
                }
            }
        } catch(Exception e) {
            //catching anything thrown in this block just so it doesn't crash the program unnecessarily
            e.printStackTrace();
        }
    }

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
//    private ActivityResultLauncher<String> requestPermissionLauncher =
//            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
//                if (isGranted) {
//                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_LONG).show(); //setWifiIndicator();
//                } else {
//                    Toast.makeText(MainActivity.this, "Empty", Toast.LENGTH_LONG).show();
//                }
//            });

    ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                ACCESS_COARSE_LOCATION,false);
                        if (fineLocationGranted != null && fineLocationGranted) {
                            // Precise location access granted.
                            Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_LONG).show();
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            // Only approximate location access granted.
                            Toast.makeText(MainActivity.this, "NEED FINE", Toast.LENGTH_LONG).show();
                        } else {
                            // No location access granted.
                            Toast.makeText(MainActivity.this, "Empty", Toast.LENGTH_LONG).show();
                        }
                    }
            );

    private void setCard(View view){
        ImageButton interestsCard = view.findViewById(R.id.plotsci);
        ImageButton interestsCard1 = view.findViewById(R.id.settingsci);
        Button status = view.findViewById(R.id.status);
        ImageButton sound = view.findViewById(R.id.sound);
        ImageButton light = view.findViewById(R.id.light);
        TextView display = view.findViewById(R.id.textView20ci);
        ImageButton plot = view.findViewById(R.id.plotsci);

        interestsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an intent to switch to second activity upon clicking
                Intent intent = new Intent(MainActivity.this, Factor4.class);
                startActivity(intent);
            }
        });

        interestsCard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an intent to switch to second activity upon clicking
                Intent intent = new Intent(MainActivity.this, Settings4.class);
                startActivity(intent);
            }
        });

        plot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an intent to switch to second activity upon clicking
                Intent browserX = new Intent(Intent.ACTION_VIEW, Uri.parse("http://10.42.0.1:5000"));
                startActivity(browserX);
            }
        });

        status.setOnClickListener(new View.OnClickListener() {
            int wificlick = 1;
            @Override
            public void onClick(View v) {
                // create an intent to switch to second activity upon clicking
                wificlick += 1;
                if (wificlick % 2 == 1) {
                    status.setText("DISCONNECTED");
                    status.setTextColor(Color.parseColor("#990000"));
                    status.setBackgroundColor(Color.parseColor("#d3d3d3"));
                    display.setText("INACTIVE");
                    display.setTextColor(Color.parseColor("#636363"));
                }
                else{
                    for (View var : inflated)
                    {
                        Button stat = var.findViewById(R.id.status);
                        TextView disp = var.findViewById(R.id.textView20ci);
                        stat.setText("DISCONNECTED");
                        stat.setTextColor(Color.parseColor("#990000"));
                        stat.setBackgroundColor(Color.parseColor("#d3d3d3"));
                        disp.setText("INACTIVE");
                        disp.setTextColor(Color.parseColor("#636363"));
                    }
                    status.setText("CONNECTED");
                    display.setText("CLEAR");
                    display.setTextColor(Color.parseColor("#0f9d58"));
                    status.setTextColor(Color.parseColor("#0f9d58"));
                    status.setBackgroundColor(Color.parseColor("#d3d3d3"));
                    setWifiIndicator(view);
                }
            }
        });

        sound.setOnClickListener(new View.OnClickListener() {
            int wificlick = 1;
            @Override
            public void onClick(View v) {
                // create an intent to switch to second activity upon clicking
                wificlick += 1;
                if (wificlick % 2 == 1) {
                    sound.setImageResource(R.drawable.speaker);
                }
                else{
                    sound.setImageResource(R.drawable.mute);
                }
            }
        });

        light.setOnClickListener(new View.OnClickListener() {
            int wificlick = 1;
            @Override
            public void onClick(View v) {
                // create an intent to switch to second activity upon clicking
                wificlick += 1;
                if (wificlick % 2 == 1) {
                    light.setImageResource(R.drawable.lighton);
                }
                else{
                    light.setImageResource(R.drawable.lightoff);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction(ScannerService.ACTION_READ_SCANNER);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        //registerReceiver(mReceiver, intentFilter);

        // Register all the card views with their appropriate IDs
//        ImageButton plot = findViewById(R.id.plotsci);
//        ImageButton setting = findViewById(R.id.settingsci);
//        Button status = findViewById(R.id.status);
//        ImageButton sound = findViewById(R.id.sound);
//        ImageButton light = findViewById(R.id.light);
//        TextView display = findViewById(R.id.textView20ci);
        SwitchMaterial switch1 = findViewById(R.id.switch1);

        //        Button setup = findViewById(R.id.abutton);
        ImageButton wifiset = findViewById(R.id.wifiset);
        Button wificon = findViewById(R.id.wificon);
        ImageButton serset = findViewById(R.id.serset);
        Button sercon = findViewById(R.id.sercon);
//        TextView wifi = findViewById(R.id.image_view20ci);
        LinearLayout mainLayout1 = findViewById(R.id.inflater1);

        wificon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an intent to switch to second activity upon clicking
//                  View myLayout = getLayoutInflater().inflate(R.layout.my_layout, mainLayout, false);
//                  mainLayout.addView(myLayout, 0); //new ViewGroup.LayoutParams(
//                  ViewGroup.LayoutParams.WRAP_CONTENT, uu3ViewGroup.LayoutParams.WRAP_CONTENT));

                if (ContextCompat.checkSelfPermission(
                        MainActivity.this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    View myLayout1 = getLayoutInflater().inflate(R.layout.my_layout, mainLayout1, false);
                    mainLayout1.addView(myLayout1, 0); //new ViewGroup.LayoutParams(
                    //ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    inflated.add(myLayout1);
                    setCard(myLayout1);
                } else {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(new String[] {
                            ACCESS_FINE_LOCATION,
                            ACCESS_COARSE_LOCATION});
                }


            }
        });

        wifiset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
                startActivity(intent);
            }
        });

        sercon.setOnClickListener(new View.OnClickListener() {
            int wificlick = 0;
            @Override
            public void onClick(View v) {
                // create an intent to switch to second activity upon clicking
                wificlick += 1;
                if (wificlick % 2 == 1) {
                    sercon.setText("Serial Disconnect");
                    sercon.setBackgroundColor(Color.parseColor("#006d2d"));
//                    status.setText("CONNECTED");
//                    status.setTextColor(Color.parseColor("#0f9d58"));
//                    status.setBackgroundColor(Color.parseColor("#d3d3d3"));
                    for (View var : inflated)
                    {
                        Button stat = var.findViewById(R.id.status);
                        TextView disp = var.findViewById(R.id.textView20ci);
                        stat.setText("CONNECTED");
                        stat.setTextColor(Color.parseColor("#0f9d58"));
                        stat.setBackgroundColor(Color.parseColor("#d3d3d3"));
                        disp.setText("CLEAR");
                        disp.setTextColor(Color.parseColor("#0f9d58"));

                    }

                }
                else{
                    sercon.setText("Serial Connect");
                    sercon.setBackgroundColor(Color.parseColor("#990000"));
//                    status.setText("DISCONNECTED");
//                    status.setTextColor(Color.parseColor("#990000"));
//                    status.setBackgroundColor(Color.parseColor("#d3d3d3"));
                    for (View var : inflated)
                    {
                        Button stat = var.findViewById(R.id.status);
                        TextView disp = var.findViewById(R.id.textView20ci);
                        stat.setText("DISCONNECTED");
                        stat.setTextColor(Color.parseColor("#990000"));
                        stat.setBackgroundColor(Color.parseColor("#d3d3d3"));
                        disp.setText("INACTIVE");
                        disp.setTextColor(Color.parseColor("#636363"));
                    }
                }
            }
        });

        // To listen for a switch's checked/unchecked state changes
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            // Responds to switch being checked/unchecked
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wifiset.setClickable(false);
                    wifiset.setAlpha(0.25F);
                    wificon.setClickable(false);
                    wificon.setBackgroundColor(Color.parseColor("#AEAEAE"));
                    serset.setClickable(true);
                    serset.setAlpha(0.55F);
                    sercon.setClickable(true);
                    sercon.setAlpha(1);
                    sercon.setBackgroundColor(Color.parseColor("#990000"));
                    for(View var: inflated){
                        Button status = var.findViewById(R.id.status);
                        status.setClickable(false);
                    }
//                    status.setClickable(false);
//                    status.setText("DISCONNECTED");
//                    status.setTextColor(Color.parseColor("#990000"));
//                    status.setBackgroundColor(Color.parseColor("#d3d3d3"));
                }
                else{
                    serset.setClickable(false);
                    serset.setAlpha(0.25F);
                    sercon.setClickable(false);
                    sercon.setBackgroundColor(Color.parseColor("#AEAEAE"));
                    wificon.setBackgroundColor(Color.parseColor("#636363"));
                    wifiset.setClickable(true);
                    wifiset.setAlpha(0.55F);
                    sercon.setAlpha(0.55F);
                    wificon.setClickable(true);
                    for(View var: inflated){
                        Button status = var.findViewById(R.id.status);
                        status.setClickable(true);
                    }
//                    status.setClickable(true);
//                    status.setText("DISCONNECTED");
//                    status.setTextColor(Color.parseColor("#990000"));
//                    status.setBackgroundColor(Color.parseColor("#d3d3d3"));
//                    if (ContextCompat.checkSelfPermission(
//                            MainActivity.this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
//                    } else {
//                        // You can directly ask for the permission.
//                        // The registered ActivityResultCallback gets the result of this request.
//                        requestPermissionLauncher.launch(new String[] {
//                                ACCESS_FINE_LOCATION,
//                                ACCESS_COARSE_LOCATION});
//                    }
                }
            }
        });


        switch1.setChecked(true);
        wifiset.setClickable(false);
        wificon.setClickable(false);
        serset.setClickable(true);
        sercon.setClickable(true);
//        status.setClickable(false);
        wificon.setBackgroundColor(Color.parseColor("#AEAEAE"));

        // Find all available drivers from attached devices.
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            // Toast.makeText(MainActivity.this, "Empty", Toast.LENGTH_LONG).show();
            return;
        }

        // Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
            // Toast.makeText(MainActivity.this, "Null", Toast.LENGTH_LONG).show();
            return;
        }

        UsbSerialPort port = driver.getPorts().get(0); // Most devices have just one port (port 0)
        try{
            port.open(connection);
            port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            byte[] buffer = new byte[8192];
            String len = String.valueOf(port.read(buffer, READ_WAIT_MILLIS));
            Toast.makeText(MainActivity.this, len, Toast.LENGTH_LONG).show();
        } catch (IOException e){
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
        }

        // inflate (create) another copy of our custom layout

//        // make changes to our custom layout and its subviews
//        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) myLayout.getLayoutParams();
//        params.setMargins(8,8,dp100,8); // params.setMargins(0,0,0,0); at first
//        myLayout.setLayoutParams(params);
//        myLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
//        TextView textView = myLayout.findViewById(R.id.textView);
//        textView.setText("New Layout");

        // add our custom layout to the main layout

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportFragmentManager().addOnBackStackChangedListener(this);
//        if (savedInstanceState == null)
//            getSupportFragmentManager().beginTransaction().add(R.id.fragment, new DevicesFragment(), "devices").commit();
//        else
//            onBackStackChanged();
//    }
//
//    @Override
//    public void onBackStackChanged() {
//        getSupportActionBar().setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount()>0);
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//        return true;
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        if("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(intent.getAction())) {
//            TerminalFragment terminal = (TerminalFragment)getSupportFragmentManager().findFragmentByTag("terminal");
//            if (terminal != null)
//                terminal.status("USB device detected");
//        }
//        super.onNewIntent(intent);
    }



    @Override
    protected void onResume() {
        super.onResume();
        // Fetching the stored data from the SharedPreference
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        LinearLayout mainLayout1 = findViewById(R.id.inflater1);
        SwitchMaterial switch1 = findViewById(R.id.switch1);
        ImageButton wifiset = findViewById(R.id.wifiset);
        Button wificon = findViewById(R.id.wificon);
        ImageButton serset = findViewById(R.id.serset);
        Button sercon = findViewById(R.id.sercon);
        boolean bool = sh.getBoolean("switch1", false);

        int mViewsCount = sh.getInt("mViewsCount", 0);;

        for(int i = 1; i <= mViewsCount; i++) {
            View myLayout1 = getLayoutInflater().inflate(R.layout.my_layout, mainLayout1, false);
            mainLayout1.addView(myLayout1, 0);
            inflated.add(myLayout1);
            setCard(myLayout1);
            Button stat = myLayout1.findViewById(R.id.status);
            TextView wifiSSID = myLayout1.findViewById(R.id.image_view20ci);
            TextView disp = myLayout1.findViewById(R.id.textView20ci);
            wifiSSID.setText(sh.getString("wifi"+i, ""));

            if (sh.getString("" + i, "").equals("CONNECTED")){
                stat.setText("CONNECTED");
                stat.setTextColor(Color.parseColor("#0f9d58"));
                stat.setBackgroundColor(Color.parseColor("#d3d3d3"));
                disp.setText("CLEAR");
                disp.setTextColor(Color.parseColor("#0f9d58"));
            }
            else{
                stat.setText("DISCONNECTED");
                stat.setTextColor(Color.parseColor("#990000"));
                stat.setBackgroundColor(Color.parseColor("#d3d3d3"));
                disp.setText("INACTIVE");
                disp.setTextColor(Color.parseColor("#636363"));
            }
        }

        if(bool){
            wifiset.setClickable(false);
            wifiset.setAlpha(0.25F);
            wificon.setClickable(false);
            wificon.setBackgroundColor(Color.parseColor("#AEAEAE"));
            serset.setClickable(true);
            serset.setAlpha(0.55F);
            sercon.setClickable(true);
            switch1.setChecked(true);
            for(View var: inflated){
                Button status = var.findViewById(R.id.status);
                status.setClickable(false);
            }
        }
        else {
            serset.setClickable(false);
            serset.setAlpha(0.25F);
            sercon.setClickable(false);
            sercon.setBackgroundColor(Color.parseColor("#AEAEAE"));
            wificon.setBackgroundColor(Color.parseColor("#636363"));
            sercon.setAlpha(0.55F);
            wifiset.setClickable(true);
            wifiset.setAlpha(0.55F);
            wificon.setClickable(true);
            switch1.setChecked(false);
            for(View var: inflated){
                Button status = var.findViewById(R.id.status);
                status.setClickable(true);
            }
        }

        wificon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an intent to switch to second activity upon clicking
//                  View myLayout = getLayoutInflater().inflate(R.layout.my_layout, mainLayout, false);
//                  mainLayout.addView(myLayout, 0); //new ViewGroup.LayoutParams(
//                  ViewGroup.LayoutParams.WRAP_CONTENT, uu3ViewGroup.LayoutParams.WRAP_CONTENT));

                if (ContextCompat.checkSelfPermission(
                        MainActivity.this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    View myLayout1 = getLayoutInflater().inflate(R.layout.my_layout, mainLayout1, false);
                    mainLayout1.addView(myLayout1, 0); //new ViewGroup.LayoutParams(
                    //ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    inflated.add(myLayout1);
                    setCard(myLayout1);
                } else {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(new String[] {
                            ACCESS_FINE_LOCATION,
                            ACCESS_COARSE_LOCATION});
                }


            }
        });

        wifiset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
                startActivity(intent);
            }
        });

        if(sh.getBoolean("sercon", false)){
            sercon.setText("Serial Disconnect");
            sercon.setBackgroundColor(Color.parseColor("#006d2d"));
        }
        else{
            sercon.setText("Serial Connect");
            sercon.setBackgroundColor(Color.parseColor("#990000"));
        }

        sercon.setOnClickListener(new View.OnClickListener() {
            int wificlick = Integer.parseInt(sh.getBoolean("sercon", false) ? "1" : "0");
            @Override
            public void onClick(View v) {
                // create an intent to switch to second activity upon clicking
                wificlick += 1;
                if (wificlick % 2 == 1) {
                    sercon.setText("Serial Disconnect");
                    sercon.setBackgroundColor(Color.parseColor("#006d2d"));
//                    status.setText("CONNECTED");
//                    status.setTextColor(Color.parseColor("#0f9d58"));
//                    status.setBackgroundColor(Color.parseColor("#d3d3d3"));
                    for (View var : inflated)
                    {
                        Button stat = var.findViewById(R.id.status);
                        TextView disp = var.findViewById(R.id.textView20ci);
                        stat.setText("CONNECTED");
                        stat.setTextColor(Color.parseColor("#0f9d58"));
                        stat.setBackgroundColor(Color.parseColor("#d3d3d3"));
                        disp.setText("CLEAR");
                        disp.setTextColor(Color.parseColor("#0f9d58"));
                    }

                }
                else{
                    sercon.setText("Serial Connect");
                    sercon.setBackgroundColor(Color.parseColor("#990000"));
//                    status.setText("DISCONNECTED");
//                    status.setTextColor(Color.parseColor("#990000"));
//                    status.setBackgroundColor(Color.parseColor("#d3d3d3"));
                    for (View var : inflated)
                    {
                        Button stat = var.findViewById(R.id.status);
                        TextView disp = var.findViewById(R.id.textView20ci);
                        stat.setText("DISCONNECTED");
                        stat.setTextColor(Color.parseColor("#990000"));
                        stat.setBackgroundColor(Color.parseColor("#d3d3d3"));
                        disp.setText("INACTIVE");
                        disp.setTextColor(Color.parseColor("#636363"));
                    }
                }
            }
        });

        // To listen for a switch's checked/unchecked state changes
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            // Responds to switch being checked/unchecked
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wifiset.setClickable(false);
                    wifiset.setAlpha(0.25F);
                    wificon.setClickable(false);
                    wificon.setBackgroundColor(Color.parseColor("#AEAEAE"));
                    serset.setClickable(true);
                    serset.setAlpha(0.55F);
                    sercon.setClickable(true);
                    sercon.setAlpha(1);
                    for(View var: inflated){
                        Button status = var.findViewById(R.id.status);
                        status.setClickable(false);
                    }
                }
                else{
                    serset.setClickable(false);
                    serset.setAlpha(0.25F);
                    sercon.setClickable(false);
                    sercon.setAlpha(0.55F);
                    wificon.setBackgroundColor(Color.parseColor("#636363"));
                    wifiset.setClickable(true);
                    wifiset.setAlpha(0.55F);
                    wificon.setClickable(true);
                    for(View var: inflated){
                        Button status = var.findViewById(R.id.status);
                        status.setClickable(true);
                    }
                }
            }
        });
    }

    // Store the data in the SharedPreference in the onPause() method
    // When the user closes the application onPause() will be called and data will be stored
    @Override
    protected void onPause() {
        super.onPause();
        // Creating a shared pref object with a file name "MySharedPref" in private mode
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        // write all the data entered by the user in SharedPreference and apply
        int mViewsCount = 0;
        for(View view : inflated)
        {
            mViewsCount++;
            Button stat = view.findViewById(R.id.status);
            myEdit.putString(""+mViewsCount, stat.getText().toString());
            TextView wifiSSID = view.findViewById(R.id.image_view20ci);
            myEdit.putString("wifi"+mViewsCount, wifiSSID.getText().toString());
        }

        myEdit.putInt("mViewsCount", mViewsCount);

        SwitchMaterial switch1 = findViewById(R.id.switch1);
        if (switch1.isChecked()){
            myEdit.putBoolean("switch1", true);
        }
        else{
            myEdit.putBoolean("switch1", false);
        }

        Button sercon = findViewById(R.id.sercon);
        if(sercon.getText().toString().equals("Serial Disconnect")){
            myEdit.putBoolean("sercon", true);
        }
        else{
            myEdit.putBoolean("sercon", false);
        }

        myEdit.apply();
    }

}