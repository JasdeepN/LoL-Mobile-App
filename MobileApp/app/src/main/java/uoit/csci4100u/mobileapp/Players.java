package uoit.csci4100u.mobileapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import net.rithms.riot.api.endpoints.summoner.dto.Summoner;

import java.util.ArrayList;
import java.util.List;

import uoit.csci4100u.mobileapp.util.OnGetDataListener;

import static uoit.csci4100u.mobileapp.Main.dbHelper;

public class Players extends AppCompatActivity {

    List<Summoner> players;
    List<Boolean> statusi;
    ListView lv;
    int playerCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.players);
        lv = (ListView) findViewById(R.id.players_list);
        players = new ArrayList<>();
        statusi = new ArrayList<>();
        test();
    }

    private void test() {
        dbHelper.getAllUsers(new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot x : dataSnapshot.getChildren()) {
                    players.add((x.child("summoner")).getValue(Summoner.class));
                    statusi.add(x.child("can_play").getValue(Boolean.class));
                    Log.d("players:result:can_play", statusi.get(playerCount).toString());
                    Log.d("players:result:summoner", players.get(playerCount).getName());
                    playerCount++;
                }
                MyAdapter adapter = new MyAdapter(getApplicationContext(), players);
                // Attach the adapter to a ListView
                lv.setAdapter(adapter);
            }

            @Override
            public void onStart() {
                Log.d("players:getData", "Starting");
            }

            @Override
            public void onFailure() {

            }
        });
    }

    public class MyAdapter extends ArrayAdapter<Summoner>{

        public MyAdapter(@NonNull Context context, List<Summoner> p) {
            super(context, 0, p);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Summoner user = getItem(position);
            Boolean status = statusi.get(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.mylist, parent, false);
            }
            // Lookup view for data population
            TextView sumName = (TextView) convertView.findViewById(R.id.sum_name);
            TextView sumLevel = (TextView) convertView.findViewById(R.id.sum_level);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);

            // Populate the data into the template view using the data object

            sumName.setText(user.getName());
            sumLevel.setText(String.valueOf(user.getSummonerLevel()));

            if(status){
                icon.setImageDrawable(getDrawable(R.drawable.ic_check_black_24dp));
            } else {
                icon.setImageDrawable(getDrawable(R.drawable.ic_close_black_24dp));

            }
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
