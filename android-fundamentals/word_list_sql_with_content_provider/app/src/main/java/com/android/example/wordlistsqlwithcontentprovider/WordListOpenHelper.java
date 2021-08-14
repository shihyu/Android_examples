/*
 * Copyright (C) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.example.wordlistsqlwithcontentprovider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import static com.android.example.wordlistsqlwithcontentprovider.Contract.ALL_ITEMS;
import static com.android.example.wordlistsqlwithcontentprovider.Contract.DATABASE_NAME;
import static com.android.example.wordlistsqlwithcontentprovider.Contract.WordList.KEY_ID;
import static com.android.example.wordlistsqlwithcontentprovider.Contract.WordList.KEY_WORD;
import static com.android.example.wordlistsqlwithcontentprovider.Contract.WordList.WORD_LIST_TABLE;
/**
 * Open helper for the list of words database.
 */
public class WordListOpenHelper extends SQLiteOpenHelper
{
    private static final String TAG = WordListOpenHelper.class.getSimpleName();
    // Has to be 1 first time or app will crash.
    private static final int DATABASE_VERSION = 1;
    SQLiteDatabase mWritableDB;
    SQLiteDatabase mReadableDB;
    ContentValues mValues = new ContentValues();
    // Build the SQL query that creates the table.
    private static final String WORD_LIST_TABLE_CREATE =
        "CREATE TABLE " + WORD_LIST_TABLE + " (" +
        KEY_ID + " INTEGER PRIMARY KEY, " + // will auto-increment if no value passed
        KEY_WORD + " TEXT );";
    public WordListOpenHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(WORD_LIST_TABLE_CREATE);
        fillDatabaseWithData(db);
    }
    /**
     * Adds the initial data set to the database.
     * According to the docs, onCreate for the open helper does not run on the UI thread.
     *
     * @param db Database to fill with data since the member variables are not initialized yet.
     */
    public void fillDatabaseWithData(SQLiteDatabase db)
    {
        String[] words = {"Android", "Adapter", "ListView", "AsyncTask", "Android Studio",
                          "SQLiteDatabase", "SQLOpenHelper", "Data model", "ViewHolder",
                          "Android Performance", "OnClickListener"
                         };
        // Create a container for the data.
        ContentValues values = new ContentValues();

        for (int i = 0; i < words.length; i++) {
            // Put column/value pairs into the container, overriding existing values.
            values.put(KEY_WORD, words[i]);
            db.insert(WORD_LIST_TABLE, null, values);
        }
    }
    /**
     * Called when a database needs to be upgraded. The most basic version of this method drops
     * the tables, and then recreates them. All data is lost, which is why for a production app,
     * you want to back up your data first. If this method fails, changes are rolled back.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w(WordListOpenHelper.class.getName(),
              "Upgrading database from version " + oldVersion + " to "
              + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + WORD_LIST_TABLE);
        onCreate(db);
    }
    /**
     * Queries the database for an entry at a given position.
     *
     * @param position The Nth row in the table.
     * @return a WordItem with the requested database entry.
     */
    public Cursor query(int position)
    {
        String query;

        if (position != ALL_ITEMS) {
            position++; // Because database starts counting at 1.
            query = "SELECT " + KEY_ID + "," + KEY_WORD + " FROM " + WORD_LIST_TABLE +
                    " WHERE " + KEY_ID + "=" + position + ";";
        } else {
            query = "SELECT  * FROM " + WORD_LIST_TABLE + " ORDER BY " + KEY_WORD + " ASC ";
        }

        Cursor cursor = null;

        try {
            if (mReadableDB == null) {
                mReadableDB = this.getReadableDatabase();
            }

            cursor = mReadableDB.rawQuery(query, null);
        } catch (Exception e) {
            Log.d(TAG, "QUERY EXCEPTION! " + e);
        }

        finally {
            return cursor;
        }
    }
    /**
     * Gets the number of rows in the word list table.
     *
     * @return The number of entries in WORD_LIST_TABLE.
     */
    public Cursor count()
    {
        MatrixCursor cursor = new MatrixCursor(new String[] {Contract.CONTENT_PATH});

        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }

            // queryNumEntries returns a long, but we need to pass up an int.
            // With the small number of entries, no worries about losing precision.
            int count = (int) DatabaseUtils.queryNumEntries(mReadableDB, WORD_LIST_TABLE);
            cursor.addRow(new Object[] {count});
        } catch (Exception e) {
            Log.d(TAG, "COUNT EXCEPTION " + e);
        }

        return cursor;
    }
    /**
     * Adds a single word row/entry to the database.
     *
     * @param  values Container for key/value columns/values.
     * @return The id of the inserted word.
     */
    public long insert(ContentValues values)
    {
        long added = 0;

        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }

            added = mWritableDB.insert(WORD_LIST_TABLE, null, values);
        } catch (Exception e) {
            Log.d(TAG, "INSERT EXCEPTION " + e);
        }

        return added;
    }
    /**
     * Updates  word with the supplied id to the supplied value.
     *
     * @param id Id of the word to update.
     * @param word The new value of the word.
     * @return the number of rows affected.
     */
    public int update(int id, String word)
    {
        int updated = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_WORD, word);

            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }

            updated = mWritableDB.update(WORD_LIST_TABLE, //table to change
                                         values, // new values to insert
                                         KEY_ID + " = ?", // selection criteria for row (in this case, the _id column)
                                         new String[] {String.valueOf(id)}); //selection args; the actual value of the id
        } catch (Exception e) {
            Log.d(TAG, "UPDATE EXCEPTION " + e);
        }

        return updated;
    }
    /**
     * Deletes one entry identified by its id.
     *
     * @param id ID of the entry to delete.
     * @return The number of rows deleted. Since we are deleting by id, this should be 0 or 1.
     */
    public int delete (int id)
    {
        int deleted = 0;

        try {
            if (mWritableDB == null) {
                mWritableDB = this.getWritableDatabase();
            }

            deleted = mWritableDB.delete(WORD_LIST_TABLE,
                                         KEY_ID + " = ? ", new String[] {String.valueOf(id)});
        } catch (Exception e) {
            Log.d(TAG, "DELETE EXCEPTION " + e);
        }

        return deleted;
    }
}
