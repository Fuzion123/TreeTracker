package com.apps.frederik.treetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCaptureActivity;
import com.google.android.gms.vision.barcode.Barcode;

public class AddSensorActivity extends AppCompatActivity {

    private static final int RC_BARCODE_CAPTURE = 1234;
    private static final String TAG = "OverviewActivity";

    private Button buttonAddSensor;
    private EditText editTextSensorName;
    private TextView textViewInformationToUser;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sensor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonAddSensor = findViewById(R.id.buttonAddSensor);
        editTextSensorName = findViewById(R.id.editTextSensorName);
        textViewInformationToUser = findViewById(R.id.textViewInformationToUser);
        fab = findViewById(R.id.fab);

        //Setup state of buttons and text field
        fab.setVisibility(View.VISIBLE);
        buttonAddSensor.setVisibility(View.INVISIBLE);
        editTextSensorName.setVisibility(View.INVISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddSensorActivity.this, BarcodeCaptureActivity.class);
                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });

        buttonAddSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use helper class to add sensor to Firebase

                //If succesfully added to Firebase, return to OverviewActivity
                //TODO: Add a proper (null?) check
                if (invalidSensorName())
                    Toast.makeText(AddSensorActivity.this, "Enter a name, please", Toast.LENGTH_LONG).show();
                else
                {
                    finish();
                }
            }
        });
    }

    private boolean invalidSensorName() {
        String Name = editTextSensorName.getText().toString();
        return Name.isEmpty() && Name.matches("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    textViewInformationToUser.setText(R.string.text_give_sensor_a_name);
                    buttonAddSensor.setVisibility(View.VISIBLE);
                    editTextSensorName.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);

                } else {
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
