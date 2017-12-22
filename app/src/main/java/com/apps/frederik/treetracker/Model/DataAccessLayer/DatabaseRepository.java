package com.apps.frederik.treetracker.Model.DataAccessLayer;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by frede on 22/12/2017.
 */

public class DatabaseRepository {
    public DatabaseReference _dbRef;


    public DatabaseRepository(String relativePath){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        _dbRef = database.getReference(relativePath);
    }

    public void SetValue(String val){
        _dbRef.setValue(val);
    }
}
