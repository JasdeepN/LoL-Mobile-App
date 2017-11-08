package uoit.csci4100u.mobileapp.util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.rithms.riot.api.endpoints.summoner.dto.Summoner;

/**
 * Created by jasdeep on 2017-11-07.
 *
 * Database helper Class, contains all methods relating to database use
 */

public class DatabaseHelperUtil {
    //reference to Firebase Realtime Database
    private DatabaseReference mDatabaseRef;

    public DatabaseHelperUtil(){
        DatabaseReference mDatabaseRoot = FirebaseDatabase.getInstance().getReference();
        mDatabaseRef = mDatabaseRoot.child("Summoners");
    }

    private void writeNewUser(String UID, Summoner newSumm) {
       mDatabaseRef.child(UID).setValue(newSumm);
    }

    /**
     * Public add user method
     *
     * @param UUID Unique user ID
     * @param newUser Summoner object
     */
    public void addUser(String UUID, Summoner newUser){
        writeNewUser(UUID, newUser);
    }

    //TODO: Implement reading from database on app start
    public void readFromDB(){

    }


}
