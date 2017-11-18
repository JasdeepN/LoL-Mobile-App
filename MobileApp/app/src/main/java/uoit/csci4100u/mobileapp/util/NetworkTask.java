package uoit.csci4100u.mobileapp.util;

import android.os.AsyncTask;

import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

import java.util.concurrent.Callable;

/**
 * Created by jasdeep on 2017-11-06.
 *
 * Methods that are common across all NetworkTasks go here
 */

public abstract class NetworkTask<Void, result> extends AsyncTask<Void, Void, result> {
    static final private String API_KEY = "RGAPI-2972a243-6c42-4973-b070-5125645ec77e";
    static private ApiConfig config;
    static protected RiotApi riot_api;

    //TODO: return api reference method

    /**
     * Default constructor - sets up Api config and instantiates new RiotApi object
     */
    public NetworkTask(){
        //TODO: setup API here
        config = new ApiConfig().setKey(API_KEY);
        riot_api = new RiotApi(config);
    }

    /**
     * Method to return the RiotApi object to calling Class
     *
     * @return configured RiotApi object
     */
    public RiotApi getAPI(){
        if(riot_api == null){
            return null;
        } else {
            return riot_api;
        }
    }

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
