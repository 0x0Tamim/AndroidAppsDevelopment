package ewubd.edu.tamim2023360298;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private Button btnExit, btnAddNew;
    private ListView lvBirthdays;
    private ArrayList<Birthday> birthdays;
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnExit     = findViewById(R.id.btnExit);
        btnAddNew   = findViewById(R.id.btnAddNew);
        lvBirthdays = findViewById(R.id.lvBirthdays);

        birthdays = new ArrayList<>();
        adapter = new CustomListAdapter(this, birthdays);
        lvBirthdays.setAdapter(adapter);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("btnExit...mainActivity");
                finish();
            }
        });

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("btnAddNew...mainActivity");
                Intent i = new Intent(MainActivity.this, BirthdayInfoActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onResume....mainActivity");
        loadList();
    }

    private void loadList() {
        // First: load from local SQLite DB
        birthdays.clear();
        EventDB db = new EventDB(this);
        Cursor cursor = db.selectAll();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name  = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
                long   dob   = cursor.getLong(cursor.getColumnIndexOrThrow("dob"));
                birthdays.add(new Birthday(name, phone, dob));
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Toast.makeText(this, "Database is empty!", Toast.LENGTH_LONG).show();
        }
        adapter.notifyDataSetChanged();

        // Second: also load from remote server (runs in background)
        loadFromRemoteServer();
    }

    private void loadFromRemoteServer() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String url = "https://www.muthosoft.com/univ/cse489/key_value.php";
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("action",   "restore"));
                params.add(new BasicNameValuePair("sid",      "2023360298"));  // <-- তোমার Student ID
                params.add(new BasicNameValuePair("semester", "2026-1"));
                return RemoteAccess.getInstance().makeHttpRequest(url, "POST", params);
            }

            @Override
            protected void onPostExecute(String data) {
                System.out.println("Remote restore response: " + data);
                try {
                    JSONObject json = new JSONObject(data);
                    if (json.has("key-value")) {
                        birthdays.clear();
                        JSONArray ja = json.getJSONArray("key-value");
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject j  = ja.getJSONObject(i);
                            String value  = j.getString("value");
                            // value format: "name,phone,dob"
                            String[] cols = value.split(",");
                            if (cols.length >= 3) {
                                String name  = cols[0];
                                String phone = cols[1];
                                long   dob   = Long.parseLong(cols[2]);
                                birthdays.add(new Birthday(name, phone, dob));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public void onStart()   { super.onStart();   System.out.println("onStart...mainActivity"); }
    public void onPause()   { super.onPause();   System.out.println("onPause...mainActivity"); }
    public void onStop()    { super.onStop();    System.out.println("onStop...mainActivity"); }
    public void onDestroy() { super.onDestroy(); System.out.println("onDestroy...mainActivity"); }
}