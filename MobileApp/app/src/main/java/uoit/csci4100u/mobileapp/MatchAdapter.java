package uoit.csci4100u.mobileapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.match.dto.Match;
import net.rithms.riot.api.endpoints.match.dto.MatchList;
import net.rithms.riot.api.endpoints.match.dto.MatchReference;
import net.rithms.riot.api.endpoints.match.dto.Participant;
import net.rithms.riot.api.endpoints.static_data.dto.Champion;
import net.rithms.riot.api.endpoints.static_data.dto.ChampionList;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import uoit.csci4100u.mobileapp.tasks.ChampionIconTask;
import uoit.csci4100u.mobileapp.util.NetworkTask;

import static uoit.csci4100u.mobileapp.Main.champions;
import static uoit.csci4100u.mobileapp.Main.current_version;
import static uoit.csci4100u.mobileapp.Main.locale;

/**
 * Created by jasdeep on 2017-12-12.
 */

public class MatchAdapter extends ArrayAdapter<Match> {
    Context context;
    Map<String, Champion> champList;


    public MatchAdapter(@NonNull Context context, @NonNull ArrayList<Match> objects) {
        super(context, 0, objects);
        this.context = context;
        champList = champions.getData();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        List<ImageView> champIcons = new ArrayList<>();

        // Get the data item for this position
        Match currMatch = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.match_list_item, parent, false);
        }

        // Get data from the match reference
        long duration = currMatch.getGameDuration();
        int map_id = currMatch.getMapId();
        String map = MapId(map_id);
        List<Participant> match_players = currMatch.getParticipants();

        // Lookup view for data population
        TextView gameTime = (TextView) convertView.findViewById(R.id.match_time);
        TextView gameMap = (TextView) convertView.findViewById(R.id.match_map);
        TextView winningTeam = (TextView) convertView.findViewById(R.id.txtWinningTeam);
        ImageView champ0 = (ImageView) convertView.findViewById(R.id.icon1);
        ImageView champ1 = (ImageView) convertView.findViewById(R.id.icon2);
        ImageView champ2 = (ImageView) convertView.findViewById(R.id.icon3);
        ImageView champ3 = (ImageView) convertView.findViewById(R.id.icon4);
        ImageView champ4 = (ImageView) convertView.findViewById(R.id.icon5);
        ImageView champ5 = (ImageView) convertView.findViewById(R.id.icon6);
        ImageView champ6 = (ImageView) convertView.findViewById(R.id.icon7);
        ImageView champ7 = (ImageView) convertView.findViewById(R.id.icon8);
        ImageView champ8 = (ImageView) convertView.findViewById(R.id.icon9);
        ImageView champ9 = (ImageView) convertView.findViewById(R.id.icon10);
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
        // Populate the data into the template view using the data object
        String format = context.getResources().getString(R.string.match_time);
        String timex = DateUtils.formatElapsedTime(duration);
        String time = String.format(format, timex);
        String teamThatWon = "";
        Boolean didBlueWin = currMatch.getParticipants().get(0).getStats().isWin();

        if(didBlueWin)
        {
            teamThatWon = "Winner: Blue";
        }

        else
        {
            teamThatWon = "Winner: Red";
        }

//        Log.d( "duration", time+"");
//        Log.d("time", timex);
        gameTime.setText(time);
        gameMap.setText(map);
        winningTeam.setText(teamThatWon);


        //use data dragon to fill these

//        Log.d("champ:list:values", champList.values()+"");
//        Log.d("champ:list:keys", champList.keySet()+"");
        Collection<Champion> c = champList.values();
        for (int i = 0; i < 10; i++) {
            Participant x = match_players.get(i);
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
                }
            }
        }
        //do other things with the champ

//            String champName = playedChamp.;
//            try {
//                champIcons.get(i).setImageBitmap(new ChampionIconTask().execute(champName).get());
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//            Log.d("map:", champMap.containsKey(champId)+"");
//            Log.d("champId =", champId+"");
//        }
//        champ0.setImageDrawable();

        return convertView;
    }

//    public static void setChampIcon(Bitmap bm, int icon_num) {
//        Log.d("champ:set icon", "set");
//        champIcons.get(icon_num).setImageBitmap(bm);
//    }

    //gets the constants for queue, queueid, and gameQueueConfigId fields
    private String MapId(int id) {
        switch (id) {
            case 1:
                return "Summoner's Rift (orignal)";
            case 2:
                return "Summoner's Rift (Autumn)";
            case 3:
                return "The Proving Grounds";
            case 4:
                return "Twisted Treeline (original)";
            case 8:
                return "The Crystal Scar";
            case 10:
                return "The Twisted Treeline";
            case 11:
                return "Summoner's Rift";
            case 12:
                return "Howling Abyss";
            case 14:
                return "Butcher's Bridge";
            case 16:
                return "Cosmic Ruins";
            case 18:
                return "Valoran City Park";
            case 19:
                return "Substructure 43";
            default:
                return "unknown map";

        }
    }

    public class MapBackground extends AsyncTask<Integer, Void, Bitmap> {
        static protected final String BASE_DRAGON_URL = "http://ddragon.leagueoflegends.com/cdn/";
        @Override
        protected Bitmap doInBackground(Integer... input) {
            Bitmap bm = null;
            try {
                //http://ddragon.leagueoflegends.com/cdn/6.8.1/img/map/map11.png
                String tempUrl = BASE_DRAGON_URL + current_version + "/img/map/map" + input[0] +
                        "" +
                        ".png";
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
            Log.d("map:end", "finished data dragon access");
        }

        @Override
        protected void onPreExecute() {
            Log.d("map:start", "starting data dragon access");
        }
    }
}
