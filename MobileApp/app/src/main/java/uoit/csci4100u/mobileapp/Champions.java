package uoit.csci4100u.mobileapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import net.rithms.riot.api.endpoints.static_data.dto.ChampionList;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;
import net.rithms.riot.api.endpoints.static_data.dto.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static uoit.csci4100u.mobileapp.Main.riot_api;

public class Champions extends AppCompatActivity {
    final String TAG = "champion.java";
    ChampionList champions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.champions);
        new networkTask().execute();
    }

    private class networkTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Log.d("async task", "Started");
            try {
                //stuff here
                champions = riot_api.getDataChampionList(Platform.NA);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String Result) {
            Log.d("async task", "Finished");
            Champion temp_champ;
            String temp = "";
            Map<String, Champion> x = new HashMap<>();
            x = champions.getData();
            for (Object objname : x.keySet()) {
                temp_champ = x.get(objname);
                temp += "KEY: [" + objname + "] --> ";
                temp += temp_champ.getName() + " LORE: ";
                temp += temp_champ.getLore();
                temp += "\n";
            }
            Log.d("downloaded", temp);
        }
    }
}
