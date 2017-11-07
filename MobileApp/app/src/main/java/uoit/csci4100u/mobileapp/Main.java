package uoit.csci4100u.mobileapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;

import uoit.csci4100u.mobileapp.util.LocationUtil;
import uoit.csci4100u.mobileapp.util.PermissionChecker;

/**
 * Main method for App, handles setting up and receiving Activities/Results
 *
 * currently uses temp development key for Riot API - check to see if key is still valid before
 * changing anything
 *
 * Uses util Class PermissionChecker to check for android permissions
 * Uses util Class LocationUtil to listen for and respond to GPS changes
 */
public class Main extends AppCompatActivity {
    //temp dev key change daily
    static final String API_KEY = "RGAPI-c4fd5a72-66df-43c1-94c7-1634213aca85";
    static ApiConfig config = new ApiConfig().setKey(API_KEY);
    static public RiotApi riot_api = new RiotApi(config);
    static final String TAG = "Main.java";
    static final int REQUEST_SET_SUMMONER = 2;
    static final int SUCCESS = 1;
    static final int FAILURE = 0;
    static final int CANCEL = -1;
    public static boolean acquired;

    //the users summoner info
    protected static Summoner uSummoner;

    //New Location util
    private LocationUtil locUtil;

    //permission checker class
    private PermissionChecker checker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onStart() {
        //checks for location permissions
        checker = new PermissionChecker(getBaseContext(), this);
        checker.getPermissions();

        //starts the location listener
        locUtil = new LocationUtil(this);

        super.onStart();
    }

    @Override
    protected void onStop() {
        //ghost method left over from networkUtil
        super.onStop();
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
    }


    public void onSetSummonerClicked(View v) {
        if (!acquired) {
            Intent setSummIntent = new Intent(Main.this, SetSummoner.class);
            startActivityForResult(setSummIntent, REQUEST_SET_SUMMONER);
        } else {
            Toast.makeText(this, R.string.already_set, Toast.LENGTH_SHORT).show();
        }
    }

    //TODO: update this so it makes sense
    public void temp_click(View v) {
        Intent temp_intent = new Intent(Main.this, Champions.class);
        startActivity(temp_intent);
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
        } else if (resultCode == FAILURE) {
            Log.d(TAG, "Error");
            Toast.makeText(this, R.string.lbl_set_fail, Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "other result");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
