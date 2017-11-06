package uoit.csci4100u.mobileapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

import org.w3c.dom.Text;

import static uoit.csci4100u.mobileapp.Main.riot_api;

public class SetSummoner extends AppCompatActivity {
    //regex for checking valid summoner names
    static final String REGEX = "^[0-9/\\/\\p{L} _\\/\\/.]+$";
    static final String TAG = "setsummoner.java";
    EditText summonerField;
    TextView summ_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_summoner);
        setResult(Main.FAILURE);
        summonerField = (EditText) findViewById(R.id.summoner_box);
        summ_info = (TextView) findViewById(R.id.summ_info);
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
                summ_info.setText(R.string.async_task);
                new networkTask().execute(input);
            } else {
                summ_info.setText(R.string.error_summ_name);
                Log.d(TAG, "regex fail");
            }
        } else {
            summ_info.setText(R.string.error_summ_name);
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
            } catch (net.rithms.riot.api.RiotApiException e){
                Log.d(TAG, e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String Result){
            Log.d("async task", "Finished");
            testVomitSummoner(summoner);
        }
    }

    private void testVomitSummoner(Summoner x){
        if(x != null) {
//            String localTAG = "api reply";
//            Log.d(localTAG, "name: " + x.getName());
//            Log.d(localTAG, "account id: " + x.getAccountId());
//            Log.d(localTAG, "id: " + x.getId());
//            Log.d(localTAG, "level: " + x.getSummonerLevel());
//            Log.d(localTAG, "last modified: " + x.getRevisionDate());

            String retString = "Name: " + x.getName() + "\nAccount ID: " + x.getAccountId() +
                    "\nID: " + x.getId() + "\nLevel: " + x.getSummonerLevel() + "\nLast Modified:" +
                    x.getRevisionDate();

            summ_info.setText(retString);
        } else {
            summ_info.setText(R.string.cant_find_name);
        }
    }
}
