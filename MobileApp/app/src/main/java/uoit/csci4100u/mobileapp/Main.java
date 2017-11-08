package uoit.csci4100u.mobileapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;

import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;

import uoit.csci4100u.mobileapp.util.DatabaseHelperUtil;
import uoit.csci4100u.mobileapp.util.LocationUtil;
import uoit.csci4100u.mobileapp.util.PermissionChecker;

import static java.security.AccessController.getContext;

/**
 * Main method for App, handles setting up and receiving Activities/Results
 * <p>
 * currently uses temp development key for Riot API - check to see if key is still valid before
 * changing anything
 * <p>
 * Uses util Class PermissionChecker to check for android permissions
 * Uses util Class LocationUtil to listen for and respond to GPS changes
 * Uses Abstract class NetworkTask which contains methods needed for Riot's API calls
 */
public class Main extends AppCompatActivity {
    //temp dev key
    static final String API_KEY = "RGAPI-61d7bd4d-a1b7-466c-bb7a-4c2d2d1385f2";
    static String UUID = "";
    static ApiConfig config = new ApiConfig().setKey(API_KEY);
    static public RiotApi riot_api = new RiotApi(config);
    static final String TAG = "Main.java";
    static final int REQUEST_SET_SUMMONER = 2;
    static final int SUCCESS = 1;
    static final int FAILURE = 0;
    static final int CANCEL = -1;
    public static boolean acquired;
    Bundle extras;
    TextView summoner_info;
    TextView welcome_lbl;
    ToggleButton avail_button;
    //the users summoner info
    protected static Summoner uSummoner;

    //New Location util
    private LocationUtil locUtil;

    //database helper
    private DatabaseHelperUtil dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setUpLayouts();
    }

    @Override
    protected void onStart() {
        extras = getIntent().getExtras();
        UUID = extras.getString("UUID");

        //checks for permissions
        new PermissionChecker(getBaseContext(), this).getPermissions();

        //starts the location listener
        locUtil = new LocationUtil(this);

        dbHelper = new DatabaseHelperUtil();
        acquired = false;

        checkIfUserExists(UUID);

        super.onStart();
    }

    @Override
    protected void onStop() {
        //ghost method left over from networkUtil
        super.onStop();
    }

    /**
     * Instantiates and assigns global variables related to the layout
     */
    public void setUpLayouts(){
        summoner_info = (TextView) findViewById(R.id.summoner_info);
        welcome_lbl = (TextView) findViewById(R.id.welcome_lbl);
        avail_button = (ToggleButton) findViewById(R.id.avail_button);

        avail_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Log.d("Toggle", "ON");
                } else {
                    // The toggle is disabled
                    Log.d("Toggle", "OFF");
                }
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

    /**
     * Checks Firebase to see if there is a Summoner object saved with the users UUID, if it
     * finds it launches the run() method
     *
     * @see Main#run()
     * @see DatabaseHelperUtil#readData(DatabaseHelperUtil.OnGetDataListener)
     * @param UUID unique user id
     */
    private void checkIfUserExists(final String UUID) {
        //tells the user whats happening
        summoner_info.setText(R.string.async_task);
        dbHelper.readData(new DatabaseHelperUtil.OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {

                Log.d("data GOT BACK", dataSnapshot.getValue() + "");
                for (DataSnapshot dChild : dataSnapshot.getChildren()) {
                    if (dChild.getKey().equals(UUID)) {
                        Log.d(TAG, "child key >>> " + dChild.getKey());
                        Log.d(TAG, "Summoner " + dChild.getValue(Summoner.class));
                        setSummoner(dChild.getValue(Summoner.class));
                        run();
                        //if it hits this break found user
                        break;
                    }
                }
                //otherwise should hit this
                run();
            }

            @Override
            public void onStart() {
                //when starting
                Log.d(TAG, "Started database read");
            }

            @Override
            public void onFailure() {
                Log.d("onFailure", "Failed");
            }
        });

    }

    /**
     * Simply checks if the user has a Summoner object that is valid and prints the info to the
     * screen; if the Summoner object is
     */
    private void run() {
        if (acquired) {
            printSummonerToScreen(uSummoner);
            String welcome_format = getResources().getString(R.string.welcome_back);
            String welcome_message = String.format(welcome_format, uSummoner.getName());
            welcome_lbl.setText(welcome_message);
        } else {
            Intent setSummIntent = new Intent(Main.this, SetSummoner.class);
            startActivityForResult(setSummIntent, REQUEST_SET_SUMMONER);
        }
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
        Main.uSummoner = you;
        acquired = true;
    }

    //TODO: update this so it makes sense
    public void temp_click(View v) {
        Intent temp_intent = new Intent(Main.this, Champions.class);
        startActivity(temp_intent);
    }


    public void onSetSummonerClicked(View v) {

    }

    //TODO: remove this, this is a temporary onCLick method
    public void checkConnection(View v) {
        if (locUtil.getLocation() != null) {
            Log.d(TAG, locUtil.getLocation().toString());
        } else {
            Log.d(TAG, "something went wrong");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SET_SUMMONER && resultCode == SUCCESS) {
            Toast.makeText(this, R.string.lbl_set, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "write to DB");
            dbHelper.addUser(UUID, uSummoner);
        } else if (resultCode == FAILURE) {
            Log.d(TAG, "Error");
            Toast.makeText(this, R.string.lbl_set_fail, Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "other result");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
