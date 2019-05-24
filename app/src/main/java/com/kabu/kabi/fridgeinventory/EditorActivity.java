package com.kabu.kabi.fridgeinventory;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.kabu.kabi.fridgeinventory.data.FridgeContract;

public class EditorActivity extends AppCompatActivity {

    private Button mButton;
    private EditText mNameEt, mQuantityEt;
    private Spinner mSpinner;
    private int mUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent i = getIntent();
        Uri currentUri = i.getData();

        if(currentUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_item));
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_item));
        }

        mButton = findViewById(R.id.submitBtn);
        mNameEt = findViewById(R.id.nameEt);
        mQuantityEt = findViewById(R.id.quantityEt);
        mSpinner = findViewById(R.id.spinner);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(FridgeContract.FridgeEntry.COLUMN_ITEM_NAME, mNameEt.getText().toString());
                values.put(FridgeContract.FridgeEntry.COLUMN_ITEM_QUANTITY, Integer.parseInt(mQuantityEt.getText().toString()));
                values.put(FridgeContract.FridgeEntry.COLUMN_ITEM_UNIT, mUnit);
                Uri uri = getContentResolver().insert(FridgeContract.FridgeEntry.CONTENT_URI, values);
                finish();
            }
        });

        setupSpinner();
    }

    private void setupSpinner() {
        ArrayAdapter unitSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_unit_options, android.R.layout.simple_spinner_item);

        unitSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mSpinner.setAdapter(unitSpinnerAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.unit_pcs))) {
                        mUnit = FridgeContract.FridgeEntry.UNIT_PIECES;
                    } else if (selection.equals(getString(R.string.unit_bottles))) {
                        mUnit = FridgeContract.FridgeEntry.UNIT_BOTTLES;
                    } else if (selection.equals(getString(R.string.unit_kg))){
                        mUnit = FridgeContract.FridgeEntry.UNIT_KG;
                    } else {
                        mUnit = FridgeContract.FridgeEntry.UNIT_L;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mUnit = FridgeContract.FridgeEntry.UNIT_PIECES; // Unknown
            }
        });
    }
}
