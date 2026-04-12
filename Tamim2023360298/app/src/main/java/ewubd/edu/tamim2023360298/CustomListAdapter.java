package ewubd.edu.tamim2023360298;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;

public class CustomListAdapter extends ArrayAdapter<Birthday> {

    private final Context context;
    private final ArrayList<Birthday> values;

    public CustomListAdapter(@NonNull Context context, @NonNull ArrayList<Birthday> items) {
        super(context, -1, items);
        this.context = context;
        this.values = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.birthday_row, parent, false);

        // birthday_row.xml structure:
        // Child 0 → TextView (name)
        // Child 1 → horizontal LinearLayout
        //     Child 0 → TextView (date)
        //     Child 1 → TextView (age)
        ViewGroup rootView = (ViewGroup) rowView;
        TextView tvName = (TextView) rootView.getChildAt(0);
        ViewGroup bottomRow = (ViewGroup) rootView.getChildAt(1);
        TextView tvDate = (TextView) bottomRow.getChildAt(0);
        TextView tvAge  = (TextView) bottomRow.getChildAt(1);

        Birthday b = values.get(position);

        // Set name
        tvName.setText(b.name);

        // Format date of birth: "01 January, 2003"
        Calendar dobCal = Calendar.getInstance();
        dobCal.setTimeInMillis(b.dob);
        int dobDay   = dobCal.get(Calendar.DAY_OF_MONTH);
        int dobMonth = dobCal.get(Calendar.MONTH); // 0-indexed
        int dobYear  = dobCal.get(Calendar.YEAR);

        String[] months = {
                "January","February","March","April","May","June",
                "July","August","September","October","November","December"
        };
        tvDate.setText(String.format("%02d %s, %d", dobDay, months[dobMonth], dobYear));

        // Calculate age: years, months, days
        Calendar today = Calendar.getInstance();
        int years   = today.get(Calendar.YEAR)         - dobYear;
        int months2 = today.get(Calendar.MONTH)        - dobMonth;
        int days    = today.get(Calendar.DAY_OF_MONTH) - dobDay;

        if (days < 0)    { months2--; days    += 30; }
        if (months2 < 0) { years--;   months2 += 12; }

        tvAge.setText(years + " years " + months2 + "m " + days + "d");

        return rowView;
    }
}