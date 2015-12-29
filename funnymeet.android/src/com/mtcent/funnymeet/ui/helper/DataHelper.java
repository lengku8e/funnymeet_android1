package com.mtcent.funnymeet.ui.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper {

	// private static final String DATABASE_CREATE =
	// "create table titles (_id integer primary key autoincrement, " +
	// "isbn text not null, title text not null, " +
	// "publisher text not null);";

	Context mContext = null;

	public static class Sinfo {
		public String key;
		public String value;
		public long time;
	}

	private class STORAGEDatabaseHelper extends SQLiteOpenHelper {
		public static final String STORAGEDATABASE_NAME = "storage.db";
		public static final String STORAGEDATABASE_TableNAME = "keyvalue";
		public static final int STORAGEDATABASE_VERSION = 1;

		public static final String Table_key = "key";
		public static final String Table_value = "value";
		public static final String Table_time = "time";

		STORAGEDatabaseHelper(Context context) {
			super(context, STORAGEDATABASE_NAME, null, STORAGEDATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// ???????
			db.execSQL("CREATE TABLE IF NOT EXISTS " + STORAGEDATABASE_TableNAME + "(" + //
					Table_key + " varchar primary key," + //
					Table_value + " varchar," + //
					Table_time + " double(-1)" + //
					")");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS diary");
			onCreate(db);
		}
	}

	private SQLiteDatabase StorageDatadb = null;

	private STORAGEDatabaseHelper StorageDatadbHelper = null;

	public DataHelper(Context context) {
		mContext = context;
		// workerDatadbHelper = new WorkerDatabaseHelper(mContext);
		// workerDatadb = workerDatadbHelper.getWritableDatabase();
	}

	String mJobDatadbName = null;

	String getJobDatadbName() {
		return mJobDatadbName;
	}

	boolean OpenStorageDatadb() {
		if (StorageDatadb == null) {
			StorageDatadbHelper = new STORAGEDatabaseHelper(mContext);
			StorageDatadb = StorageDatadbHelper.getWritableDatabase();
		}
		if (StorageDatadb == null) {
			return false;
		} else {
			return true;
		}
	}

	public void colosDataManager() {
		if (StorageDatadb != null) {
			StorageDatadb.close();
			StorageDatadbHelper.close();
			StorageDatadb = null;
			StorageDatadbHelper = null;
		}
	}

	public Sinfo getValueInfo(String key) {
		Sinfo info = new Sinfo();
		if (OpenStorageDatadb()) {
			synchronized (StorageDatadb) {
				String[] whereArgs = { String.valueOf(key) };
				Cursor cursor = StorageDatadb.query(STORAGEDatabaseHelper.STORAGEDATABASE_TableNAME, null, STORAGEDatabaseHelper.Table_key + "=?", whereArgs, null, null, null);
				cursor.moveToFirst();
				if (!cursor.isAfterLast()) {
					info.key = cursor.getString(cursor.getColumnIndex(STORAGEDatabaseHelper.Table_key));
					info.time = cursor.getLong(cursor.getColumnIndex(STORAGEDatabaseHelper.Table_time));
					info.value = cursor.getString(cursor.getColumnIndex(STORAGEDatabaseHelper.Table_value));
				}
				cursor.close();
			}
		}
		return info;
	}

	public String getValue(String key) {
		return getValueInfo(key).value;
	}

	public boolean setValueInfo(Sinfo info) {
		boolean ret = false;
		try {
			if (OpenStorageDatadb()) {
				synchronized (StorageDatadb) {
					long id = -1;
					ContentValues values = getContentValues(info);
					String[] whereArgs = { String.valueOf(info.key) };
					id = StorageDatadb.update(STORAGEDatabaseHelper.STORAGEDATABASE_TableNAME, values, STORAGEDatabaseHelper.Table_key + "=?", whereArgs);
					if (id <= 0) {
						id = StorageDatadb.insert(STORAGEDatabaseHelper.STORAGEDATABASE_TableNAME, null, values);
					}
					ret = id >= 0;
				}
			}
		} catch (Exception e) {

		}
		return ret;
	}

	public boolean setValue(String key, String value) {
		Sinfo info = new Sinfo();
		info.key = key;
		info.value = value;
		info.time = System.currentTimeMillis();
		return setValueInfo(info);
	}

	ContentValues getContentValues(Sinfo info) {

		ContentValues values = null;
		if (info.key != null) {
			values = new ContentValues();
			values.put(STORAGEDatabaseHelper.Table_key, info.key);
			if (info.time >= 0) {
				values.put(STORAGEDatabaseHelper.Table_time, info.time);
			}
			if (info.value != null) {
				values.put(STORAGEDatabaseHelper.Table_value, info.value);
			}
		}
		return values;
	}

}
