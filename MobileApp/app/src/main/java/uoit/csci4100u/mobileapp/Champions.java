package uoit.csci4100u.mobileapp;
//
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
//
//
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.endpoints.static_data.dto.ChampionList;
import net.rithms.riot.constant.Platform;
import net.rithms.riot.api.endpoints.static_data.dto.*;
//
import java.util.HashMap;
import java.util.Map;
//
import uoit.csci4100u.mobileapp.util.NetworkTask;
//
//
////TODO: this works but need to change so the api is not being called on each request
public class Champions extends AppCompatActivity {
   final static String TAG = "champion.java";
//    static ChampionList champions;
//    RiotApi riot_api;
//    getNetChamp netTask;
    String champName;
    TextView champText;
//
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.champions);
 //      setup();
//
    }
//

    public void getChampion(View source)
    {
        champText = (TextView) findViewById(R.id.txtChampName);
        champName = champText.getText().toString();
        Log.d("TAG", champName);


    }
//    private void setup(){
//        try {
//            netTask = new getNetChamp();
//            riot_api = netTask.getAPI();
//            netTask.execute().get();
//        } catch (Exception e){
//            Log.d(TAG, "error");
//        }
//    }
//
//    static class getNetChamp extends NetworkTask<String, String> {
//
//        @Override
//        protected String doInBackground(String... strings) {
//            Log.d("async task", "Started");
//            try {
//                //stuff here
//                champions = riot_api.getDataChampionList(Platform.NA);
//            } catch (Exception e) {
//                Log.d(TAG, e.toString());
//            }
//            return null;
//        }
//
//
//        @Override
//        protected void onPostExecute(String Result) {
//            Log.d("async task", "Finished");
//            Champion temp_champ;
//            String temp = "";
//            Map<String, Champion> x = new HashMap<>();
//            x = champions.getData();
//            for (Object objname : x.keySet()) {
//                temp_champ = x.get(objname);
//                temp += "KEY: [" + objname + "] --> ";
//                temp += temp_champ.getName() + " LORE: ";
//                temp += temp_champ.getLore();
//                temp += "\n";
//            }
//            Log.d("downloaded", temp);
//        }
//    }
}
