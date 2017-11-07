package uoit.csci4100u.mobileapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import net.rithms.riot.api.endpoints.summoner.dto.Summoner;

import java.util.ArrayList;
import java.util.List;

import uoit.csci4100u.mobileapp.util.NetworkTask;

//TODO: set the summoner ID globally (probably)
public class SetSummoner extends AppCompatActivity {
    //regex for checking valid summoner names
    static final String REGEX = "^[0-9/\\/\\p{L} _\\/\\/.]+$";
    static final String TAG = "setsummoner.java";
    String[] locales;
    NetworkTask networkTask;
    String[] networkParam;
    EditText summonerField;
    static TextView summ_info;
    String region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_summoner);

        networkParam = new String[2];
        summonerField = (EditText) findViewById(R.id.summoner_box);
        summ_info = (TextView) findViewById(R.id.summ_info);
//
        locales = getResources().getStringArray(R.array.Region_Array);
        setupSpinner();
    }

    /**
     * sets up spinner with resources from file, then puts the selected locale into the
     * networkParams array
     */
    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, locales);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner sItems = (Spinner) findViewById(R.id.locale_spinner);
        //Create listener for spinner including on item selected method
        sItems.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        region = (String) sItems.getSelectedItem();
                        Log.d(TAG, "id= " + region);
                        networkParam[0] = region;
                    }

                    //nothing will never be selected
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                        setResult(Main.CANCEL);
                    }
                });
        sItems.setAdapter(adapter);
    }


    /**
     * Checks to see if a EditText field is empty
     *
     * @param etText The EditText field to be checked
     * @return True if the field is empty; False otherwise
     */
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    //gets summoner name from the edit text field, checks for REGEX match and then starts an
    //asynchronous call to the Riot API
    public void getSummonerName(View v) {
        networkTask = new NetworkTask();
        String input = "";
        if (!isEmpty(summonerField)) {
            input = summonerField.getText().toString().trim();
            if (input.matches(REGEX)) {
                Log.d(TAG, "regex match");
                summ_info.setText(R.string.async_task);
                networkParam[1] = input;
                networkTask.execute(networkParam);

            } else {
                summ_info.setText(R.string.error_summ_name);
                Log.d(TAG, "regex fail");
            }
        } else {
            summ_info.setText(R.string.error_summ_name);
            Log.d(TAG, "error empty input field");
        }
    }

//TODO: remove this when done testing
//    public static void VomitSummoner(Summoner summoner) {
//        String localTAG = "api reply";
//        Log.d(localTAG, "name: " + summoner.getName());
//        Log.d(localTAG, "account id: " + summoner.getAccountId());
//        Log.d(localTAG, "id: " + summoner.getId());
//        Log.d(localTAG, "level: " + summoner.getSummonerLevel());
//        Log.d(localTAG, "last modified: " + summoner.getRevisionDate());
//    }

    /**
     * temporary method that prints summoner info to device screen
     *
     * @param x the Summoner object to be printed
     */
    public static void testVomitSummoner(Summoner x) {
        try {
            String retString = "Name: " + x.getName() + "\nAccount ID: " + x.getAccountId() +
                    "\nID: " + x.getId() + "\nLevel: " + x.getSummonerLevel() + "\nLast Modified:" +
                    x.getRevisionDate();

            summ_info.setText(retString);
        } catch (NullPointerException e) {
            summ_info.setText(R.string.cant_find_name);
        }
    }
}
