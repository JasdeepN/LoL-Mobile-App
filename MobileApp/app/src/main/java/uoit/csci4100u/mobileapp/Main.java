package uoit.csci4100u.mobileapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;

import uoit.csci4100u.mobileapp.util.LocationUtil;
import uoit.csci4100u.mobileapp.util.PermissionChecker;

public class Main extends AppCompatActivity {
    //temp dev key change daily
    static ApiConfig config = new ApiConfig().setKey("RGAPI-f4491519-5480-4a7e-95d8-643d003e69b3");
    static RiotApi riot_api = new RiotApi(config);
    static final String TAG = "Main.java";
    static final int SUCCESS = 1;
    static final int FAILURE = 0;
    static final int REQUEST_SET_SUMMONER = 2;

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

    //TODO: set summoner globally so other methods can use unique summoner ID
    public void onSetSummonerClicked(View v) {
        Intent setSummIntent = new Intent(Main.this, SetSummoner.class);
        startActivityForResult(setSummIntent, REQUEST_SET_SUMMONER);
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
        } else if (resultCode == FAILURE) {
            Log.d(TAG, "Error");
        } else {
            Log.d(TAG, "other result");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
