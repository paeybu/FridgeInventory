package com.kabu.kabi.fridgeinventory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kabu.kabi.fridgeinventory.data.FridgeContract;

public class FridgeCursorAdapter extends CursorAdapter {

    public FridgeCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // TODO: Fill out this method and return the list item view (instead of null)

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // TODO: Fill out this method
        final int currentId = cursor.getInt(cursor.getColumnIndexOrThrow(FridgeContract.FridgeEntry._ID));
        final View myview = view;
        TextView nameTextView = view.findViewById(R.id.name);
        TextView summaryTextView = view.findViewById(R.id.summary);

        int nameColumnIndex = cursor.getColumnIndexOrThrow(FridgeContract.FridgeEntry.COLUMN_ITEM_NAME);
        int quantityColumnIndex = cursor.getColumnIndexOrThrow(FridgeContract.FridgeEntry.COLUMN_ITEM_QUANTITY);
        int unitColumnIndex = cursor.getColumnIndexOrThrow(FridgeContract.FridgeEntry.COLUMN_ITEM_UNIT);

        String name = cursor.getString(nameColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);
        int unit = cursor.getInt(unitColumnIndex);
        String stringUnit;
        switch (unit) {
            case FridgeContract.FridgeEntry.UNIT_PIECES:
                stringUnit = "Pieces";
                break;
            case FridgeContract.FridgeEntry.UNIT_BOTTLES:
                stringUnit = "Bottles";
                break;
            case FridgeContract.FridgeEntry.UNIT_KG:
                stringUnit = "Kg";
                break;
            case FridgeContract.FridgeEntry.UNIT_L:
                stringUnit = "L";
                break;
            default:
                stringUnit = "Pieces";
                break;
        }

        nameTextView.setText(name);
        summaryTextView.setText(quantity + " " + stringUnit);

        Button mDeleteBtn = view.findViewById(R.id.deleteBtn);
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.
                        setTitle("Are you sure you want to delete this item?").
                        setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String[] selectionArgs = new String[] { String.valueOf(currentId) };
                                context.getContentResolver().delete(FridgeContract.FridgeEntry.CONTENT_URI, FridgeContract.FridgeEntry._ID + "=?", selectionArgs);
                                Toast.makeText(context, "Delete row " + currentId, Toast.LENGTH_LONG).show();
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
        });
    }

}