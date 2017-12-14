package uoit.csci4100u.mobileapp.tasks;

import android.util.Log;

import net.rithms.riot.api.endpoints.match.dto.Match;
import net.rithms.riot.api.endpoints.match.dto.MatchList;
import net.rithms.riot.api.endpoints.match.dto.MatchReference;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;

import java.util.List;

import uoit.csci4100u.mobileapp.Main;
import uoit.csci4100u.mobileapp.util.NetworkTask;

import static uoit.csci4100u.mobileapp.Main.locale;


/**
 * Created by jasdeep on 2017-12-09.
 */

public class GetMatches extends NetworkTask<Summoner, Integer, MatchList> {

    MatchList result;
    @Override
    protected MatchList doInBackground(Summoner... user) {
        try {
            Log.d("looking for", locale + " " + user[0].getAccountId());
            result = riot_api.getRecentMatchListByAccountId(locale, user[0].getAccountId());
        } catch (net.rithms.riot.api.RiotApiException re) {
            Log.d("URL ERROR", re + "");
            return null;
        }
        return result;
    }

    @Override
    protected void onPostExecute(MatchList result) {
        Log.d("matches:end", "finished getting matches");
//            displayMatches(result);
//        Log.d("matches", result.getMatches()+"");
        List<MatchReference> matches = result.getMatches();
        Main.setMatchList(matches);
    }


    @Override
    protected void onPreExecute() {
        Log.d("getMatches:start", "starting getting matches");
    }
}
