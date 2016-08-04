package manjunn.weather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.text.DecimalFormat;

public class WeatherForecastFull extends Fragment {

    GridView wDetailGrid;
    static final String DEGREE = "\u00b0";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_weather_forecast_full, container, false);
        wDetailGrid = (GridView) v.findViewById(R.id.wDetailGrid);
        loadGridValues();
        return v;
    }

    private void loadGridValues() {

        DecimalFormat df = new DecimalFormat("##.##");
        int size = MainActivity.date.size() - 1;
        String[] date = new String[size];
        String[] desc = new String[size];
        String[] max = new String[size];
        String[] min = new String[size];
        for (int i = 0; i < size; i++) {
            date[i] = MainActivity.date.get(i);
            desc[i] = df.format(MainActivity.temp.get(i)).toString() + DEGREE + " , " + MainActivity.status.get(i);
            max[i] = df.format(MainActivity.maxTemp.get(i)).toString() + DEGREE;
            min[i] = df.format(MainActivity.minTemp.get(i)).toString() + DEGREE;
        }
        CustomGrid adapter = new CustomGrid(getActivity(), date, desc, max, min, R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.layout.city_grid);
        wDetailGrid.setAdapter(adapter);
    }
}
