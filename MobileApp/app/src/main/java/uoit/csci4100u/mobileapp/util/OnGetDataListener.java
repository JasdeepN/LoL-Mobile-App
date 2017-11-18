package uoit.csci4100u.mobileapp.util;

/**
 * Created by jasdeep on 2017-11-09.
 *
 * Interface for getDataListener
 */

import com.google.firebase.database.DataSnapshot;

public interface OnGetDataListener {
    //this is for callbacks
    void onSuccess(DataSnapshot dataSnapshot);

    void onStart();

    void onFailure();
}

