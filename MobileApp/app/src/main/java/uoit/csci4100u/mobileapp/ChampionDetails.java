package uoit.csci4100u.mobileapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.static_data.dto.Champion;
import net.rithms.riot.api.endpoints.static_data.dto.ChampionSpell;
import net.rithms.riot.api.endpoints.static_data.dto.Image;
import net.rithms.riot.api.endpoints.static_data.dto.Skin;
import net.rithms.riot.api.endpoints.static_data.dto.Stats;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import uoit.csci4100u.mobileapp.util.NetworkTask;

import static uoit.csci4100u.mobileapp.Main.champions;
import static uoit.csci4100u.mobileapp.Main.current_version;
import static uoit.csci4100u.mobileapp.Main.locale;

public class ChampionDetails extends AppCompatActivity {
    Map<String, Champion> champMap;
    ImageView champPortrait, splash;
    TextView champName;
    TextView champInfo;
    int champID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.champion_details);
        Bundle extras = getIntent().getExtras();
        champID = extras.getInt("champID");
        Log.d("received", champID+"");
        champMap = champions.getData();
        Champion currentChamp= null;
        Collection<Champion> c = champMap.values();
//        Log.d("champs", c.toString()+"");
        for (Champion x : c){
            if (x.getId() == champID){
                currentChamp = champMap.get(x.getName());
                new ChampionSplash().execute(x.getName());
                List<Skin> skins = new ArrayList<>();
                skins = currentChamp.getSkins();
                Log.d("test", skins+"");
                break;
            }
        }
        champPortrait = (ImageView) this.findViewById(R.id.champicon);
        champName = (TextView) this.findViewById(R.id.champ_name);
        champInfo = (TextView) this.findViewById(R.id.blurb);
        splash = (ImageView) this.findViewById(R.id.splash);

        loadLayout(currentChamp);
    }

    public void loadLayout(Champion current) {
        //do things here!~!!!~~!
        new ChampionIcon().execute(current.getName());
        champName.setText(current.getName());
        champInfo.setText(current.getBlurb());
        //        SpellAdapter sAdapter = new
        // SpellAdapter(this, spells);
//        spells_lv.setAdapter(sAdapter);
        Stats champStats = current.getStats();
    }

    public class SpellAdapter extends ArrayAdapter<ChampionSpell> {
        public SpellAdapter(Context context, List<ChampionSpell> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            ChampionSpell spell = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.champ_spell, parent,
                        false);
            }
            // Lookup view for data population
            TextView spellName = (TextView) convertView.findViewById(R.id.spell_name);
            TextView spellDesc = (TextView) convertView.findViewById(R.id.spell_desc);
            ImageView spellIcon = (ImageView) convertView.findViewById(R.id.spell_icon);
            // Populate the data into the template view using the data object
            spellName.setText(spell.getName());
            spellDesc.setText(spell.getDescription());
            // Return the completed view to render on screen
            return convertView;
        }
    }

    public void setChampIcon(Bitmap bm) {
        champPortrait.setImageBitmap(bm);
    }

    public void setChampSplash(Bitmap bm) {
        splash.setImageBitmap(bm);
    }

    public class ChampionIcon extends AsyncTask<String, Void, Bitmap> {
        // http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/Aatrox.png
        static protected final String BASE_DRAGON_URL = "http://ddragon.leagueoflegends.com/cdn/";

        @Override
        protected Bitmap doInBackground(String... input) {
            Bitmap bm = null;
            try {
                String champNameFormated = "";
                champNameFormated = input[0].replace(" ", "");
//            champNameFormated = champNameFormated.replace("'", "").substring(1).toLowerCase();
                String tempUrl = BASE_DRAGON_URL + current_version + "/img/champion/" +
                        champNameFormated + ".png";
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
            setChampIcon(result);
        }

        @Override
        protected void onPreExecute() {
            Log.d("champIcon:start", "starting data dragon access");
        }
    }


    public class ChampionSplash extends AsyncTask<String, Void, Bitmap> {
        // http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/Aatrox.png
        static protected final String BASE_DRAGON_URL = "http://ddragon.leagueoflegends.com/cdn/";

        @Override
        protected Bitmap doInBackground(String... input) {
            Bitmap bm = null;
            try {
                String champNameFormated = "";
                champNameFormated = input[0].replace(" ", "");
//            champNameFormated = champNameFormated.replace("'", "").substring(1).toLowerCase();
                String tempUrl = BASE_DRAGON_URL + "img/champion/splash/" + champNameFormated + "_0.jpg";
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
            setChampSplash(result);
        }

        @Override
        protected void onPreExecute() {
            Log.d("champIcon:start", "starting data dragon access");
        }
    }


}
