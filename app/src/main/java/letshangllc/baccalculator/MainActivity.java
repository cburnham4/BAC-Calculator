package letshangllc.baccalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();
    private String gender = "";

    final private static int BEER_OZ = 12;
    final private static double WINE_OZ = 5;
    final private static double SHOT_ALC_OZ = 1.5;

    final private static double BEER_ALC_CONTENT = .05;
    final private static double WINE_ALC_CONTENT = .13;
    final private static double SHOT_ALC_CONTENT = .40;


    private EditText et_weight, et_hours, et_shots, et_beers, et_wines;

    private int numBeers, numShots, numWines;
    private double hours, weight, bac;
    TextView tv_bac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setupViews();
        this.setUpAds();

    }

    public void setupViews(){
        Spinner spinner = (Spinner) findViewById(R.id.spinner_gender);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genders, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        et_weight = (EditText) findViewById(R.id.input_weight);
        et_hours = (EditText) findViewById(R.id.input_hours);
        et_beers = (EditText) findViewById(R.id.input_beer);
        et_shots = (EditText) findViewById(R.id.input_shots);
        et_wines = (EditText) findViewById(R.id.input_wine);

        tv_bac = (TextView) findViewById(R.id.tv_bac);

        Button btnCalculate = (Button) findViewById(R.id.btnCalculateBAC);

        assert btnCalculate != null;
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    weight = Double.parseDouble(et_weight.getText().toString());
                    hours = Double.parseDouble(et_hours.getText().toString());
                    try {
                        numBeers = Integer.parseInt(et_beers.getText().toString());
                    }catch (Exception e){
                        numBeers= 0;
                    }
                    try{
                        numShots = Integer.parseInt(et_shots.getText().toString());
                    }catch (Exception e){
                        numShots = 0;
                    }
                    try {
                        numWines = Integer.parseInt(et_wines.getText().toString());
                    } catch (Exception e){
                        numWines = 0;
                    }
                    bac = updateBAC();
                    tv_bac.setText(String.format(Locale.US, "BAC: %.3f", bac));
                    tv_bac.setVisibility(View.VISIBLE);
                }catch (Exception e ){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "One or more options is blank", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }


    private double updateBAC(){
        double BAC;
        double numberOfDrinks = ((numBeers *  BEER_OZ*BEER_ALC_CONTENT) +
                (numShots*SHOT_ALC_CONTENT*SHOT_ALC_OZ) +
                (numWines*WINE_OZ*WINE_ALC_CONTENT));

        Log.i(TAG, "Num: " +numberOfDrinks);
        //Women Multiplier
        double R = .66;
        if (gender.equalsIgnoreCase("Male")){
            //Male multiplier
            R = .73;
        }

        double weightInKg = weight/2.2;
        BAC =(numberOfDrinks * 5.14)/(weight * R) - (.015 * hours);

        Toast.makeText(getApplicationContext(), String.format(Locale.US, "BAC: %.3f", BAC), Toast.LENGTH_LONG).show();
        Log.v("DRINKING", "" + BAC);
        //bacGauge.setTargetValue((float)BAC * 100.0f);

        //Return 0 for negative BAC
        if(BAC < 0){
            return 0;
        }

        return BAC;
    }

    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
            gender = (String) parent.getItemAtPosition(pos);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }


    }

    /* private instance of adsHelper */
    private AdsHelper adsHelper;
    /* Setup and run ads */
    private void setUpAds(){
        adsHelper = new AdsHelper(getWindow().getDecorView(), getResources().getString(R.string.admob_id),this);
        adsHelper.setUpAds();
        int delay = 1000; // delay for 1 sec.
        int period = getResources().getInteger(R.integer.ad_refresh_rate);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                adsHelper.refreshAd();  // display the data
            }
        }, delay, period);
    }

}
