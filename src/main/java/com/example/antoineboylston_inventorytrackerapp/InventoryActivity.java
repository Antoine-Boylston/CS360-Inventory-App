package com.example.antoineboylston_inventorytrackerapp;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.database.Cursor;
import android.Manifest;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.widget.Switch;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * InventoryActivity
 *
 * Main dashboard screen of the Inventory Tracker application.
 *
 * Responsibilities:
 * - Display inventory list using RecyclerView
 * - Handle SMS alert toggle and phone number input
 * - Manage runtime SMS permission requests
 * - Navigate to Add/Edit Item screen
 */
public class InventoryActivity extends AppCompatActivity {

    // Request code used when asking for SMS permission
    private static final int SMS_PERMISSION_CODE = 100;

    // UI components
    private Switch smsSwitch;
    private EditText editPhoneNumber;
    private RecyclerView recyclerView;

    // Database + adapter
    private DatabaseHelper dbHelper;
    private InventoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enables edge-to-edge layout support
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory);

        // Bind UI elements
        smsSwitch = findViewById(R.id.switchSms);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);

        /*
         * Load saved SMS preference from SharedPreferences.
         * This ensures SMS toggle state persists between sessions.
         */
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this);

        boolean smsEnabled = prefs.getBoolean("sms_enabled", false);
        smsSwitch.setChecked(smsEnabled);

        // Show or hide phone input field based on saved preference
        editPhoneNumber.setVisibility(
                smsEnabled ? View.VISIBLE : View.GONE
        );

        /*
         * SMS Toggle Listener
         * - Shows/hides phone number field
         * - Requests runtime permission if necessary
         * - Saves preference state
         */
        smsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {

                editPhoneNumber.setVisibility(View.VISIBLE);

                // Check if permission already granted
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {

                    prefs.edit().putBoolean("sms_enabled", true).apply();

                } else {
                    // Request SMS permission at runtime
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.SEND_SMS},
                            SMS_PERMISSION_CODE
                    );
                }

            } else {
                // Disable SMS alerts and hide phone input
                editPhoneNumber.setVisibility(View.GONE);
                prefs.edit().putBoolean("sms_enabled", false).apply();
            }
        });

        /*
         * Adjust layout padding to account for system bars
         */
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top,
                    systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Configure RecyclerView layout
        recyclerView = findViewById(R.id.recyclerInventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load inventory data into RecyclerView
        loadInventory();

        /*
         * Add Item button navigates to AddEditItemActivity
         */
        Button addButton = findViewById(R.id.buttonAddItem);
        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(
                    InventoryActivity.this,
                    AddEditItemActivity.class
            );
            startActivity(intent);
        });
    }

    /**
     * Queries database and loads inventory into RecyclerView.
     */
    private void loadInventory() {
        Cursor cursor = dbHelper.getAllItems();
        adapter = new InventoryAdapter(this, cursor);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Refresh inventory list whenever returning to this activity.
     * Ensures UI reflects latest database changes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadInventory();
    }

    /**
     * Checks SMS permission before sending message.
     * If permission is not granted, requests it.
     */
    public void checkSmsPermissionAndSend(String message) {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {

            sendSms(message);

        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_CODE
            );
        }
    }

    /**
     * Handles result of runtime permission request.
     * Updates toggle state based on user decision.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                SharedPreferences prefs =
                        PreferenceManager.getDefaultSharedPreferences(this);
                prefs.edit().putBoolean("sms_enabled", true).apply();

                smsSwitch.setChecked(true);

            } else {
                smsSwitch.setChecked(false);
            }
        }
    }


    /**
     * Sends SMS alert using device's SMS manager.
     * Displays Toast message for success/failure feedback.
     */
    private void sendSms(String message) {

        String phoneNumber =
                editPhoneNumber.getText().toString().trim();

        if (phoneNumber.isEmpty()) {
            Toast.makeText(this,
                    "Please enter a valid phone number",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(
                    phoneNumber,
                    null,
                    message,
                    null,
                    null
            );

            Toast.makeText(
                    this,
                    "SMS Sent: " + message,
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "SMS Failed: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            e.printStackTrace();
        }
    }
}