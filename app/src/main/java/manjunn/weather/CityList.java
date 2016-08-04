package manjunn.weather;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import java.text.DecimalFormat;

public class CityList extends Fragment {

    GridView cityGrid;
    static final String DEGREE = "\u00b0";
    static String[] citiesSel;
    ImageButton addCity;
    static boolean flag = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_city_list, container, false);
        cityGrid = (GridView) v.findViewById(R.id.cityListGrid);
        addCity = (ImageButton) v.findViewById(R.id.addCity);
        addCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchCities.class));
            }
        });
        loadGridValues();
        return v;
    }

    private void loadGridValues() {
        DecimalFormat df = new DecimalFormat("##.##");
        String[] cities = new String[100];
        String[] temp_status = new String[100];
        int count = 0;
        if (MainActivity.selectedCities.moveToFirst() && MainActivity.selectedCities != null) {
            do {
                cities[count] = MainActivity.selectedCities.getString(0);
                temp_status[count] = df.format(MainActivity.selectedCities.getDouble(1)) + DEGREE + " , " + MainActivity.selectedCities.getString(2);
                count++;
            } while (MainActivity.selectedCities.moveToNext());
        }
        String[] finalCities = new String[count];
        String[] finalTempStatus = new String[count];  //removing unused size
        for (int i = 0; i < count; i++) {
            finalCities[i] = cities[i];
            finalTempStatus[i] = temp_status[i];
        }
        citiesSel = finalCities;
        CustomGrid adapter = new CustomGrid(getActivity(), finalCities, finalTempStatus, null, null, R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.layout.city_grid);
        cityGrid.setAdapter(adapter);
        cityGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.city = citiesSel[position];
                view.setBackground(getActivity().getDrawable(R.color.red));
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
    }
}
