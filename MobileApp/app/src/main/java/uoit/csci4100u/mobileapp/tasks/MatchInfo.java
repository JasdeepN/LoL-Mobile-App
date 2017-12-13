package uoit.csci4100u.mobileapp.tasks;

import android.util.Log;

import uoit.csci4100u.mobileapp.Main;
import uoit.csci4100u.mobileapp.util.NetworkTask;

import net.rithms.riot.api.endpoints.match.dto.Match;
import net.rithms.riot.api.endpoints.match.dto.MatchList;
import net.rithms.riot.api.endpoints.match.dto.MatchReference;

import java.util.List;

import static uoit.csci4100u.mobileapp.Main.locale;

/**
 * Created by jasdeep on 2017-12-09.
 *
 * gets single match data
 */

public class MatchInfo extends NetworkTask<Long, Void, Match> {
    Match current;
    @Override
    protected Match doInBackground(Long... longs) {
        try{
            current = riot_api.getMatch(locale, longs[0]);
        }catch(net.rithms.riot.api.RiotApiException re){
            re.printStackTrace();
        }
        return current;
    }
    @Override
    protected void onPostExecute(Match result) {
        Log.d("matchInfo:end", "finished getting match info");
        Log.d("Results", result.toString());
        Main.addMatch(result);
    }


    @Override
    protected void onPreExecute() {
        Log.d("matchInfo:start", "starting getting match info");
    }
}

