package uoit.csci4100u.mobileapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import net.rithms.riot.api.endpoints.summoner.dto.Summoner;

import uoit.csci4100u.mobileapp.util.NetworkTask;

import static uoit.csci4100u.mobileapp.Main.riot_api;

/**
 * Set summoner is called only when the users Unique User ID (UUID) is not found in the database
 *
 * Handles all methods for setting up new Summoner
 * Ensures summoner name matches REGEX before using Async task to query Riot API
 * Uses extended NetworkTask -> RiotAPIGetSummoner
 */
public class SetSummoner extends AppCompatActivity {
    //regex for checking valid summoner names
    static final String REGEX = "^[0-9/\\/\\p{L} _\\/\\/.]+$";
    static final String TAG = "setsummoner.java";
    String[] locales;
    RiotAPIGetSummoner networkTask;
    String[] networkParam;
    EditText summonerField;

    String region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_summoner);

        networkParam = new String[2];
        summonerField = (EditText) findViewById(R.id.summoner_box);
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

    /**
     * Gets summoner name from the edit text field, checks for REGEX match and then starts an
     * asynchronous call to the Riot API
     *
     * @param v current Android view
     */
    public void finish(View v) {
        networkTask = new RiotAPIGetSummoner();
        String input = "";
        if (!isEmpty(summonerField)) {
            input = summonerField.getText().toString().trim();
            if (input.matches(REGEX)) {
                Log.d(TAG, "regex match");
                networkParam[1] = input;

                //starts the async task without blocking
//                networkTask.execute(networkParam);

                //start the async task with blocking for result
                try {
                    Main.setSummoner(networkTask.execute(networkParam).get());
                } catch (java.util.concurrent.ExecutionException e) {
                    Log.d(TAG, e.toString());
                } catch (java.lang.InterruptedException i) {
                    Log.d(TAG, i.toString());
                }
                setResult(Main.SUCCESS);
                finish();
            } else {
                Log.d(TAG, "regex fail");
                setResult(Main.FAILURE);
            }
        } else {
            Log.d(TAG, "error empty input field");
            setResult(Main.FAILURE);
        }
    }

    /**
     * Extends NetworkTask so we can have access to the methods in it
     *
     * Queries the Riot API if a valid Summoner name is provided and saves the returned Summoner
     * to the Summoner object in the Main method
     */
    static class RiotAPIGetSummoner extends NetworkTask{
    private Summoner summoner;
        //TODO: remove logs when done testing this method
        @Override
        protected Summoner doInBackground(String[] input) {
            Log.d("async task", "Started");
//            Log.d("input 0", input[0]); //platform/locale
//            Log.d("input 1", input[1]); //summoner name
            try {
                summoner = riot_api.getSummonerByName(checkPlatform(input[0]),
                        input[1]);
            } catch (net.rithms.riot.api.RiotApiException e) {
                Log.d(TAG, e.toString());
            }
            return summoner;
        }

        /**
         * Runs after download complete
         * Saves the returned Summoner object to the Main method
         *
         * @param Result Resulting Summoner object from Riot API
         */
        @Override
        protected void onPostExecute(Summoner Result) {
            Log.d("async task", "Finished");
            Main.setSummoner(Result);
            Main.acquired = true;
        }
    }
}
