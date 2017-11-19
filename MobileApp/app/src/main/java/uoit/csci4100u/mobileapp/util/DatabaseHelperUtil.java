package uoit.csci4100u.mobileapp.util;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.rithms.riot.api.endpoints.summoner.dto.Summoner;

import java.util.Date;

/**
 * Created by jasdeep on 2017-11-07.
 *
 * Database helper Class, contains all methods relating to database use
 */

public class DatabaseHelperUtil implements DatabaseReference.CompletionListener{
    //reference to Firebase Realtime Database
    private DatabaseReference mDatabaseRoot;
    private DatabaseReference mDatabaseRef;
    static final String TAG = "DatabaseUtil.java";
    private String mUUID;

    /**
     * Default Constructor - Gets reference to database and sets it globally
     */
    public DatabaseHelperUtil() {
        mDatabaseRoot= FirebaseDatabase.getInstance().getReference();
        mDatabaseRef = mDatabaseRoot.child("summoners");
    }

    public void setUUID(String UUID){
        this.mUUID = UUID;
    }

    /**
     * Private method for database writes
     *
     * @param newSumm Summoner object to attach to account
     */
    private void writeNewUser(Summoner newSumm, String region, String version) {
        mDatabaseRef.child(mUUID).setValue(newSumm);
        //sets the default search value to false
        mDatabaseRef.child(mUUID).child("can_play").setValue(false);
        mDatabaseRef.child(mUUID).child("region").setValue(region);
        mDatabaseRef.child(mUUID).child("last_update").setValue(System.currentTimeMillis());
        mDatabaseRef.child(mUUID).child("version").setValue(version);
    }

    /**
     * Public add user method
     *
     * @param newUser    Unique user ID
     * @param newUser Summoner object
     */
    public void addUser(Summoner newUser, String region, String version) {
        writeNewUser(newUser, region, version);
    }

    public void updateUser(Summoner user, String version) {
        updateDBuser(user, version);
    }

    private void updateDBuser(Summoner user, String version) {
        mDatabaseRef.child(mUUID).setValue(user);
        //sets the default search value to false
        mDatabaseRef.child(mUUID).child("last_update").setValue(System.currentTimeMillis());
        mDatabaseRef.child(mUUID).child("version").setValue(version);
    }

    public DatabaseReference getUserRef(String UUID){
        return mDatabaseRef.child(UUID);
    }

    /**
     * Override default onComplete() to write some logs
     *
     * @param databaseError Database error if an error occurred
     * @param databaseReference Reference object to Firebase
     */
    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError != null) {
            Log.d(TAG, databaseError.getMessage());
        } else {
            Log.d(TAG, "Data saved successfully.");
        }
    }

    /**
     * Reads data from Firebase
     *
     * @param listener listener object that is listening for database changes
     */
    public void readDataSummoner(String UUID, final OnGetDataListener listener) {

        listener.onStart();
            mDatabaseRef.child(UUID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listener.onSuccess(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    listener.onFailure();
                }
            });

    }

    public DatabaseReference getCurrentStatus(){
        return mDatabaseRef.child(mUUID).child("can_play");
    }


    //TODO: toggle method
    public Boolean togglePlay(Boolean current_setting){
            mDatabaseRef.child(mUUID).child("can_play").setValue(!current_setting);
            return !current_setting;
    }

    //TODO: delete user from database method
}
