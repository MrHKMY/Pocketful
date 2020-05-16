package com.mindscape.budgetwiser;

import android.provider.BaseColumns;

/**
 * Created by Hakimi on 15/5/2020.
 */
public class DatabaseContract {

    public static final class DatabaseEntry implements BaseColumns {
        public static final String TABLE_NAME = "todayTable";
        public static final String COLUMN_NAME = "items";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
