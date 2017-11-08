package uoit.csci4100u.mobileapp.util;

import net.rithms.riot.api.endpoints.summoner.dto.Summoner;

/**
 * Created by jasdeep on 2017-11-07.
 *
 * TODO: DETERMINE IF THIS IS NECESSARY
 */

public class ExtendedSummoner extends Summoner {

    /**
     * Returns String object containing the current Summoner ID
     *
     * @param curr Summoner that we need the ID of
     * @return String containing the Summoner ID
     */
    public static String getAccountIdStr(Summoner curr){
        return Long.toString(curr.getAccountId());
    }

}
