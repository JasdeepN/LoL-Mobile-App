package uoit.csci4100u.mobileapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

public class Main extends AppCompatActivity {
    static ApiConfig config = new ApiConfig().setKey("RGAPI-1da05fb7-ed0d-4922-8e43-b1ece25ad65a");
    static RiotApi riot_api = new RiotApi(config);
    static final String TAG = "Main.java";
    static final int SUCCESS = 1;
    static final int FAILURE = 0;
    static final int REQUEST_SET_SUMMONER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void onSetSummonerClicked(View v){
        Intent setSummIntent = new Intent(Main.this, SetSummoner.class);
        startActivityForResult(setSummIntent, REQUEST_SET_SUMMONER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SET_SUMMONER && resultCode == SUCCESS){}
        else if (resultCode == FAILURE){Log.d(TAG, "Error");}
        else {Log.d(TAG, "other result");}
        super.onActivityResult(requestCode, resultCode, data);
    }
}
