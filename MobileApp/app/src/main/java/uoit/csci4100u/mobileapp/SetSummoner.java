package uoit.csci4100u.mobileapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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
            } else {Log.d(TAG, "regex fail");}
        } else {
            Log.d(TAG, "error empty input field");
        }
    }

}
