package com.kabu.kabi.fridgeinventory;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.kabu.kabi.fridgeinventory.data.FridgeContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Button mButton;
    private EditText mNameEt, mQuantityEt;
    private Spinner mSpinner;
    private int mUnit;
    private Uri mCurrentUri;

    private static final int FRIDGE_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent i = getIntent();
        mCurrentUri = i.getData();

        //New Item
        if (isNewItem()) {
            setTitle(getString(R.string.editor_activity_title_new_item));
            invalidateOptionsMenu();
            // Edit Item
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_item));
            getLoaderManager().initLoader(FRIDGE_LOADER, null, this);
        }

        mButton = findViewById(R.id.submitBtn);
        mNameEt = findViewById(R.id.nameEt);
        mQuantityEt = findViewById(R.id.quantityEt);
        mSpinner = findViewById(R.id.spinner);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePet();
            }
        });

        setupSpinner();
    }

    private void savePet() {

        String nameString = mNameEt.getText().toString().trim();
        String quantityString = mQuantityEt.getText().toString().trim();

        //New item
        if (isNewItem()) {
            if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(quantityString)) {
                Toast.makeText(getApplicationContext(), "You must input name and quantity", Toast.LENGTH_LONG).show();
                return;
            }
        }

        ContentValues values = new ContentValues();
        values.put(FridgeContract.FridgeEntry.COLUMN_ITEM_NAME, mNameEt.getText().toString());
        values.put(FridgeContract.FridgeEntry.COLUMN_ITEM_QUANTITY, Integer.parseInt(mQuantityEt.getText().toString()));
        values.put(FridgeContract.FridgeEntry.COLUMN_ITEM_UNIT, mUnit);

        //New
        if (isNewItem()) {
            Uri insertUri = getContentResolver().insert(FridgeContract.FridgeEntry.CONTENT_URI, values);

            if(insertUri == null) {
                Toast.makeText(getApplicationContext(), "Failed to save item", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Item saved", Toast.LENGTH_LONG).show();
            }
        // Update
        } else {
            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);
            // No rows affected =failed
            if (rowsAffected == 0) {
                Toast.makeText(getApplicationContext(), "Failed to update item", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Item updated", Toast.LENGTH_LONG).show();
            }
        }


        finish();
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
                    } else if (selection.equals(getString(R.string.unit_kg))) {
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

    private boolean isNewItem() {
        return mCurrentUri == null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isNewItem()) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_item);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete_item) {
            deleteItem();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.
                setTitle("Are you sure you want to delete this item?").
                setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);
                        if (rowsDeleted == 0) {
                            Toast.makeText(getApplicationContext(), "Fail to delete item", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Item deleted", Toast.LENGTH_LONG).show();
                        }
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).
                create().show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                mCurrentUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColIndex = cursor.getColumnIndex(FridgeContract.FridgeEntry.COLUMN_ITEM_NAME);
            int quantityColIndex = cursor.getColumnIndex(FridgeContract.FridgeEntry.COLUMN_ITEM_QUANTITY);
            int unitColIndex = cursor.getColumnIndex(FridgeContract.FridgeEntry.COLUMN_ITEM_UNIT);

            String itemName = cursor.getString(nameColIndex);
            int quantity = cursor.getInt(quantityColIndex);
            int unit = cursor.getInt(unitColIndex);

            mNameEt.setText(itemName);
            mQuantityEt.setText(Integer.toString(quantity));

            switch (unit) {
                case FridgeContract.FridgeEntry.UNIT_PIECES:
                    mSpinner.setSelection(FridgeContract.FridgeEntry.UNIT_PIECES);
                    break;
                case FridgeContract.FridgeEntry.UNIT_BOTTLES:
                    mSpinner.setSelection(FridgeContract.FridgeEntry.UNIT_BOTTLES);
                    break;
                case FridgeContract.FridgeEntry.UNIT_KG:
                    mSpinner.setSelection(FridgeContract.FridgeEntry.UNIT_KG);
                    break;
                default:
                    mSpinner.setSelection(FridgeContract.FridgeEntry.UNIT_L);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEt.setText("");
        mQuantityEt.setText("");
        mSpinner.setSelection(0);
    }
}
