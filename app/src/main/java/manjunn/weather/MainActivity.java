package manjunn.weather;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static LocationManager locationManager;
    Location location;
    static String city = "", curCity = "", curCountry = "";
    static JSONObject data;
    private Context context = this;
    static Double lat = 0.0, lon = 0.0;
    static List<Double> temp = new ArrayList<>();
    static List<Double> maxTemp = new ArrayList<>();
    static List<Double> minTemp = new ArrayList<>();
    static List<String> status = new ArrayList<>();
    static List<String> date = new ArrayList<>();
    static List<Integer> humidity = new ArrayList<>();
    private SqliteDatabaseOperations sqliteDatabaseOperations;
    static Cursor selectedCities;
    boolean FNFCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {       //check for gps connection
            showGPSAlert();
        } else {
            if (city.equalsIgnoreCase(""))
                city = getCurrentLocation(); // to get the current location
            new JSONParser().execute();    // get weather data based on city
        }
    }

    private String getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {  // verifying the internet connection permission
            showAlert(getResources().getString(R.string.check_internet_permission));
        }
        if (!isNetworkAvailable())  // verifying the network connection
            showAlert(getResources().getString(R.string.network_unavailable));
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  //getting location through last known location
        if (location != null) {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                curCity = addresses.get(0).getLocality();
                Toast.makeText(context, getResources().getString(R.string.current_location_is) + " " + curCity, Toast.LENGTH_SHORT).show();
                return curCity;
            } catch (IOException e) {
            }
        }
        //else showAlert(getResources().getString(R.string.location_not_found));
        return curCity;
    }

    public void calculateValues() {
        try {
            date.clear();
            temp.clear();
            maxTemp.clear();
            minTemp.clear();
            humidity.clear();
            status.clear();
            JSONObject cityObject = data.getJSONObject("city");
            curCountry = cityObject.getString("country");
            JSONObject coordObject = cityObject.getJSONObject("coord");
            lat = coordObject.getDouble("lat");
            lon = coordObject.getDouble("lon");
            JSONArray mainList = data.getJSONArray("list");
            for (int i = 0; i < mainList.length(); i++) {
                JSONObject object = mainList.getJSONObject(i);
                date.add(convertToDate(object.getInt("dt")));
                JSONObject tempObject = mainList.getJSONObject(i).getJSONObject("temp");
                temp.add(tempObject.getDouble("day") - 273.15);
                maxTemp.add(tempObject.getDouble("max") - 273.15);
                minTemp.add(tempObject.getDouble("min") - 273.15);
                humidity.add(object.getInt("humidity"));
                JSONArray weatherArray = mainList.getJSONObject(i).getJSONArray("weather");
                for (int j = 0; j < weatherArray.length(); j++) {
                    JSONObject jsonObject = (JSONObject) weatherArray.get(j);
                    status.add(jsonObject.getString("description"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveCity() {
        if (FNFCheck) {
            FNFCheck = false;
            showFNFAlert(getResources().getString(R.string.city_not_found));
        } else {
            sqliteDatabaseOperations = new SqliteDatabaseOperations(context);
            if (String.valueOf(MainActivity.temp.get(0)).equals("") || String.valueOf(MainActivity.temp.get(0)) == null)
                showAlert(getResources().getString(R.string.no_internet_connection));
            else {
                sqliteDatabaseOperations.insertCitiesDetails(sqliteDatabaseOperations, MainActivity.city, String.valueOf(MainActivity.temp.get(0)), String.valueOf(MainActivity.status.get(0)));
                selectedCities = sqliteDatabaseOperations.getSavedCitiesDetails(sqliteDatabaseOperations);
                startActivity(new Intent(getApplicationContext(), HomeSliding.class));
            }
        }
    }

    public class JSONParser extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q="+city+"&APPID=ea574594b9d36ab688642d5fbeab847e");
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=" + city.replaceAll("\\s+", "") + "&cnt=15&APPID=" + getResources().getString(R.string.weather_app_id));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                String tmp = "";
                StringBuffer json = new StringBuffer(1024);
                connection.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((tmp = reader.readLine()) != null)
                    json.append(tmp).append("\n");
                reader.close();
                connection.disconnect();
                MainActivity.data = new JSONObject(json.toString());
                if (MainActivity.data.getInt("cod") != 200) {
                    System.out.println("Cancelled");
                }
            } catch (Exception e) {
                FNFCheck = true; // if weather api couldn't find the city's weather
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void Void) {
            if (MainActivity.data != null) {
                calculateValues();  // extract JSON
                saveCity();  //save city details for future usage
            }

        }
    }

    private String convertToDate(Integer dateIn) {

        long timestamp = Long.parseLong(String.valueOf(dateIn)) * 1000;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d", Locale.CANADA);
        String date = sdf.format(timestamp);
        return date.toString();
    }

    private boolean isNetworkAvailable() {  // verifying the network availability
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showFNFAlert(String msg) {

        new AlertDialog.Builder(context)
                .setTitle(getResources().getString(R.string.tittle_city_not_found))
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(context, SearchCities.class));
                    }
                })

                .setIcon(R.drawable.alert)
                .show();
    }


    private void showGPSAlert() {

        new AlertDialog.Builder(context)
                .setTitle(getResources().getString(R.string.gps_alert))
                .setMessage(getResources().getString(R.string.enable_gps))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                })
                .setIcon(R.drawable.alert)
                .show();
    }

    public void showAlert(String msg) {
        new AlertDialog.Builder(context)
                .setTitle(getResources().getString(R.string.alert))
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(context, MainActivity.class));
                    }
                })
                .setIcon(R.drawable.alert)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
