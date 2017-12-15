package uoit.csci4100u.mobileapp.util;

import android.os.AsyncTask;
import android.util.Log;

import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

import java.util.concurrent.Callable;

/**
 * Created by jasdeep on 2017-11-06.
 * <p>
 * Methods that are common across all NetworkTasks go here
 */

public abstract class NetworkTask<Void, progress, result> extends AsyncTask<Void, Integer,
        result> {

    //permanent key
    static final private String API_KEY = "RGAPI-1d8e63b1-de1c-40b0-aa22-d19c1f86a7f3";
    // -- temp key replace this one --
//    static final private String API_KEY = "RGAPI-089296c3-3d3a-48b3-9ab1-0c0d6d3953d0";
    static protected final String BASE_DRAGON_URL = "http://ddragon.leagueoflegends.com/cdn/";

    static private ApiConfig config;
    static protected RiotApi riot_api;


    /**
     * Default constructor - sets up Api config and instantiates new RiotApi object
     */
    public NetworkTask() {
        //TODO: setup API here
        Log.d("new network task using", API_KEY);
        config = new ApiConfig().setKey(API_KEY);
        riot_api = new RiotApi(config);
    }

    /**
     * Method to return the RiotApi object to calling Class
     *
     * @return configured RiotApi object
     */
    public static RiotApi getAPI() {
        if (riot_api == null) {
            Log.d("RIOT API", "api error");
            return null;
        } else {
            Log.d("RIOT API", "api successfully returned");
            return riot_api;
        }
    }

    /**
     * Checks which platform was selected and returns the corresponding Platform object
     *
     * @param locale which region the Summoner is in
     * @return Platform object that matches the locale
     */
   static public Platform checkPlatform(String locale) {
        switch (locale) {
            case "BR":
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

    public static String getKey() {
        return API_KEY;
    }

    static public String getEndPoint(String locale) {
        switch (locale) {
            case "BR":
                return "br1.api.riotgames.com";
            case "EUNE":
                return "eun1.api.riotgames.com";
            case "EUW":
                return "euw1.api.riotgames.com";
            case "JP":
                return "jp1.api.riotgames.com";
            case "KR":
                return "kr.api.riotgames.com";
            case "LAN":
                return "la1.api.riotgames.com";
            case "LAS":
                return "la2.api.riotgames.com";
            case "NA":
                return "na1.api.riotgames.com";
            case "OCE":
                return "oc1.api.riotgames.com";
            case "TR":
                return "tr1.api.riotgames.com";
            case "PBE":
                return "pbe1.api.riotgames.com";
            default:
                return "ru.api.riotgames.com";
        }
    }


}
