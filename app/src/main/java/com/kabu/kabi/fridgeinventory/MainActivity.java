package com.kabu.kabi.fridgeinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kabu.kabi.fridgeinventory.data.FridgeContract;
import com.kabu.kabi.fridgeinventory.data.FridgeDbHelper;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private TextView tv;
    private ListView mListView;

    private static final int FRIDGE_LOADER = 0;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    FridgeCursorAdapter mFridgeCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = findViewById(R.id.list);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), EditorActivity.class);
                startActivity(i);
            }
        });

        Cursor cursor = getContentResolver().query(FridgeContract.FridgeEntry.CONTENT_URI, null, null, null, null ,null);

        mFridgeCursorAdapter = new FridgeCursorAdapter(this, cursor);
        mListView.setAdapter(mFridgeCursorAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), EditorActivity.class);
                Uri currentUri = ContentUris.withAppendedId(FridgeContract.FridgeEntry.CONTENT_URI, id);
                i.setData(currentUri);
                startActivity(i);
            }
        });

        getLoaderManager().initLoader(FRIDGE_LOADER, null, this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_add_dummy:
                addDummy();
                break;
            case R.id.action_delete_all:
                deleteAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.
                setTitle("Are you sure you want to delete all items?").
                setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int rowsDeleted = getContentResolver().delete(FridgeContract.FridgeEntry.CONTENT_URI, null, null);
                        Toast.makeText(getApplicationContext(), "Rows deleted : " + rowsDeleted, Toast.LENGTH_LONG).show();
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

    private void addDummy() {

        ContentValues values = new ContentValues();
        values.put(FridgeContract.FridgeEntry.COLUMN_ITEM_NAME, "Milk");
        values.put(FridgeContract.FridgeEntry.COLUMN_ITEM_QUANTITY, 1);
        values.put(FridgeContract.FridgeEntry.COLUMN_ITEM_UNIT, FridgeContract.FridgeEntry.UNIT_BOTTLES);
        Uri uri = getContentResolver().insert(FridgeContract.FridgeEntry.CONTENT_URI, values);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                FridgeContract.FridgeEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFridgeCursorAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFridgeCursorAdapter.swapCursor(null);

    }
}
