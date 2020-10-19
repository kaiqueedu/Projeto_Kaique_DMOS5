package com.example.projeto_kaique_dmos5.view;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.projeto_kaique_dmos5.R;

import com.example.projeto_kaique_dmos5.api.RetrofitService;
import com.example.projeto_kaique_dmos5.model.weatherapi.Weather;
import com.example.projeto_kaique_dmos5.model.weatherapi.WeatherResponse;

import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEYID = "2f78457799fa9a9f8713bc8c3ae350f7";
    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static final int REQUEST_PERMISSION = 64;

    private EditText cidadeEditText;
    private Button botaoBuscar;

    private TextView txvStatus;
    private TextView txvTemperatura;
    private TextView txvTemperaturaMax;
    private TextView txvTemperaturaMin;
    private TextView txvUmidade;
    private TextView txvVento;

    private Button botaoLimpar;

    Retrofit mRetrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLayout();

    }

    private void setLayout(){
        cidadeEditText = findViewById(R.id.edittext_cidade);
        botaoBuscar = findViewById(R.id.button_buscar);
        botaoBuscar.setOnClickListener(this);

        txvStatus = findViewById(R.id.textView_status);
        txvTemperatura = findViewById(R.id.textview_temperatura);
        txvTemperaturaMax = findViewById(R.id.textview_temp_max);
        txvTemperaturaMin = findViewById(R.id.textview_temp_min);
        txvUmidade = findViewById(R.id.textview_umidade);
        txvVento = findViewById(R.id.textview_vento);

        botaoLimpar = findViewById(R.id.button_limpar);
        botaoLimpar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(botaoBuscar)){
            if(temPermissao()){
                getResquest();
            }else{
                solicitaPermissao();
            }
        }else if(v.equals(botaoLimpar)){
            clear();
        }

    }

    private void getResquest(){
        mRetrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitService service = mRetrofit.create(RetrofitService.class);

        String cidade = cidadeEditText.getText().toString();

        Call<WeatherResponse> requestWeather = service.getWeatherConditions(cidade, KEYID);

        requestWeather.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(MainActivity.this, R.string.erroCidade, Toast.LENGTH_SHORT).show();
                    return;
                }
                WeatherResponse map = response.body();
                updateUI(map);
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.erroCidade, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean temPermissao(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
    }

    private void solicitaPermissao(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
            final Activity activity = this;
            new AlertDialog.Builder(this)
                    .setMessage(R.string.explicacaoPermissao)
                    .setPositiveButton(R.string.botaoFornecer, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.INTERNET}, REQUEST_PERMISSION);
                        }
                    })
                    .setNegativeButton(R.string.botaNegar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.INTERNET
                    },
                    REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equalsIgnoreCase(Manifest.permission.INTERNET) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    getResquest();
                }
            }
        }
    }

    private void updateUI(WeatherResponse map){
        txvStatus.setText( cidadeEditText.getText().toString() + ", " + map.getWeather().get(0).getDescription().toString());
        txvTemperatura.setText(map.getMain().getTemp().toString());
        txvTemperaturaMax.setText(map.getMain().getTempMin().toString());
        txvTemperaturaMin.setText(map.getMain().getTempMax().toString());
        txvUmidade.setText(map.getMain().getHumidity().toString() + "%");
        txvVento.setText(map.getWind().getSpeed().toString() + "m/s");
    }

    private void clear(){
        txvStatus.setText("");
        txvTemperatura.setText("");
        txvTemperaturaMax.setText("");
        txvTemperaturaMin.setText("");
        txvUmidade.setText("");
        txvVento.setText("");
    }

}