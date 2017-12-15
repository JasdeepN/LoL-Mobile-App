package uoit.csci4100u.mobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import net.rithms.riot.api.endpoints.match.dto.Match;
import net.rithms.riot.api.endpoints.match.dto.Participant;
import net.rithms.riot.api.endpoints.static_data.dto.Champion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import uoit.csci4100u.mobileapp.tasks.ChampionIconTask;
import uoit.csci4100u.mobileapp.tasks.MatchInfo;

import static uoit.csci4100u.mobileapp.Main.champions;
import static uoit.csci4100u.mobileapp.Main.recentMatches;

/**
 * Created by wesley on 13/12/17.
 */

public class MatchDetails extends AppCompatActivity {

    Map<String, Champion> champList;
    int matchID;

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
        String blueTeam;
        String redTeam;
        String gameLength;

        Match CurrentMatch;

        String player1;
        String player2;
        String player3;
        String player4;
        String player5;
        String player6;
        String player7;
        String player8;
        String player9;
        String player10;

        TextView tvPlayer1;
        TextView tvPlayer2;
        TextView tvPlayer3;
        TextView tvPlayer4;
        TextView tvPlayer5;
        TextView tvPlayer6;
        TextView tvPlayer7;
        TextView tvPlayer8;
        TextView tvPlayer9;
        TextView tvPlayer10;
        List<Participant> players;
        List<String> teams;
        players = new ArrayList<>();
        teams = new ArrayList<>();
        champList = champions.getData();

        tvPlayer1 = (TextView) findViewById(R.id.txtPlayer1);
        tvPlayer2 = (TextView) findViewById(R.id.txtPlayer2);
        tvPlayer3 = (TextView) findViewById(R.id.txtPlayer3);
        tvPlayer4 = (TextView) findViewById(R.id.txtPlayer4);
        tvPlayer5 = (TextView) findViewById(R.id.txtPlayer5);
        tvPlayer6 = (TextView) findViewById(R.id.txtPlayer6);
        tvPlayer7 = (TextView) findViewById(R.id.txtPlayer7);
        tvPlayer8 = (TextView) findViewById(R.id.txtPlayer8);
        tvPlayer9 = (TextView) findViewById(R.id.txtPlayer9);
        tvPlayer10 = (TextView) findViewById(R.id.txtPlayer10);


       CurrentMatch = recentMatches.get(matchID);


        CurrentMatch.getGameMode();
        // Log.d("CURRENT TEAM: ", CurrentMatch.get);
        CurrentMatch.getGameType();
        CurrentMatch.getGameDuration();

        players = CurrentMatch.getParticipants();
        Log.d("current match", CurrentMatch.getGameId()+"");


        //Game mode == ARAM,5V5,3V3, PVE
        //Game type == ranked, normals, Draft
        Log.d("GAME MODE:", CurrentMatch.getGameMode());
        Log.d("GAME TYPE:", CurrentMatch.getGameType());


        int player1ID = players.get(0).getChampionId();
        player1 = Integer.toString(player1ID);
        // player1 = players.get(0).getChampionId();
        player2 = players.get(1).toString();
        player3 = players.get(2).toString();
        player4 = players.get(3).toString();
        player5 = players.get(4).toString();
        player6 = players.get(5).toString();
        player7 = players.get(6).toString();
        player8 = players.get(7).toString();
        player9 = players.get(8).toString();
        player10 = players.get(9).toString();

        tvPlayer1.setText(player1);
        tvPlayer2.setText(player2);
        tvPlayer3.setText(player3);
        tvPlayer4.setText(player4);
        tvPlayer5.setText(player5);
        tvPlayer6.setText(player6);
        tvPlayer7.setText(player7);
        tvPlayer8.setText(player8);
        tvPlayer9.setText(player9);
        tvPlayer10.setText(player10);


        String[] playerList = new String[10];
        Collection<Champion> c = champList.values();
        for (int i = 0; i < 10; i++) {
            Participant x = players.get(i);
            int champId = x.getChampionId();
            //set this in case want to get more data from champ later
            Champion playedChamp;
            Log.d("champ:ID", champId + "");
            for (Champion y : c) {
                if (y.getId() == champId) {
                    playedChamp = y;
                    new ChampionIconTask().execute(playedChamp.getName(), i + "");
                    playerList[i] = playedChamp.getName();

                }
            }
        }

        tvPlayer1.setText(playerList[0]);
        tvPlayer2.setText(playerList[1]);
        tvPlayer3.setText(playerList[2]);
        tvPlayer4.setText(playerList[3]);
        tvPlayer5.setText(playerList[4]);
        tvPlayer6.setText(playerList[5]);
        tvPlayer7.setText(playerList[6]);
        tvPlayer8.setText(playerList[7]);
        tvPlayer9.setText(playerList[8]);
        tvPlayer10.setText(playerList[9]);
    }
}
