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

import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_profile);

        // sob input field ar spinner
        EditText etName = findViewById(R.id.etName);
        Spinner spBlood = findViewById(R.id.spBloodGroup);
        EditText etPhone = findViewById(R.id.etPhone);
        EditText etWhatsapp = findViewById(R.id.etWhatsapp);
        Spinner spDistrict = findViewById(R.id.spDistrict);
        EditText etArea = findViewById(R.id.etArea);
        EditText etDate = findViewById(R.id.etLastDonation);
        Button btnSave = findViewById(R.id.btnSaveProfile);

        // database helper object, data save korar jonno
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // blood group spinner e list set korchi strings.xml theke
        ArrayAdapter<CharSequence> bloodAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.blood_groups,
                android.R.layout.simple_spinner_item
        );
        bloodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBlood.setAdapter(bloodAdapter);

        // district spinner e list set korchi strings.xml theke
        ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.districts,
                android.R.layout.simple_spinner_item
        );
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDistrict.setAdapter(districtAdapter);

        // last donation date field e click korle calendar dekhabe
        etDate.setOnClickListener(v -> {

            // default hishebe ajker date
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            // date picker dialog open korchi
            DatePickerDialog datePicker = new DatePickerDialog(
                    ProfileActivity.this,
                    (view, y, m, d) -> {
                        // selected date format kore field e
                        String selectedDate = String.format("%02d/%02d/%d", d, m + 1, y);
                        etDate.setText(selectedDate);
                    },
                    year, month, day
            );
            datePicker.show();
        });

        btnSave.setOnClickListener(v -> {

            // sob field theke data nichi
            String name = etName.getText().toString().trim();
            String blood = spBlood.getSelectedItem().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String whatsapp = etWhatsapp.getText().toString().trim();
            String district = spDistrict.getSelectedItem().toString().trim();
            String area = etArea.getText().toString().trim();
            String date = etDate.getText().toString().trim();

            // required field khali thakle error dekhabo
            if (name.isEmpty() || phone.isEmpty() || area.isEmpty() || date.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // blood group ar district theke default option select thakle error
            if (blood.equals("Select Blood Group") || district.equals("Select District")) {
                Toast.makeText(ProfileActivity.this, "Please select valid options", Toast.LENGTH_SHORT).show();
                return;
            }

            // phone number 11 digit er kom hoile accept korbo na
            if (phone.length() < 11) {
                Toast.makeText(ProfileActivity.this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            // database e data insert korchi, true/false return korbe
            boolean saved = dbHelper.insertDonor(name, blood, phone, whatsapp, district, area, date);

            if (saved) {
                Toast.makeText(ProfileActivity.this, "Profile Saved Successfully", Toast.LENGTH_SHORT).show();

                // save er pore sob field clear kore dibo
                etName.setText("");
                etPhone.setText("");
                etWhatsapp.setText("");
                etArea.setText("");
                etDate.setText("");
                spBlood.setSelection(0);    // spinner first item e reset
                spDistrict.setSelection(0); // district spinner o reset
            } else {
                // kono karone save na hole error msg
                Toast.makeText(ProfileActivity.this, "Error Saving Data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
