package uoit.csci4100u.mobileapp;

import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import net.rithms.riot.api.endpoints.match.dto.Match;
import net.rithms.riot.api.endpoints.match.dto.Participant;
import net.rithms.riot.api.endpoints.match.dto.ParticipantStats;
import net.rithms.riot.api.endpoints.static_data.dto.Champion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
    ParticipantStats playerStats;
    final static String win = "win";
    final static String lose = "lose";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_details);
        //android.app.FragmentManager fragmentManager = getFragmentManager();
        //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        Bundle extras = getIntent().getExtras();
        matchID = extras.getInt("matchID");
        Log.d("MatchDetailsFor", matchID+"");
        setupLayout();

        mAdapter = new MatchAdapter(mContext, recentMatches);

    }

    private void setupLayout() {
        String gameMode;
        String gameType;
        String gameLength;

        Boolean didBlueWin;


        Match CurrentMatch;

        String player1, player1Stats;
        String player2, player2Stats;
        String player3, player3Stats;
        String player4, player4Stats;
        String player5, player5Stats;
        String player6, player6Stats;
        String player7, player7Stats;
        String player8, player8Stats;
        String player9, player9Stats;
        String player10, player10Stats;


        TextView tvGameType;
        TextView tvGameMode;
        TextView tvPlayer1, tvPlayer1Stats;
        TextView tvPlayer2, tvPlayer2Stats;
        TextView tvPlayer3, tvPlayer3Stats;
        TextView tvPlayer4, tvPlayer4Stats;
        TextView tvPlayer5, tvPlayer5Stats;
        TextView tvPlayer6, tvPlayer6Stats;
        TextView tvPlayer7, tvPlayer7Stats;
        TextView tvPlayer8, tvPlayer8Stats;
        TextView tvPlayer9, tvPlayer9Stats;
        TextView tvPlayer10, tvPlayer10Stats;
        TextView tvBlueWin, tvRedWin;

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

        List<Participant> players;
        champList = champions.getData();

        tvGameType = (TextView) findViewById(R.id.lblMatchDetailsTitle);
        tvGameMode = (TextView) findViewById(R.id.match_map);

        tvPlayer1 = (TextView) findViewById(R.id.txtPlayer1);
        tvPlayer1Stats = (TextView) findViewById(R.id.txtPlayer1Stats);

        tvPlayer2 = (TextView) findViewById(R.id.txtPlayer2);
        tvPlayer2Stats = (TextView) findViewById(R.id.txtPlayer2Stats);

        tvPlayer3 = (TextView) findViewById(R.id.txtPlayer3);
        tvPlayer3Stats = (TextView) findViewById(R.id.txtPlayer3Stats);

        tvPlayer4 = (TextView) findViewById(R.id.txtPlayer4);
        tvPlayer4Stats = (TextView) findViewById(R.id.txtPlayer4Stats);

        tvPlayer5 = (TextView) findViewById(R.id.txtPlayer5);
        tvPlayer5Stats = (TextView) findViewById(R.id.txtPlayer5Stats);

        tvPlayer6 = (TextView) findViewById(R.id.txtPlayer6);
        tvPlayer6Stats = (TextView) findViewById(R.id.txtPlayer6Stats);

        tvPlayer7 = (TextView) findViewById(R.id.txtPlayer7);
        tvPlayer7Stats = (TextView) findViewById(R.id.txtPlayer7Stats);

        tvPlayer8 = (TextView) findViewById(R.id.txtPlayer8);
        tvPlayer8Stats = (TextView) findViewById(R.id.txtPlayer8Stats);

        tvPlayer9 = (TextView) findViewById(R.id.txtPlayer9);
        tvPlayer9Stats = (TextView) findViewById(R.id.txtPlayer9Stats);

        tvPlayer10 = (TextView) findViewById(R.id.txtPlayer10);
        tvPlayer10Stats = (TextView) findViewById(R.id.txtPlayer10Stats);


       CurrentMatch = recentMatches.get(matchID);


        CurrentMatch.getGameMode();
        // Log.d("CURRENT TEAM: ", CurrentMatch.get);
        CurrentMatch.getGameType();
        CurrentMatch.getGameDuration();


        players = CurrentMatch.getParticipants();
        Log.d("current match", CurrentMatch.getGameId()+"");

        player1Stats = Integer.toString(CurrentMatch.getParticipants().get(0).getStats().getKills())
                       + "/"
                       + Integer.toString(CurrentMatch.getParticipants().get(0).getStats().getDeaths())
                       + "/"
                       +Integer.toString(CurrentMatch.getParticipants().get(0).getStats().getAssists());


        player2Stats = Integer.toString(CurrentMatch.getParticipants().get(1).getStats().getKills())
                + "/"
                + Integer.toString(CurrentMatch.getParticipants().get(1).getStats().getDeaths())
                + "/"
                +Integer.toString(CurrentMatch.getParticipants().get(1).getStats().getAssists());


        player3Stats = Integer.toString(CurrentMatch.getParticipants().get(2).getStats().getKills())
                + "/"
                + Integer.toString(CurrentMatch.getParticipants().get(2).getStats().getDeaths())
                + "/"
                +Integer.toString(CurrentMatch.getParticipants().get(2).getStats().getAssists());


        player4Stats = Integer.toString(CurrentMatch.getParticipants().get(3).getStats().getKills())
                + "/"
                + Integer.toString(CurrentMatch.getParticipants().get(3).getStats().getDeaths())
                + "/"
                +Integer.toString(CurrentMatch.getParticipants().get(3).getStats().getAssists());


        player5Stats = Integer.toString(CurrentMatch.getParticipants().get(4).getStats().getKills())
                + "/"
                + Integer.toString(CurrentMatch.getParticipants().get(4).getStats().getDeaths())
                + "/"
                +Integer.toString(CurrentMatch.getParticipants().get(4).getStats().getAssists());


        player6Stats = Integer.toString(CurrentMatch.getParticipants().get(5).getStats().getKills())
                + "/"
                + Integer.toString(CurrentMatch.getParticipants().get(5).getStats().getDeaths())
                + "/"
                +Integer.toString(CurrentMatch.getParticipants().get(5).getStats().getAssists());


        player7Stats = Integer.toString(CurrentMatch.getParticipants().get(6).getStats().getKills())
                + "/"
                + Integer.toString(CurrentMatch.getParticipants().get(6).getStats().getDeaths())
                + "/"
                +Integer.toString(CurrentMatch.getParticipants().get(6).getStats().getAssists());


        player8Stats = Integer.toString(CurrentMatch.getParticipants().get(7).getStats().getKills())
                + "/"
                + Integer.toString(CurrentMatch.getParticipants().get(7).getStats().getDeaths())
                + "/"
                +Integer.toString(CurrentMatch.getParticipants().get(7).getStats().getAssists());


        player9Stats = Integer.toString(CurrentMatch.getParticipants().get(8).getStats().getKills())
                + "/"
                + Integer.toString(CurrentMatch.getParticipants().get(8).getStats().getDeaths())
                + "/"
                +Integer.toString(CurrentMatch.getParticipants().get(8).getStats().getAssists());


        player10Stats = Integer.toString(CurrentMatch.getParticipants().get(9).getStats().getKills())
                + "/"
                + Integer.toString(CurrentMatch.getParticipants().get(9).getStats().getDeaths())
                + "/"
                +Integer.toString(CurrentMatch.getParticipants().get(9).getStats().getAssists());

        //Game mode == ARAM,5V5,3V3, PVE
        //Game type == ranked, normals, Draft
        Log.d("GAME MODE:", CurrentMatch.getGameMode());
        Log.d("GAME TYPE:", CurrentMatch.getGameType());

        gameMode = CurrentMatch.getGameMode();
        gameType = CurrentMatch.getGameType();

        tvBlueWin = (TextView) findViewById(R.id.txtBlueTeamWin);
        tvRedWin = (TextView) findViewById(R.id.txtRedTeamWin);

        didBlueWin = CurrentMatch.getParticipants().get(0).getStats().isWin();

        if (didBlueWin)
        {
            tvBlueWin.setText(win);
            tvRedWin.setText(lose);
        }

        else
            {
                tvBlueWin.setText(lose);
                tvRedWin.setText(win);
            }


        player1 = players.get(0).toString();
        player2 = players.get(1).toString();
        player3 = players.get(2).toString();
        player4 = players.get(3).toString();
        player5 = players.get(4).toString();
        player6 = players.get(5).toString();
        player7 = players.get(6).toString();
        player8 = players.get(7).toString();
        player9 = players.get(8).toString();
        player10 = players.get(9).toString();

        Log.d("PLAYER 1 STATS: ", player1);

        tvGameMode.setText(gameMode);
        tvGameType.setText(gameType);
        tvPlayer1.setText(player1);
        tvPlayer1Stats.setText(player1Stats);

        tvPlayer2.setText(player2);
        tvPlayer2Stats.setText(player2Stats);

        tvPlayer3.setText(player3);
        tvPlayer3Stats.setText(player3Stats);

        tvPlayer4.setText(player4);
        tvPlayer4Stats.setText(player4Stats);

        tvPlayer5.setText(player5);
        tvPlayer5Stats.setText(player5Stats);

        tvPlayer6.setText(player6);
        tvPlayer6Stats.setText(player6Stats);

        tvPlayer7.setText(player7);
        tvPlayer7Stats.setText(player7Stats);

        tvPlayer8.setText(player8);
        tvPlayer8Stats.setText(player8Stats);

        tvPlayer9.setText(player9);
        tvPlayer9Stats.setText(player9Stats);

        tvPlayer10.setText(player10);
        tvPlayer10Stats.setText(player10Stats);


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
                    try {
                        champIcons.get(i).setImageBitmap(new ChampionIconTask().execute(playedChamp
                                .getName(), i + "").get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    //new ChampionIconTask().execute(playedChamp.getName(), i + "");
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



