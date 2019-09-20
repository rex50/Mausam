package com.rex50.mausam.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.rex50.mausam.ModelClasses.WeatherModelClass;
import com.rex50.mausam.Network.APIManager;
import com.rex50.mausam.Network.LocationDataManager;
import com.rex50.mausam.R;
import com.rex50.mausam.Utils.MaterialSnackBar;

import java.text.DecimalFormat;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements LocationDataManager.LocationResultResponse, APIManager.CallBackResponse{

    TextView initialText;
    MaterialSnackBar materialSnackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        requestLocation();

    }

    private void init() {
        materialSnackBar = new MaterialSnackBar(this);
        initialText = findViewById(R.id.initialText);
//        initialText.setText("Getting location...");
        materialSnackBar.show("Getting location...", MaterialSnackBar.LENGTH_INDEFINITE);
    }

    private void requestWeather(){
        APIManager apiManager = APIManager.getInstance(this);
        HashMap<String, String> urlExtras = new HashMap<>();
        urlExtras.put("lat", String.valueOf(LocationDataManager.CURRENT_LOCATION.getLatitude()));
        urlExtras.put("lon", String.valueOf(LocationDataManager.CURRENT_LOCATION.getLongitude()));
        apiManager.getCurrentWeather(APIManager.SERVICE_CURRENT_WEATHER, urlExtras, this);
    }

    private void requestLocation(){
        LocationDataManager locationDataManager = LocationDataManager.getInstance(this);
        locationDataManager.getLocation();
    }

    @Override
    public void LocationUpdated() {
//        initialText.setText("Location Updated\n");
        materialSnackBar.show("Location Updated", MaterialSnackBar.LENGTH_SHORT);
        requestWeather();
    }

    @Override
    public void onWeatherResponseSuccess(WeatherModelClass weatherDetails) {
        initialText.append(new DecimalFormat("##.##").format(weatherDetails.getMain().getTemp() - 273.15)+" C\n"+ weatherDetails.getName());
        materialSnackBar.showActionSnackBar("Current Temp in kelvin : " + weatherDetails.getMain().getTemp().toString(), "Ok", MaterialSnackBar.LENGTH_INDEFINITE, new MaterialSnackBar.SnackBarListener() {
            @Override
            public void onActionPressed() {
                materialSnackBar.show("Working", MaterialSnackBar.LENGTH_LONG);
            }
        });
    }

    @Override
    public void onWeatherResponseFailure(String msg) {
        initialText.append("Something went wrong");
    }

}
