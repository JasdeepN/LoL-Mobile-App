package uoit.csci4100u.mobileapp;

import android.content.Context;
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

import uoit.csci4100u.mobileapp.util.NetworkTask;

import static uoit.csci4100u.mobileapp.Main.riot_api;

//TODO: set the summoner ID globally (probably)
public class SetSummoner extends AppCompatActivity {
    //regex for checking valid summoner names
    static final String REGEX = "^[0-9/\\/\\p{L} _\\/\\/.]+$";
    static final String TAG = "setsummoner.java";
    String[] locales;
    RiotNetworkTask networkTask;
    String[] networkParam;
    EditText summonerField;
    TextView summ_info;
    String region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_summoner);

        networkParam = new String[2];
        summonerField = (EditText) findViewById(R.id.summoner_box);
        summ_info = (TextView) findViewById(R.id.summ_info);
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
     *          <p>
     *          TODO: NetworkTask will eventually become an interface meaning doInBackground() and
     *          onPostExecute() will have to be implemented
     */
    public void getSummonerName(View v) {
        networkTask = new RiotNetworkTask();
        String input = "";
        if (!isEmpty(summonerField)) {
            input = summonerField.getText().toString().trim();
            if (input.matches(REGEX)) {
                Log.d(TAG, "regex match");
                summ_info.setText(R.string.async_task);
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

                printSummonerToScreen(Main.getSummoner());
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
    public void printSummonerToScreen(Summoner x) {
        try {
            String retString = "Name: " + x.getName() + "\nAccount ID: " + x.getAccountId() +
                    "\nID: " + x.getId() + "\nLevel: " + x.getSummonerLevel() + "\nLast Modified:" +
                    x.getRevisionDate();

            this.summ_info.setText(retString);
        } catch (NullPointerException e) {
            this.summ_info.setText(R.string.cant_find_name);
        }
    }

    /**
     * Extends NetworkTask so we can have access to the methods in it
     *
     * Queries the Riot API if a valid Summoner name is provided and saves the returned Summoner
     * to the Summoner object in the Main method
     */
    static class RiotNetworkTask extends NetworkTask{
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
