package uoit.csci4100u.mobileapp.tasks;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import uoit.csci4100u.mobileapp.Main;
import uoit.csci4100u.mobileapp.MatchAdapter;
import uoit.csci4100u.mobileapp.R;
import uoit.csci4100u.mobileapp.util.NetworkTask;

import static uoit.csci4100u.mobileapp.Main.current_version;
import static uoit.csci4100u.mobileapp.Main.mContext;

/**
 * Created by jasdeep on 2017-12-12.
 */

public class ChampionIconTask extends AsyncTask<String, Void, Bitmap> {
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
            Drawable myIcon = mContext.getDrawable( R.drawable
                    .ic_error_black_24dp );

            return getBitmap(myIcon);
        }
        return bm;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static Bitmap getBitmap(Drawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }


    @Override
    protected void onPostExecute(Bitmap result) {
        Log.d("champIcon:end", "finished data dragon access");
//        MatchAdapter.setChampIcon(result, i);
    }

    @Override
    protected void onPreExecute() {
        Log.d("champIcon:start", "starting data dragon access");
    }
}


