package uoit.csci4100u.mobileapp;

import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.rithms.riot.api.endpoints.match.dto.Match;
import net.rithms.riot.api.endpoints.match.dto.Participant;
import net.rithms.riot.api.endpoints.match.dto.ParticipantStats;
import net.rithms.riot.api.endpoints.static_data.dto.Champion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import uoit.csci4100u.mobileapp.tasks.ChampionIconTask;

import static uoit.csci4100u.mobileapp.Main.champions;
import static uoit.csci4100u.mobileapp.Main.mContext;
import static uoit.csci4100u.mobileapp.Main.recentMatches;

/**
 * Created by wesley on 13/12/17.
 */

public class MatchDetails extends AppCompatActivity {

    Map<String, Champion> champList;
    static protected final String BASE_DRAGON_URL = "http://ddragon.leagueoflegends.com/cdn/";
    int matchID;
    static MatchAdapter mAdapter;
    List<ImageView> champIcons = new ArrayList<>();
    List<String> playerScore = new ArrayList<>();
    ParticipantStats playerStats;
    final static String win = "win";
    final static String lose = "lose";
    ArrayAdapter<String> blueTeam;
    ArrayAdapter<String> redTeam;
    String[] blueTeamArray = new String [5];
    String[] redTeamArray = new String [5];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_details);

        Bundle extras = getIntent().getExtras();
        matchID = extras.getInt("matchID");
        Log.d("MatchDetailsFor", matchID+"");
        setupLayout();

        mAdapter = new MatchAdapter(mContext, recentMatches);

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
            int champId = x.getChampionId();
            //set this in case want to get more data from champ later
            Champion playedChamp;
            Log.d("champ:ID", champId + "");
            for (Champion y : c) {
                if (y.getId() == champId) {
                    playedChamp = y;
                    try {
                        champIcons.get(i).setImageBitmap(new ChampionIconTask().execute(playedChamp
                                .getName(), i + "").get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
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

            blueTeamArray[i] = player;
        }

        for (int j = 5; j < 10; j++)
        {
            playerStats = Integer.toString(CurrentMatch.getParticipants().get(j).getStats().getKills())
                    + "/"
                    + Integer.toString(CurrentMatch.getParticipants().get(j).getStats().getDeaths())
                    + "/"
                    +Integer.toString(CurrentMatch.getParticipants().get(j).getStats().getAssists());

            player = playerList[j] + " " + playerStats;

           redTeamArray[j-5] = player;
        }

        blueTeam = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,blueTeamArray);
        redTeam = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, redTeamArray);

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
        champIcons.add(champ0);
        champIcons.add(champ1);
        champIcons.add(champ2);
        champIcons.add(champ3);
        champIcons.add(champ4);
        champIcons.add(champ5);
        champIcons.add(champ6);
        champIcons.add(champ7);
        champIcons.add(champ8);
        champIcons.add(champ9);
    }

}



