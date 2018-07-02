package manjunn.weather;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Home extends Fragment {

    static TextView curCityText, weatherStatus, curTempr, curStatus, maxMin, curHumidity, day, time;
    static RelativeLayout relativeLayout;
    static final String DEGREE = "\u00b0";
    private Button searchButton;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_home, container, false);
        curCityText = (TextView) v.findViewById(R.id.curCityText);
        weatherStatus = (TextView) v.findViewById(R.id.weatherStatus);
        curStatus = (TextView) v.findViewById(R.id.curStatus);
        curTempr = (TextView) v.findViewById(R.id.curTemp);
        maxMin = (TextView) v.findViewById(R.id.maxMin);
        curHumidity = (TextView) v.findViewById(R.id.currHumidity);
        relativeLayout = (RelativeLayout) v.findViewById(R.id.main);
        day = (TextView) v.findViewById(R.id.day);
        time = (TextView) v.findViewById(R.id.time);
        searchButton = (Button) v.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchCities.class));
            }
        });
        displayTempDetails();
        setHasOptionsMenu(true);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        startActivity(new Intent(getActivity(), HomeSliding.class));
                    }
                }
        );
        return v;
    }

    public void displayTempDetails() {
        String curCity = MainActivity.city + " , " + MainActivity.curCountry + " \n " + "Geo coords[ " + MainActivity.lat + "," + MainActivity.lon + " ]" + "\n Weather Forecast:";
        curCityText.setText(curCity);
        DecimalFormat df = new DecimalFormat("##.##");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        day.setText(String.valueOf(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime())));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String curTime = sdf.format(calendar.getTime());
        time.setText(curTime.substring(0, 5));
        if (MainActivity.status.get(0).contains("rain") && MainActivity.temp.get(0) > 28)
            relativeLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.sunrain, null));
        else if (MainActivity.temp.get(0) > 28 || MainActivity.status.get(0).contains("haze"))
            relativeLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.sun, null));
        else if (MainActivity.status.get(0).contains("clear sky"))
            relativeLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.moon, null));
        else if (MainActivity.status.get(0).contains("cloud"))
            relativeLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.cloudy, null));
        else if (MainActivity.status.get(0).contains("thunder"))
            relativeLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.thunder, null));
        else if (MainActivity.status.get(0).contains("rain"))
            relativeLayout.setBackground(getActivity().getResources().getDrawable(R.drawable.rain, null));
        curTempr.setText(df.format(MainActivity.temp.get(0)).toString() + DEGREE);
        curStatus.setText(MainActivity.status.get(0));
        maxMin.setText("Max " + df.format(MainActivity.maxTemp.get(0)).toString() + DEGREE + " Min " + df.format(MainActivity.minTemp.get(0)).toString() + DEGREE);
        curHumidity.setText("Humidity: " + String.valueOf(MainActivity.humidity.get(0)));
    }
}

