package uoit.csci4100u.mobileapp.util;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

import java.util.ArrayList;

import uoit.csci4100u.mobileapp.SetSummoner;

import static uoit.csci4100u.mobileapp.Main.riot_api;

/**
 * Created by jasdeep on 2017-11-06.
 *
 * Handles all network access asynchronously
 */

public class NetworkTask extends AsyncTask<String, Void, Summoner> {
    private static final String TAG = "Network Access";
    private Summoner summoner;

    //TODO: remove logs when done testing this method
    @Override
    protected Summoner doInBackground(String[] input) {
        Log.d("async task", "Started");
        Log.d("input 0", input[0]); //platform/locale
        Log.d("input 1", input[1]); //summoner name
        try {
            summoner = riot_api.getSummonerByName(checkPlatform(input[0]), input[1]);
        } catch (net.rithms.riot.api.RiotApiException e) {
            Log.d(TAG, e.toString());
        }
        return summoner;
    }

    //run this after download complete
    @Override
    protected void onPostExecute(Summoner Result) {
        Log.d("async task", "Finished");
        //print to device
        SetSummoner.testVomitSummoner(Result);
    }

    //returns the summoner name
    public Summoner getSummoner() {
        return summoner;
    }

    /**
     * Checks which platform was selected and returns the corresponding Platform object
     *
     * @param locale which region the Summoner is in
     * @return Platform object that matches the locale
     */
    private Platform checkPlatform(String locale) {
        if (locale.equals("BR")) {
             return Platform.BR;
        } else if (locale.equals("EUNE")) {
             return Platform.EUNE;
        } else if (locale.equals("EUW")) {
             return Platform.EUW;
        } else if (locale.equals("JP")) {
             return Platform.JP;
        } else if (locale.equals("KR")) {
             return Platform.KR;
        } else if (locale.equals("LAN")) {
             return Platform.LAN;
        } else if (locale.equals("LAS")) {
             return Platform.LAS;
        } else if (locale.equals("NA")) {
             return Platform.NA;
        } else if (locale.equals("OCE")) {
             return Platform.OCE;
        } else if (locale.equals("TR")) {
             return Platform.TR;
        } else {
             return Platform.RU;
        }
    }

}
