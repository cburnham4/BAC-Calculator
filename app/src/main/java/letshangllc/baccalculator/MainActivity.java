package letshangllc.baccalculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String PREFS_NAME = "bac_calculator_prefs";
    private static final String PREF_DISCLAIMER_ACCEPTED = "disclaimer_accepted_v1";

    private String gender = "";

    private static final int BEER_OZ = 12;
    private static final double WINE_OZ = 5;
    private static final double SHOT_ALC_OZ = 1.5;

    private static final double BEER_ALC_CONTENT = .05;
    private static final double WINE_ALC_CONTENT = .13;
    private static final double SHOT_ALC_CONTENT = .40;

    private EditText et_weight, et_hours, et_shots, et_beers, et_wines;

    private int numBeers, numShots, numWines;
    private double hours, weight, bac;
    private TextView tv_bac;

    private AdsHelper adsHelper;
    private Timer adRefreshTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        applyEdgeToEdgeInsets();
        setupViews();
        maybeShowDisclaimer();
        setUpAds();
    }

    private void applyEdgeToEdgeInsets() {
        final View root = findViewById(R.id.root_layout);
        final View adLayout = findViewById(R.id.ad_layout);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(insets.left, insets.top, insets.right, 0);
            if (adLayout != null) {
                adLayout.setPadding(0, 0, 0, insets.bottom);
            }
            return windowInsets;
        });
    }

    private void maybeShowDisclaimer() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(PREF_DISCLAIMER_ACCEPTED, false)) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.disclaimer_title)
                .setMessage(R.string.disclaimer_body)
                .setCancelable(false)
                .setPositiveButton(R.string.disclaimer_accept, (dialog, which) ->
                        prefs.edit().putBoolean(PREF_DISCLAIMER_ACCEPTED, true).apply())
                .show();
    }

    public void setupViews() {
        Spinner spinner = findViewById(R.id.spinner_gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        et_weight = findViewById(R.id.input_weight);
        et_hours = findViewById(R.id.input_hours);
        et_beers = findViewById(R.id.input_beer);
        et_shots = findViewById(R.id.input_shots);
        et_wines = findViewById(R.id.input_wine);

        tv_bac = findViewById(R.id.tv_bac);

        Button btnCalculate = findViewById(R.id.btnCalculateBAC);
        btnCalculate.setOnClickListener(v -> {
            try {
                weight = Double.parseDouble(et_weight.getText().toString());
                hours = Double.parseDouble(et_hours.getText().toString());
                try {
                    numBeers = Integer.parseInt(et_beers.getText().toString());
                } catch (Exception e) {
                    numBeers = 0;
                }
                try {
                    numShots = Integer.parseInt(et_shots.getText().toString());
                } catch (Exception e) {
                    numShots = 0;
                }
                try {
                    numWines = Integer.parseInt(et_wines.getText().toString());
                } catch (Exception e) {
                    numWines = 0;
                }
                bac = updateBAC();
                tv_bac.setText(String.format(Locale.US, getString(R.string.bac_result), bac));
                tv_bac.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.e(TAG, "Calculate failed", e);
                Toast.makeText(MainActivity.this, R.string.error_blank_fields, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double updateBAC() {
        double numberOfDrinks = ((numBeers * BEER_OZ * BEER_ALC_CONTENT)
                + (numShots * SHOT_ALC_CONTENT * SHOT_ALC_OZ)
                + (numWines * WINE_OZ * WINE_ALC_CONTENT));

        Log.i(TAG, "Num: " + numberOfDrinks);
        // Widmark r-factor (gender distribution ratio)
        double genderFactor = 0.66;
        if (gender.equalsIgnoreCase("Male")) {
            genderFactor = 0.73;
        }

        double estimatedBac = (numberOfDrinks * 5.14) / (weight * genderFactor) - (0.015 * hours);

        Toast.makeText(getApplicationContext(),
                String.format(Locale.US, getString(R.string.bac_result), estimatedBac),
                Toast.LENGTH_LONG).show();
        Log.v("DRINKING", "" + estimatedBac);

        if (estimatedBac < 0) {
            return 0;
        }
        return estimatedBac;
    }

    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            gender = (String) parent.getItemAtPosition(pos);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private void setUpAds() {
        adsHelper = new AdsHelper(getWindow().getDecorView(),
                getResources().getString(R.string.admob_banner_unit_id), this);
        adsHelper.setUpAds();
        int delay = 1000;
        int period = getResources().getInteger(R.integer.ad_refresh_rate);
        adRefreshTimer = new Timer();
        adRefreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                adsHelper.refreshAd();
            }
        }, delay, period);
    }

    @Override
    protected void onPause() {
        if (adsHelper != null) {
            adsHelper.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adsHelper != null) {
            adsHelper.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        if (adRefreshTimer != null) {
            adRefreshTimer.cancel();
            adRefreshTimer = null;
        }
        if (adsHelper != null) {
            adsHelper.onDestroy();
        }
        super.onDestroy();
    }
}
