package uoit.csci4100u.mobileapp.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import uoit.csci4100u.mobileapp.Main;
import uoit.csci4100u.mobileapp.util.NetworkTask;

import static uoit.csci4100u.mobileapp.Main.current_version;

/**
 * Created by jasdeep on 2017-12-12.
 */

public class ProfileIconTask extends AsyncTask<String, Void, Bitmap> {
    static protected final String BASE_DRAGON_URL = "http://ddragon.leagueoflegends.com/cdn/";

    @Override
    protected Bitmap doInBackground(String... input) {
        Bitmap bm = null;
        try {

            String tempUrl = BASE_DRAGON_URL + current_version + "/img/profileicon/" + input[0] + ".png";
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
        Log.d("ProfildIcon:end", "finished data dragon access");
        Main.setIcon(result);
    }

    @Override
    protected void onPreExecute() {
        Log.d("ProfileIcon:start", "starting data dragon access");
    }
}


