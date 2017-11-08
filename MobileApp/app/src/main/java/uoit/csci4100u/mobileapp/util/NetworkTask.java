package uoit.csci4100u.mobileapp.util;

import android.os.AsyncTask;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

/**
 * Created by jasdeep on 2017-11-06.
 *
 * Methods that are common across all NetworkTasks go here
 */

public abstract class NetworkTask extends AsyncTask<String, Void, Summoner> {
    /**
     * Checks which platform was selected and returns the corresponding Platform object
     *
     * @param locale which region the Summoner is in
     * @return Platform object that matches the locale
     */
    protected Platform checkPlatform(String locale) {
        switch (locale){
            case "BR" :
                return Platform.BR;
            case "EUNE":
                return Platform.EUNE;
            case "EUW":
                return Platform.EUW;
            case "JP":
                return Platform.JP;
            case "KR":
                return Platform.KR;
            case "LAN":
                return Platform.LAN;
            case "LAS":
                return Platform.LAS;
            case "NA":
                return Platform.NA;
            case "OCE":
                return Platform.OCE;
            case "TR":
                return Platform.TR;
            default:
                return Platform.RU;
        }
    }
}
