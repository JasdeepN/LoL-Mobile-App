package uoit.csci4100u.mobileapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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

import static uoit.csci4100u.mobileapp.Main.champions;
import static uoit.csci4100u.mobileapp.Main.mContext;
import static uoit.csci4100u.mobileapp.Main.recentMatches;

/**
 * Created by wesley on 15/12/17.
 */

public class PlayerMatchDetails extends AppCompatActivity {
    int matchID;
    int patID;
    static MatchAdapter mAdapter;
    Map<String, Champion> champList;
    ImageView champIcon;
    List<Participant> players;
    List<Participant> addPlayers = new ArrayList<>();
    TextView champName;
    TextView KDA;
    String GoldEarned;
    String MinionsKilled;
    String TotalDamageDealt;
    String PhysicalDamageDealt;
    String MagicDamageDealt;
    String TowersDestroyed;
    String name, kda;
    ArrayAdapter<String> Info;
    ArrayList<String> populateView = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_match_details);

        Bundle extras = getIntent().getExtras();
        matchID = extras.getInt("MatchID");
        patID = extras.getInt("ParticipantID");
        Log.d("participantID", patID+"");
        getPlayerDetails();



    }

    private void getPlayerDetails()
    {
        Match matchResults = null;

        champList = champions.getData();

        addPlayers.addAll(MatchDetails.redParticiant);
        addPlayers.addAll(MatchDetails.blueParticiant);



            GoldEarned = "Gold Earned: " + Integer.toString(addPlayers.get(patID-1).getStats().getGoldEarned());
            MinionsKilled = "Minions Killed: " + Integer.toString(addPlayers.get(patID-1).getStats().getTotalMinionsKilled());
            TotalDamageDealt = "Total Damage Dealt to Champions: " + Long.toString(addPlayers.get(patID-1).getStats().getTotalDamageDealtToChampions());
            PhysicalDamageDealt = "Physical Damage Dealt to Champions: " + Long.toString(addPlayers.get(patID-1).getStats().getPhysicalDamageDealtToChampions());
            MagicDamageDealt = "Magical Damage Dealt to Champions: " + Long.toString(addPlayers.get(patID-1).getStats().getMagicDamageDealtToChampions());
            TowersDestroyed = "Towers Destroyed: " + Integer.toString(addPlayers.get(patID-1).getStats().getTurretKills());

            populateView.add(GoldEarned);
            populateView.add(MinionsKilled);
            populateView.add(TotalDamageDealt);
            populateView.add(PhysicalDamageDealt);
            populateView.add(MagicDamageDealt);
            populateView.add(TowersDestroyed);

            champIcon = (ImageView) findViewById(R.id.imgChampIcon);
            champIcon.setImageBitmap(MatchDetails.bitmapArray[patID-1]);


        Info = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,populateView);

        ListView playerInforListView = (ListView) findViewById(R.id.lvDescription);
        playerInforListView.setAdapter(Info);

        name = MatchDetails.name.get(patID-1);
        kda = MatchDetails.KDA.get(patID-1);

        champName = (TextView) findViewById(R.id.txtChampName);
        String kda_format = getResources().getString(R.string.kdaFormat);
        String displayKDA = String.format(kda_format, kda);

        KDA = (TextView) findViewById(R.id.txtKDA);

        champName.setText(name);
        KDA.setText(displayKDA);

    }

}
