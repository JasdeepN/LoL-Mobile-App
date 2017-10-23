package uoit.csci4100u.mobileapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

import static uoit.csci4100u.mobileapp.Main.riot_api;

public class SetSummoner extends AppCompatActivity {
    static final String REGEX = "^[0-9/\\/\\p{L} _\\/\\/.]+$";
    static final String TAG = "setsummoner.java";
    EditText summonerField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_summoner);
        setResult(Main.FAILURE);
        summonerField = (EditText) findViewById(R.id.summoner_box);
    }

    //false means not empty true means nothing in the string
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public void getSummonerName(View v){
        String input = "";
        if(!isEmpty(summonerField)){
            input = summonerField.getText().toString().trim();
            if(input.matches(REGEX)){
                Log.d(TAG, "regex match");
                new networkTask().execute(input);
            } else {Log.d(TAG, "regex fail");}
        } else {
            Log.d(TAG, "error empty input field");
        }
    }

    private class networkTask extends AsyncTask<String, Void, String>{
        Summoner summoner;
        @Override
        protected String doInBackground(String... strings) {
            Log.d("async task", "Started");
            try {
                summoner = riot_api.getSummonerByName(Platform.NA, strings[0]);
            } catch (net.rithms.riot.api.RiotApiException e){Log.d(TAG, e.toString());}
            return null;
        }

        @Override
        protected void onPostExecute(String Result){
            Log.d("async task", "Finished");
            testVomitSummoner(summoner);
        }
    }

    private void testVomitSummoner(Summoner x){
        String localTAG = "api reply: ";
        Log.d(localTAG, "name: " + x.getName());
        Log.d(localTAG, "account id: " + x.getAccountId());
        Log.d(localTAG, "id: " + x.getId());
        Log.d(localTAG, "level: " + x.getSummonerLevel());
        Log.d(localTAG, "last modified: " + x.getRevisionDate());
    }

}
