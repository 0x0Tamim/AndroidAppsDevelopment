package ewubd.edu.tamim2023360298;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BirthdayInfoActivity extends AppCompatActivity {

    private Button   btnCancel, btnSave;
    private EditText etName, etPhone, etDob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday_info);

        btnCancel = findViewById(R.id.btnCancel);
        btnSave   = findViewById(R.id.btnSave);
        etName    = findViewById(R.id.etName);
        etPhone   = findViewById(R.id.etPhone);
        etDob     = findViewById(R.id.etDob);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("btnCancel...BirthdayInfoActivity");
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("btnSave...BirthdayInfoActivity");

                String name   = etName.getText().toString().trim();
                String phone  = etPhone.getText().toString().trim();
                String dobStr = etDob.getText().toString().trim(); // format: dd/MM/yyyy

                if (name.isEmpty() || phone.isEmpty() || dobStr.isEmpty()) {
                    Toast.makeText(BirthdayInfoActivity.this,
                            "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Parse "dd/MM/yyyy" → long millis
                long dobMillis = 0;
                try {
                    String[] parts = dobStr.split("/");
                    int day   = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]) - 1; // 0-indexed
                    int year  = Integer.parseInt(parts[2]);

                    Calendar cal = Calendar.getInstance();
                    cal.set(year, month, day, 0, 0, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    dobMillis = cal.getTimeInMillis();
                } catch (Exception e) {
                    Toast.makeText(BirthdayInfoActivity.this,
                            "Date format must be dd/MM/yyyy", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save to local SQLite DB
                EventDB db = new EventDB(BirthdayInfoActivity.this);
                db.insertBirthday(name, phone, dobMillis);

                // Save backup to remote server (in background)
                saveToRemoteServer(name, phone, dobMillis);

                Toast.makeText(BirthdayInfoActivity.this,
                        "Saved!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void saveToRemoteServer(String name, String phone, long dobMillis) {
        // key   = unique identifier (timestamp + phone)
        // value = "name,phone,dobMillis"  (comma-separated — same format used when restoring)
        String dobKey   = System.currentTimeMillis() + phone;
        String dobValue = name + "," + phone + "," + dobMillis;

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String url = "https://www.muthosoft.com/univ/cse489/key_value.php";
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("action",   "backup"));
                params.add(new BasicNameValuePair("sid",      "2023360298"));  // <-- তোমার Student ID
                params.add(new BasicNameValuePair("semester", "2026-1"));
                params.add(new BasicNameValuePair("key",      dobKey));
                params.add(new BasicNameValuePair("value",    dobValue));
                return RemoteAccess.getInstance().makeHttpRequest(url, "POST", params);
            }

            @Override
            protected void onPostExecute(String data) {
                System.out.println("Remote backup response: " + data);
            }
        }.execute();
    }
}