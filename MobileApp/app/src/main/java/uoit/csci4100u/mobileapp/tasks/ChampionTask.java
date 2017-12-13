package uoit.csci4100u.mobileapp.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.match.dto.MatchList;
import net.rithms.riot.api.endpoints.static_data.dto.ChampionList;
import net.rithms.riot.constant.Platform;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import uoit.csci4100u.mobileapp.Main;
import uoit.csci4100u.mobileapp.util.NetworkTask;

import static uoit.csci4100u.mobileapp.Main.current_version;
import static uoit.csci4100u.mobileapp.Main.locale;

/**
 * Created by jasdeep on 2017-12-12.
 */

public class ChampionTask extends NetworkTask<String, Void, ChampionList> {
    ChampionList champs;
    @Override
    protected ChampionList doInBackground(String... input) {
        try {
            champs = riot_api.getDataChampionList(locale);
        } catch (RiotApiException e) {
            e.printStackTrace();
        }
        return champs;
    }

    @Override
    protected void onPostExecute(ChampionList result) {
        Log.d("champ:end", "finished data dragon access");
        Log.d("champ:end",  result.getData()+"");
    }

    @Override
    protected void onPreExecute() {
        Log.d("champ:start", "starting data dragon access");
    }
}


