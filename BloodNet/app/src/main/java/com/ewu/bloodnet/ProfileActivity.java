package com.ewu.bloodnet;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // UI references
        EditText etName = findViewById(R.id.etName);
        Spinner spBlood = findViewById(R.id.spBloodGroup);
        EditText etPhone = findViewById(R.id.etPhone);
        EditText etWhatsapp = findViewById(R.id.etWhatsapp);
        Spinner spDistrict = findViewById(R.id.spDistrict);
        EditText etArea = findViewById(R.id.etArea);
        EditText etDate = findViewById(R.id.etLastDonation);
        Button btnSave = findViewById(R.id.btnSaveProfile);

        // Database
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Blood spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.blood_groups,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBlood.setAdapter(adapter);

        // District spinner
        ArrayAdapter<CharSequence> locAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.districts,
                android.R.layout.simple_spinner_item
        );
        locAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDistrict.setAdapter(locAdapter);

        // Date picker
        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    ProfileActivity.this,
                    (view, y, m, d) -> {
                        String date = String.format("%02d/%02d/%d", d, m + 1, y);
                        etDate.setText(date);
                    },
                    year, month, day
            );
            dialog.show();
        });

        // Save button
        btnSave.setOnClickListener(v -> {

            String name = etName.getText().toString().trim();
            String blood = spBlood.getSelectedItem().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String whatsapp = etWhatsapp.getText().toString().trim();
            String district = spDistrict.getSelectedItem().toString().trim();
            String area = etArea.getText().toString().trim();
            String date = etDate.getText().toString().trim();

            // ❌ 1. Empty check
            if (name.isEmpty() || phone.isEmpty() || area.isEmpty() || date.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // ❌ 2. Spinner validation (👉 এখানে বসবে)
            if (blood.equals("Select Blood Group") || district.equals("Select District")) {
                Toast.makeText(ProfileActivity.this, "Please select valid options", Toast.LENGTH_SHORT).show();
                return;
            }

            // ❌ 3. Phone validation (👉 এখানে বসবে)
            if (phone.length() < 11) {
                Toast.makeText(ProfileActivity.this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ 4. Insert data
            boolean isInserted = dbHelper.insertDonor(
                    name, blood, phone, whatsapp, district, area, date
            );

            if (isInserted) {
                Toast.makeText(ProfileActivity.this, "Profile Saved Successfully", Toast.LENGTH_SHORT).show();

                // 🔄 5. Clear fields (👉 এখানে বসবে - success এর ভিতরে)
                etName.setText("");
                etPhone.setText("");
                etWhatsapp.setText("");
                etArea.setText("");
                etDate.setText("");
                spBlood.setSelection(0);
                spDistrict.setSelection(0);

            } else {
                Toast.makeText(ProfileActivity.this, "Error Saving Data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}