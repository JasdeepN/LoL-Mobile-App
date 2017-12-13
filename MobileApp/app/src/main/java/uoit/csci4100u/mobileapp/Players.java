package uoit.csci4100u.mobileapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;

import net.rithms.riot.api.endpoints.summoner.dto.Summoner;

import java.util.ArrayList;
import java.util.List;

import uoit.csci4100u.mobileapp.util.OnGetDataListener;

import static uoit.csci4100u.mobileapp.Main.dbHelper;

public class Players extends AppCompatActivity {

    List<Summoner> players;
    List<Boolean> statusi;
    ToggleButton onlineOnly;
    ListView lv;
    MyAdapter adapter;
    //show online players only toggle boolean
    boolean on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.players);
        setResult(Main.SUCCESS);
        lv = (ListView) findViewById(R.id.players_list);


        onlineOnly = (ToggleButton) findViewById(R.id.online_only);

        onlineOnly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Log.d("Toggle", "ON");
                    on = true;

                } else {
                    // The toggle is disabled
                    Log.d("Toggle", "OFF");
                    on = false;
                }
            }
        });
        onlineOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.clear();
                load();
            }
        });
        players = new ArrayList<>();
        statusi = new ArrayList<>();
        load();
    }

    private void load() {
        players.clear();
        statusi.clear();

        dbHelper.getAllUsers(new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot x : dataSnapshot.getChildren()) {
                    //add only online users
                    if (on) {
                        boolean test = x.child("can_play").getValue(Boolean.class);
                        if(test) {
                            players.add((x.child("summoner")).getValue(Summoner.class));
                            statusi.add(test);
                        }
                    } else {
                        players.add((x.child("summoner")).getValue(Summoner.class));
                        statusi.add(x.child("can_play").getValue(Boolean.class));
                    }
                }
                adapter = new MyAdapter(getApplicationContext(), players);
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
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.player_list_item, parent, false);
            }
            // Lookup view for data population
            TextView sumName = (TextView) convertView.findViewById(R.id.sum_name);
            TextView sumLevel = (TextView) convertView.findViewById(R.id.sum_level);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);

            // Populate the data into the template view using the data object

            sumName.setText(user.getName());

            String format = getResources().getString(R.string.level_lbl);
            String level = String.format(format, String.valueOf(user.getSummonerLevel()));
            sumLevel.setText(level);

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
