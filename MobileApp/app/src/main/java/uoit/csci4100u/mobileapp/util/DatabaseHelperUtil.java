package uoit.csci4100u.mobileapp.util;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.rithms.riot.api.endpoints.summoner.dto.Summoner;

import java.util.HashMap;

import uoit.csci4100u.mobileapp.Main;

/**
 * Created by jasdeep on 2017-11-07.
 * <p>
 * Database helper Class, contains all methods relating to database use
 */

public class DatabaseHelperUtil implements DatabaseReference.CompletionListener {
    //reference to Firebase Realtime Database
    private DatabaseReference mDatabaseRef;
    static final String TAG = "DatabaseUtil.java";

    public DatabaseHelperUtil() {
        DatabaseReference mDatabaseRoot = FirebaseDatabase.getInstance().getReference();
        mDatabaseRef = mDatabaseRoot.child("Summoners");
    }

    private void writeNewUser(String UID, Summoner newSumm) {
        mDatabaseRef.child(UID).setValue(newSumm);
    }

    /**
     * Public add user method
     *
     * @param UUID    Unique user ID
     * @param newUser Summoner object
     */
    public void addUser(String UUID, Summoner newUser) {
        writeNewUser(UUID, newUser);
    }


    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError != null) {
            Log.d(TAG, databaseError.getMessage());
        } else {
            Log.d(TAG, "Data saved successfully.");
        }
    }

    public interface OnGetDataListener {
        //this is for callbacks
        void onSuccess(DataSnapshot dataSnapshot);

        void onStart();

        void onFailure();
    }

    public void readData(final OnGetDataListener listener) {
        listener.onStart();
            mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

}
