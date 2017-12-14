package uoit.csci4100u.mobileapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;

import net.rithms.riot.api.endpoints.match.dto.Match;
import net.rithms.riot.api.endpoints.match.dto.MatchReference;
import net.rithms.riot.api.endpoints.static_data.dto.ChampionList;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import uoit.csci4100u.mobileapp.tasks.ChampionListTask;
import uoit.csci4100u.mobileapp.tasks.GetMatches;
import uoit.csci4100u.mobileapp.tasks.MatchInfo;
import uoit.csci4100u.mobileapp.tasks.ProfileIconTask;
import uoit.csci4100u.mobileapp.util.DatabaseHelperUtil;
import uoit.csci4100u.mobileapp.util.LocationUtil;
import uoit.csci4100u.mobileapp.util.NetworkTask;
import uoit.csci4100u.mobileapp.util.OnGetDataListener;
import uoit.csci4100u.mobileapp.util.PermissionChecker;

/**
 * Main method for App, handles setting up and receiving Activities/Results
 * <p>
 * TODO: currently uses temp development key for Riot API - check to see if key is still valid
 * before changing anything
 *
 * @see PermissionChecker#getPermissions()          - to check is permissions are granted, if not asks for
 * them
 * @see LocationUtil                                - updates location information
 * @see uoit.csci4100u.mobileapp.util.NetworkTask   - generic abstract class to handle most of the
 * Async network tasks
 * @see DatabaseHelperUtil                          - handles Firebase read and write
 */
public class Main extends AppCompatActivity {
    static String mUUID = "";
    static final String TAG = "Main.java";
    static final int SUCCESS = 1;
    static final int FAILURE = 0;
    static final int CANCEL = -1;
    static final int REQUEST_PLAYERS = 2;
    static ArrayList<Match> recentMatches;
    protected static boolean play_staus;
    protected static ChampionList champions;
    protected static String reigon;
    //current game version
    public static String current_version;
    static Context mContext;
    Bundle extras;
    TextView summoner_info;
    TextView welcome_lbl;
    ToggleButton avail_button;
    static ImageView icon;

    //the users summoner info
    protected static Summoner uSummoner;
    public static Platform locale;

    //New Location util
    protected LocationUtil locUtil;

    protected static DatabaseHelperUtil dbHelper;
    protected GoogleApiClient locApi;

    private static Menu menu;
    static MatchAdapter mAdapter;
    static ListView matchList;

    private static boolean refreshAvail = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = getApplicationContext();
        locUtil = new LocationUtil(this);
        setupLocAPI();
        setUpLayouts();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onStart() {
        recentMatches = new ArrayList<>();
        recentMatches.clear();

        extras = getIntent().getExtras();
        mUUID = extras.getString("UUID");
        reigon = extras.getString("locale");
        locale = NetworkTask.checkPlatform(reigon);
        current_version = extras.getString("version");
        new ChampionListTask().execute(reigon);

        Log.d("version received", current_version + "");

        locApi.connect();
        locUtil.setLocAPI(locApi);
        updateUI();

        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    protected void onResume() {
        //update without having to relaunch app
        super.onResume();
    }

    @Override
    protected void onStop() {
        locApi.disconnect();
        super.onStop();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
//        this.refreshItem = menu.getItem(0);
        startTimer(R.id.refresh_ui);
//        refreshItem.setEnabled(false);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Callback method for ChampionListTask that downloads a ChampionList from Riot API
     *
     * @param result
     * @see ChampionListTask
     */
    public static void setChampList(ChampionList result) {
        champions = result;
    }

    /**
     * sets up Google location API
     */
    private void setupLocAPI() {
        locApi = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(locUtil)
                .addOnConnectionFailedListener(locUtil)
                .build();
    }

    /**
     * method to set database helper in Main method
     *
     * @param helper
     * @see DatabaseHelperUtil
     */
    public static void setDBHelper(DatabaseHelperUtil helper) {
        dbHelper = helper;
    }


    /**
     * Instantiates and assigns global variables related to the layout
     *
     *
     */
    public void setUpLayouts() {
        summoner_info = (TextView) findViewById(R.id.summoner_info);
        welcome_lbl = (TextView) findViewById(R.id.welcome_lbl);
        avail_button = (ToggleButton) findViewById(R.id.avail_button);
        icon = (ImageView) findViewById(R.id.summoner_icon);
        matchList = (ListView) findViewById(R.id.matches);
//        matchList.setAdapter(mAdapter);

//        matchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Match item = (Match) adapterView.getItemAtPosition(i);
////
//                Intent intent = new Intent(Main.this, MatchDetailView.class);
//                intent.putExtra("matchID", item.getGameId());
////                //based on item add info to intent
//                startActivity(intent);
////                Log.d("clicked", item+"");
//            }
//        });

        avail_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Log.d("Toggle", "ON");

                } else {
                    // The toggle is disabled
                    Log.d("Toggle", "OFF");
                }
            }
        });
        avail_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play_staus = toggleStatus(play_staus);
            }
        });
    }


    /**
     * Method that prints summoner info to device screen
     *
     * @param x the Summoner object to be printed
     */
    public void printSummonerToScreen(Summoner x) {
        try {
            String retString = "Name: " + x.getName() + "\nAccount ID: " + x.getAccountId() +
                    "\nID: " + x.getId() + "\nLevel: " + x.getSummonerLevel() + "\nLast Modified:" +
                    x.getRevisionDate();


            this.summoner_info.setText(retString);
        } catch (NullPointerException e) {
            this.summoner_info.setText(R.string.cant_find_name);
        }
    }


    /**
     * Converts time in millis to minutes:seconds
     *
     * @param millis
     * @return String in format Minutes:Seconds
     */
    private static String timeConversion(long millis) {

        String hms = String.format(Locale.getDefault(), "%02d:%02d",

                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return hms;
    }


    /**
     * gets the searching for game Boolean from the Firebase
     *
     * @return Boolean True if the user is looking for a game and False if they are not
     */
    private void getPlayStatus() {
        dbHelper.getCurrentStatus(new OnGetDataListener() {

            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                play_staus = dataSnapshot.getValue(Boolean.class);
                avail_button.setChecked(play_staus);
                Log.d("getPlayStatus", "received " + play_staus);
            }

            @Override
            public void onStart() {
                Log.d("getPlayStatus", "getting play status");
            }

            @Override
            public void onFailure() {

            }
        });

    }

    /**
     * Toggles play status status on firebase
     *
     * @param status
     * @return !status
     */
    private Boolean toggleStatus(Boolean status) {
        return dbHelper.togglePlay(status);
    }


    /**
     * Getter for methods that need the users summoner info
     *
     * @return Summoner object
     */
    public static Summoner getSummoner() {
        return uSummoner;
    }

    /**
     * Setter for uSummoner
     *
     * @param you Summoner object belonging to the user
     */
    public static void setSummoner(Summoner you) {
        uSummoner = you;
    }

    /**
     * Logout method
     */
    public void logout() {
        setResult(Login.RESULT_LOGOUT);
        finish();
    }


    //TODO: remove this, this is a temporary onCLick method
    public void checkConnection() {
        if (locUtil.getLocation() != null) {
            Log.d(TAG, locUtil.getLocation().toString());
            Toast.makeText(this, locUtil.getLocation().toString(), Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "something went wrong");
        }
    }

    /**
     * forces the UI to update
     */
    public void onRefreshClick() {
        if (refreshAvail) {
        forceUpdateUI();
            Toast.makeText(this, R.string.ui_refresh, Toast.LENGTH_SHORT).show();
            startTimer(R.id.refresh_ui);
        } else {
            Toast.makeText(this, R.string.refresh_interval, Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * Starts timer
     * @param item_id
     */
    private static void startTimer(final int item_id) {
        Log.d("timer", "start");
        new CountDownTimer(300000, 1000) {
            MenuItem temp = menu.findItem(item_id);

            public void onTick(long millisUntilFinished) {
                temp.setTitle(timeConversion(millisUntilFinished));
            }

            public void onFinish() {
                Log.d("timer", "finish");
                temp.setTitle(R.string.refresh_avail);
                refreshAvail = true;
                temp.setEnabled(true);

            }
        }.start();
    }

    /**
     * overrides the default onOptionsItemSelected
     * @param item
     * @return Boolean success
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh_ui:
                onRefreshClick();
                item.setEnabled(false);
                refreshAvail = false;
                return true;
            case R.id.check_conn:
                checkConnection();
                return true;
            case R.id.logout_option:
                logout();
                return true;
            case R.id.other_players:
                launchOtherPlayers();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Launches intent for view containing other players
     */
    private void launchOtherPlayers() {
        Intent intent = new Intent(Main.this, Players.class);
        startActivityForResult(intent, REQUEST_PLAYERS);
    }

    /**
     * Updates UI
     */
    private void updateUI() {
        printSummonerToScreen(uSummoner);
        String welcome_format = getResources().getString(R.string.welcome_back);
        String welcome_message = String.format(welcome_format, uSummoner.getName());
        welcome_lbl.setText(welcome_message);
        getPlayStatus();
        new ProfileIconTask().execute(uSummoner.getProfileIconId() + "");
        new GetMatches().execute(uSummoner);

        refreshAvail = false;
    }

    /**
     * Forces update to UI
     */
    private void forceUpdateUI() {
//            printSummonerToScreen(uSummoner);
//            String welcome_format = getResources().getString(R.string.welcome_back);
//            String welcome_message = String.format(welcome_format, uSummoner.getName());
//            welcome_lbl.setText(welcome_message);
//            getPlayStatus();
        new ProfileIconTask().execute(uSummoner.getProfileIconId() + "");
        new GetMatches().execute(uSummoner);
    }


    /**
     * Callback method for setting icons
     * @param result Bitmap downloaded from DataDragon
     * @see uoit.csci4100u.mobileapp.tasks.ChampionIconTask
     */
    public static void setIcon(Bitmap result) {
        icon.setImageBitmap(result);
    }

    /**
     * Callback method for get match list method
     * @param matches list of recent Matches from Riot API
     * @see GetMatches
     */
    public static void setMatchList(List<MatchReference> matches) {
        initAdapter(matches);
    }

    /**
     * Sets up custom adapter for match ListView
     * @param matches List of recent Matches
     */
    private static void initAdapter(List<MatchReference> matches) {
        //initalize empty adapter
        Log.d("Main:setMatchList", "got recent matches");
        int count = 0;
        int MATCH_COUNT = 3;
        for (MatchReference x : matches) {
            if (count < MATCH_COUNT) {
                Log.d("Match" + count, x.toString());
                try {
                    recentMatches.add(new MatchInfo().execute(x.getGameId()).get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                count++;
            } else if(count >= MATCH_COUNT){
                mAdapter = new MatchAdapter(mContext, recentMatches);
                matchList.setAdapter(mAdapter);
                return;
            }
        }

    }

    /**
     * Adds match to adapter and notifies
     * @param newMatch New match to be added to adapter
     * @see MatchInfo
     */
    public static void addMatch(Match newMatch) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == FAILURE) {
            Log.d(TAG, "Error");
//            Toast.makeText(this, R.string.lbl_set_fail, Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_PLAYERS && resultCode == SUCCESS) {
            Log.d(TAG, "returned from other players sucessfully");

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //class made to go to layout to display the list of champions
    public void gotoChampions(View source) {
        Intent displayChamps = new Intent(Main.this, Champions.class);
        startActivityForResult(displayChamps, 101);
    }

}
