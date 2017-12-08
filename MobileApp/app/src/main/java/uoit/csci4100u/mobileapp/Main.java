package uoit.csci4100u.mobileapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import net.rithms.riot.api.endpoints.summoner.dto.Summoner;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import uoit.csci4100u.mobileapp.util.DatabaseHelperUtil;
import uoit.csci4100u.mobileapp.util.LocationUtil;
import uoit.csci4100u.mobileapp.util.NetworkTask;
import uoit.csci4100u.mobileapp.util.OnGetDataListener;
import uoit.csci4100u.mobileapp.util.PermissionChecker;


/**
 * Main method for App, handles setting up and receiving Activities/Results
 * <p>
 * TODO: currently uses temp development key for Riot API - check to see if key is still valid
 * before changing anything
 *
 * @see PermissionChecker#getPermissions()          - to check is permissions are granted, if not asks for
 * them
 * @see LocationUtil                                - updates location information
 * @see uoit.csci4100u.mobileapp.util.NetworkTask   - generic abstract class to handle most of the
 * Async network tasks
 * @see DatabaseHelperUtil                          - handles Firebase read and write
 */
public class Main extends AppCompatActivity {
    static String mUUID = "";
    static final String TAG = "Main.java";
    static final private String BASE_DRAGON_URL = "http://ddragon.leagueoflegends.com/cdn/";
    static final int SUCCESS = 1;
    static final int FAILURE = 0;
    static final int CANCEL = -1;
    public static boolean play_staus;
    static String current_version;
    Bundle extras;
    TextView summoner_info;
    TextView welcome_lbl;
    ToggleButton avail_button;
    ImageView icon;

    //the users summoner info
    protected static Summoner uSummoner;
    static String locale;

    //New Location util
    private LocationUtil locUtil;


    //database helper
    private static DatabaseHelperUtil dbHelper;
    private GoogleApiClient locApi;

    private static Menu menu;

    private static boolean refreshAvail = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        locUtil = new LocationUtil(this);
        setupLocAPI();
        setUpLayouts();
    }

    @Override
    protected void onStart() {
        extras = getIntent().getExtras();
        mUUID = extras.getString("UUID");
        locale = extras.getString("locale");
        current_version = extras.getString("version");
        Log.d("version recieved", current_version + "");
        locApi.connect();
        locUtil.setLocAPI(locApi);

        updateUI();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    protected void onResume() {
        //update without having to relaunch app
        super.onResume();
    }

    @Override
    protected void onStop() {
        locApi.disconnect();
        super.onStop();
    }

    private void setupLocAPI() {
        locApi = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(locUtil)
                .addOnConnectionFailedListener(locUtil)
                .build();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
//        this.refreshItem = menu.getItem(0);
//        startTimer(R.id.refresh_ui);
//        refreshItem.setEnabled(false);
        return super.onPrepareOptionsMenu(menu);
    }

    public static void setDBHelper(DatabaseHelperUtil helper) {
        dbHelper = helper;
    }


    /**
     * Instantiates and assigns global variables related to the layout
     * <p>
     * TODO: make this set a Boolean on Firebase where true is looking for games and false is not
     */
    public void setUpLayouts() {
        summoner_info = (TextView) findViewById(R.id.summoner_info);
        welcome_lbl = (TextView) findViewById(R.id.welcome_lbl);
        avail_button = (ToggleButton) findViewById(R.id.avail_button);
        icon = (ImageView) findViewById(R.id.imageView);

        avail_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Log.d("Toggle", "ON");

                } else {
                    // The toggle is disabled
                    Log.d("Toggle", "OFF");
                }
//                play_staus = toggleStatus(play_staus);
            }
        });
        avail_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play_staus = toggleStatus(play_staus);
            }
        });
    }

    /**
     * Method that prints summoner info to device screen
     *
     * @param x the Summoner object to be printed
     */
    public void printSummonerToScreen(Summoner x) {
        try {
            String retString = "Name: " + x.getName() + "\nAccount ID: " + x.getAccountId() +
                    "\nID: " + x.getId() + "\nLevel: " + x.getSummonerLevel() + "\nLast Modified:" +
                    x.getRevisionDate();


            this.summoner_info.setText(retString);
        } catch (NullPointerException e) {
            this.summoner_info.setText(R.string.cant_find_name);
        }
    }


    private static String timeConversion(long millis) {

        String hms = String.format(Locale.getDefault(), "%02d:%02d",

                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return hms;
    }

    public class DataDragonTask extends NetworkTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... input) {
            Bitmap bm = null;
            try {

                String tempUrl = BASE_DRAGON_URL + current_version + "/img/profileicon/" + input[0] + ".png";
                Log.d("DataDragon:lookup", tempUrl + "");
                URL url = new URL(tempUrl);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream is = urlConnection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (java.net.MalformedURLException me) {
                Log.d("URL ERROR", me + "");
            } catch (java.io.IOException ie) {
                Log.d("IO ERROR", ie + "");
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            Log.d("DataDragon:end", "finished data dragon access");
            icon.setImageBitmap(result);
        }

        @Override
        protected void onPreExecute() {
            Log.d("DataDragon:start", "starting data dragon access");
        }
    }


    /**
     * gets the searching for game Boolean from the Firebase
     *
     * @return Boolean True if the user is looking for a game and False if they are not
     */
    private void getPlayStatus() {
        dbHelper.getCurrentStatus(new OnGetDataListener() {

            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                play_staus = dataSnapshot.getValue(Boolean.class);
                avail_button.setChecked(play_staus);
                Log.d("getPlayStatus", "received " + play_staus);
            }

            @Override
            public void onStart() {
                Log.d("getPlayStatus", "getting play status");
            }

            @Override
            public void onFailure() {

            }
        });

    }

    private Boolean toggleStatus(Boolean status) {
        return dbHelper.togglePlay(status);
    }


    /**
     * Getter for methods that need the users summoner info
     *
     * @return Summoner object
     */
    public static Summoner getSummoner() {
        return uSummoner;
    }

    /**
     * Setter for uSummoner
     *
     * @param you Summoner object belonging to the user
     */
    public static void setSummoner(Summoner you) {
        uSummoner = you;
    }

    //TODO: update this so it makes sense
    public void logout() {
//        Intent temp_intent = new Intent(Main.this, Champions.class);
//        startActivity(temp_intent);
        setResult(Login.RESULT_LOGOUT);
        finish();
    }


    //TODO: remove this, this is a temporary onCLick method
    public void checkConnection() {
        if (locUtil.getLocation() != null) {
            Log.d(TAG, locUtil.getLocation().toString());
            Toast.makeText(this, locUtil.getLocation().toString(), Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "something went wrong");
        }
    }

    public void onRefreshClick() {
        if (refreshAvail) {
            updateUI();
            Toast.makeText(this, R.string.ui_refresh, Toast.LENGTH_SHORT).show();
            startTimer(R.id.refresh_ui);
        } else {
            Toast.makeText(this, R.string.refresh_interval, Toast.LENGTH_SHORT).show();

        }
    }

    private static void startTimer(final int item_id) {
        Log.d("timer", "start");
        new CountDownTimer(300000, 1000) {
            MenuItem temp = menu.findItem(item_id);

            public void onTick(long millisUntilFinished) {
                temp.setTitle(timeConversion(millisUntilFinished));
            }

            public void onFinish() {
                Log.d("timer", "finish");
                temp.setTitle(R.string.refresh_avail);
                refreshAvail = true;
                temp.setEnabled(true);

            }
        }.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh_ui:
                onRefreshClick();
                item.setEnabled(false);
                refreshAvail = false;
                return true;
            case R.id.check_conn:
                checkConnection();
                return true;
            case R.id.logout_option:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI() {
        printSummonerToScreen(uSummoner);
        String welcome_format = getResources().getString(R.string.welcome_back);
        String welcome_message = String.format(welcome_format, uSummoner.getName());
        welcome_lbl.setText(welcome_message);
        getPlayStatus();
        new DataDragonTask().execute(uSummoner.getProfileIconId() + "");
    }

    public static Boolean toggleBool(Boolean current_setting) {
        return !current_setting;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == FAILURE) {
            Log.d(TAG, "Error");
            Toast.makeText(this, R.string.lbl_set_fail, Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "other result");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
