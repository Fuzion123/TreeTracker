package com.apps.frederik.treetracker.Model.DataAccessLayer;

import android.content.Context;
import android.util.Log;
import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;
import com.apps.frederik.treetracker.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Frederik on 12/20/2017.
 */


public class FakeRepository {
    private Context _contest;

    public FakeRepository(Context con){
        _contest = con;
    }

    public List<MonitoredObject> GenerateFakeModel(){
        String jsonString = JSONReader(_contest);
        Gson gson = new GsonBuilder().create();
        Type listMonitoredObjects = new TypeToken<ArrayList<MonitoredObject>>(){}.getType(); // copied from https://stackoverflow.com/questions/5554217/google-gson-deserialize-listclass-object-generic-type
        List<MonitoredObject> objects = gson.fromJson(jsonString, listMonitoredObjects);
        return objects;
    }

    private String JSONReader(Context con){
        InputStream resourceReader = con.getResources().openRawResource(R.raw.fakedata);
        Writer writer = new StringWriter();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceReader, "UTF-8"));
            String line = reader.readLine();
            while (line != null) {
                writer.write(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            Log.e("PropertiesReading JSON ", "Unhandled exception while using JSONResourceReader", e);
        } finally {
            try {
                resourceReader.close();
            } catch (Exception e) {
                Log.e("reading JSON ", "Unhandled exception while using JSONResourceReader", e);
            }
        }
        return writer.toString();
    }
}
