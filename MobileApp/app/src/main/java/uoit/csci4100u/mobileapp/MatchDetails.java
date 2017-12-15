package uoit.csci4100u.mobileapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.rithms.riot.api.endpoints.match.dto.Match;
import net.rithms.riot.api.endpoints.match.dto.Participant;
import net.rithms.riot.api.endpoints.match.dto.ParticipantStats;
import net.rithms.riot.api.endpoints.static_data.dto.Champion;
import net.rithms.riot.api.endpoints.static_data.dto.Image;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import uoit.csci4100u.mobileapp.tasks.ChampionIconTask;

import static uoit.csci4100u.mobileapp.Main.champions;
import static uoit.csci4100u.mobileapp.Main.current_version;
import static uoit.csci4100u.mobileapp.Main.mContext;
import static uoit.csci4100u.mobileapp.Main.recentMatches;

/**
 * Created by wesley on 13/12/17.
 */

public class MatchDetails extends AppCompatActivity {

    Map<String, Champion> champList;
    static protected final String BASE_DRAGON_URL = "http://ddragon.leagueoflegends.com/cdn/";
    int matchID;
    Bitmap[] bitmapArray = new Bitmap[10];
    List<ImageView> iconView = new ArrayList<>();
    List<String> playerScore = new ArrayList<>();
    ParticipantStats playerStats;
    final static String win = "win";
    final static String lose = "lose";
//    KDAAdapterLeft blueTeam;
ArrayAdapter<String> blueTeam;
    ArrayAdapter<String> redTeam;
    ArrayList<String> blueTeamArray = new ArrayList<>();
    ArrayList<String> redTeamArray = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_details);

        Bundle extras = getIntent().getExtras();
        matchID = extras.getInt("matchID");
        Log.d("MatchDetailsFor", matchID+"");
        setupLayout();
    }

    private void setupLayout() {
        String gameMode;
        String gameType;
        long gameLength;
        List<Participant> players;
        champList = champions.getData();

        Boolean didBlueWin;
        String player, playerStats;

        Match CurrentMatch;
        CurrentMatch = recentMatches.get(matchID);

        players = CurrentMatch.getParticipants();

        addImages();

        String[] playerList = new String[10];
        Collection<Champion> c = champList.values();
        for (int i = 0; i < 10; i++) {
            Log.d("WHEN DOES THIS CRASH: ", Integer.toString(i));
            Participant x = players.get(i);
            Log.d("SETTING PARTICIPANT ", Integer.toString(i));
            final int champId = x.getChampionId();
            //set this in case want to get more data from champ later
            Champion playedChamp;
            Log.d("champ:ID", champId + "");
            for (Champion y : c) {
                if (y.getId() == champId) {
                    playedChamp = y;
                    new ChampionIcon().execute(playedChamp.getName(), i + "");
                    iconView.get(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent details = new Intent(MatchDetails.this, ChampionDetails.class);
                            details.putExtra("champID", champId);
                            startActivity(details);
                        }
                    });
//                    try {
//                        champIcons.get(i).setImageBitmap(new ChampionIconTask().execute(playedChamp
//                                .getName(), i + "").get());
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    }
                    playerList[i] = playedChamp.getName();
                }
            }
        }


        for (int i = 0; i < 5; i++)
        {
            playerStats = Integer.toString(CurrentMatch.getParticipants().get(i).getStats().getKills())
                    + "/"
                    + Integer.toString(CurrentMatch.getParticipants().get(i).getStats().getDeaths())
                    + "/"
                    +Integer.toString(CurrentMatch.getParticipants().get(i).getStats().getAssists());

            player = playerList[i] + " " + playerStats;

            blueTeamArray.add(player);
        }

        for (int j = 5; j < 10; j++)
        {
            playerStats = Integer.toString(CurrentMatch.getParticipants().get(j).getStats().getKills())
                    + "/"
                    + Integer.toString(CurrentMatch.getParticipants().get(j).getStats().getDeaths())
                    + "/"
                    +Integer.toString(CurrentMatch.getParticipants().get(j).getStats().getAssists());

            player = playerList[j] + " " + playerStats;

           redTeamArray.add(player);
        }

        ArrayList temp = new ArrayList<>();
//        temp.addAll(blueTeamArray);
//        blueTeam = new KDAAdapterLeft(this, blueTeamArray);
        redTeam = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, redTeamArray);
        blueTeam = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, blueTeamArray);

        ListView blueListView = (ListView) findViewById(R.id.lvBlueTeam);
        blueListView.setAdapter(blueTeam);

        ListView redListView = (ListView) findViewById(R.id.lvRedTeam);
        redListView.setAdapter(redTeam);


        TextView tvGameType;
        TextView tvGameMode;
        TextView tvWinner;
        TextView tvMatchLength;

        tvGameType = (TextView) findViewById(R.id.match_time);
        tvGameMode = (TextView) findViewById(R.id.match_map);
        tvWinner = (TextView) findViewById(R.id.winner);
        tvMatchLength = (TextView) findViewById(R.id.match_length);




        CurrentMatch.getGameMode();
        // Log.d("CURRENT TEAM: ", CurrentMatch.get);
        CurrentMatch.getGameType();
        gameLength = CurrentMatch.getGameDuration();
        String format = getBaseContext().getResources().getString(R.string.match_time);
        String timex = DateUtils.formatElapsedTime(gameLength);
        String time = String.format(format, timex);





        //Game mode == ARAM,5V5,3V3, PVE
        //Game type == ranked, normals, Draft
        Log.d("GAME MODE:", CurrentMatch.getGameMode());
        Log.d("GAME TYPE:", CurrentMatch.getGameType());

        gameMode = "Current Game mode: " + CurrentMatch.getGameMode();
        gameType = CurrentMatch.getGameType();

        didBlueWin = CurrentMatch.getParticipants().get(0).getStats().isWin();

        String winner = "";

        if (didBlueWin)
        {
            winner = "Winner: Blue Team";
        }
        else
        {
           winner = "Winner: Red Team";
        }

        tvGameMode.setText(gameMode);
        tvGameType.setText(gameType);
        tvWinner.setText(winner);
        tvMatchLength.setText(time);

    }

    public class KDAAdapterLeft extends ArrayAdapter<String> {
        ArrayList<ImageView> team_icon = new ArrayList<>();
        public KDAAdapterLeft(Context context, List<String> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            String spell = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.detail_kda_item_left, parent,
                        false);
            }
            // Lookup view for data population
            TextView kda = (TextView) convertView.findViewById(R.id.kda);
            ImageView icon = (ImageView) convertView.findViewById(R.id.detail_champ_icon);
            team_icon.add(icon);
            // Populate the data into the template view using the data object
            kda.setText(spell);
//            icon.setImageBitmap(bitmapArray[position]);
//            Log.d("setting imgage", bitmapArray[position].toString());
//            icon.setImageBitmap(new ChampionIconTask().execute(playedChamp.getName(), i + "").get());
//            spellDesc.setText(spell.getDescription());
            // Return the completed view to render on screen
            return convertView;
        }

        public void setIcon(int i, Bitmap bm){
            Log.d("set Icon", i+"");
            team_icon.get(i).setImageBitmap(bm);
        }
    }

    public void addImages()
    {
        ImageView champ0 = (ImageView) findViewById(R.id.icon1);
        ImageView champ1 = (ImageView) findViewById(R.id.icon2);
        ImageView champ2 = (ImageView) findViewById(R.id.icon3);
        ImageView champ3 = (ImageView) findViewById(R.id.icon4);
        ImageView champ4 = (ImageView) findViewById(R.id.icon5);
        ImageView champ5 = (ImageView) findViewById(R.id.icon6);
        ImageView champ6 = (ImageView) findViewById(R.id.icon7);
        ImageView champ7 = (ImageView) findViewById(R.id.icon8);
        ImageView champ8 = (ImageView) findViewById(R.id.icon9);
        ImageView champ9 = (ImageView) findViewById(R.id.icon10);
        iconView.add(champ0);
        iconView.add(champ1);
        iconView.add(champ2);
        iconView.add(champ3);
        iconView.add(champ4);
        iconView.add(champ5);
        iconView.add(champ6);
        iconView.add(champ7);
        iconView.add(champ8);
        iconView.add(champ9);
    }

    public class ChampionIcon extends AsyncTask<String, Void, Bitmap> {
        // http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/Aatrox.png
        static protected final String BASE_DRAGON_URL = "http://ddragon.leagueoflegends.com/cdn/";

        int i;
        @Override
        protected Bitmap doInBackground(String... input) {
            Bitmap bm = null;
            i = Integer.parseInt(input[1]);
            try {
                String champNameFormatted = input[0].replace(" ", "");
                if (input[0].contains("'")){
                    String temp0 = input[0].replace("'", "");
                    String temp = temp0.substring(0, 1);
                    String temp2 = temp0.substring(1).toLowerCase();
                    champNameFormatted = temp+temp2;
                }

                String tempUrl = BASE_DRAGON_URL + current_version + "/img/champion/" +
                        champNameFormatted + ".png";
                Log.d("DataDragon:lookup", tempUrl + "");
                URL url = new URL(tempUrl);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream is = urlConnection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (java.net.MalformedURLException me) {
                Log.d("URL ERROR", me + "");
            } catch (java.io.IOException ie) {
                Log.d("IO ERROR", ie + "");
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            Log.d("champIcon:end", "finished data dragon access");
//        MatchAdapter.setChampIcon(result, i);
            bitmapArray[i] = result;

//            blueTeam.setIcon(i, result);
            iconView.get(i).setImageBitmap(result);
        }

        @Override
        protected void onPreExecute() {
            Log.d("champIcon:start", "starting data dragon access");
        }
    }


}



