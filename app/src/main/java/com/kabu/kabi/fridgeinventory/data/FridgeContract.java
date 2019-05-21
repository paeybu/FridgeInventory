package com.kabu.kabi.fridgeinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class FridgeContract {
    public static final String CONTENT_AUTHORITY = "com.kabu.kabi.fridgeinventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "items";
    private FridgeContract() {}


    public static class FridgeEntry implements BaseColumns {
        public static final String TABLE_NAME = "items";
        public static final String COLUMN_ITEM_NAME = "name";
        public static final String COLUMN_ITEM_QUANTITY = "quantity";
        public static final String COLUMN_ITEM_UNIT = "unit";

        public static final int UNIT_PIECES = 0;
        public static final int UNIT_BOTTLES = 1;
        public static final int UNIT_KG = 2;
        public static final int UNIT_L = 3;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;



    }
}
