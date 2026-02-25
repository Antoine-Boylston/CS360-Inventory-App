package com.example.antoineboylston_inventorytrackerapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddEditItemActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText editItemName;
    private EditText editItemQuantity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_item);
        dbHelper = new DatabaseHelper(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button saveButton = findViewById(R.id.buttonSave);
        Button cancelButton = findViewById(R.id.buttonCancel);
        editItemName = findViewById(R.id.editItemName);
        editItemQuantity = findViewById(R.id.editItemQuantity);


// Save returns to Inventory screen (functionality added in Project Three)
        saveButton.setOnClickListener(view -> {

            String itemName = editItemName.getText().toString().trim();
            String quantityText = editItemQuantity.getText().toString().trim();

            if (itemName.isEmpty() || quantityText.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityText);

            if (dbHelper.itemExists(itemName)) {
                dbHelper.incrementQuantity(itemName, quantity);
                Toast.makeText(this, "Quantity updated", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.insertItem(itemName, quantity);
                Toast.makeText(this, "New item added", Toast.LENGTH_SHORT).show();
            }


            finish();
        });

// Cancel also returns to Inventory screen
        cancelButton.setOnClickListener(view -> {
            finish(); // closes this screen and goes back
        });

    }
}