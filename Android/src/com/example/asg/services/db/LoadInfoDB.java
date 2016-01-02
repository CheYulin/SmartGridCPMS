package com.example.asg.services.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LoadInfoDB extends SQLiteOpenHelper {
	private final static String DATABASE_NAME = "asg_db"; // sql���ݿ������
	private final static int DATABASE_VERSION = 1; 			// sql���ݿ�İ汾��Ϣ
	private final static String TABLE_NAME = "loadinfo_table"; // ������Ϣ�ı���
	public final static String FIELD_ID = "_id"; // Ϊ��ʵ��SimpleCursorAdaptor������Ҫ��
	// ������4���ؼ��ĸ�����Ϣ���ݣ�
	public final static String FIELD_NAME = "name";
	public final static String FIELD_aplow = "aplow";
	public final static String FIELD_apup = "apup";
	public final static String FIELD_rplow = "rplow";
	public final static String FIELD_rpup = "rpup";

	public LoadInfoDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqldb) { // sqldb��һ�����
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder("CREATE TABLE ");
		sb.append(TABLE_NAME);
		sb.append(" (");
		sb.append(FIELD_ID);
		sb.append(" INTEGER primary key autoincrement, ");
		sb.append(FIELD_NAME);
		sb.append(" VARCHAR(20) NOT NULL, ");
		sb.append(FIELD_aplow);
		sb.append(" DOUBLE NOT NULL, ");
		sb.append(FIELD_apup);
		sb.append(" DOUBLE NOT NULL, ");
		sb.append(FIELD_rplow);
		sb.append(" DOUBLE NOT NULL, ");
		sb.append(FIELD_rpup);
		sb.append(" DOUBLE NOT NULL)");
		sqldb.execSQL(sb.toString()); // ������
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqldb, int arg1, int arg2) {
		// arg1Ϊ�ɰ汾�� arg2Ϊ�°汾��
		// TODO Auto-generated method stub
	}

	public Cursor select(double ap, double rp) // ���ҵ�ʱ����ݻ�õ�������Ϣ����
	{
		// �ȵõ�һ�����ľ��
		SQLiteDatabase db = this.getReadableDatabase();
		String whereclause = FIELD_aplow + "<? and " + FIELD_apup
				+ ">?" + " and " + FIELD_rplow + "<? and "
				+ FIELD_rpup + ">?";
		String selectionargs[] = { String.valueOf(ap), String.valueOf(ap),
				String.valueOf(rp), String.valueOf(rp) };
		String columns[] = { FIELD_NAME };
		Cursor cursor = db.query(TABLE_NAME, columns, whereclause,
				selectionargs, null, null, null);
		return cursor;
	}

	public Cursor select() // ���ҵ�ʱ����ݻ�õ�������Ϣ����
	{
		// �ȵõ�һ�����ľ��
		SQLiteDatabase db = this.getReadableDatabase();
		String whereclause = null;
		String selectionargs[] = null;
		String columns[] = null;
		Cursor cursor = db.query(TABLE_NAME, columns, whereclause,
				selectionargs, null, null, null);
		return cursor;
	}

	public long insert(String name, double aplow, double apup, double rplow,
			double rpup) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues(); // ���һ����¼
		cv.put(FIELD_NAME, name);
		cv.put(FIELD_aplow, aplow);
		cv.put(FIELD_apup, apup);
		cv.put(FIELD_rplow, rplow);
		cv.put(FIELD_rpup, rpup);
		long row = db.insert(TABLE_NAME, null, cv);
		return row; // ��ʾ��ĿǰΪֹ�ж���������¼
	}

	public void delete(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String whereClause = FIELD_ID + " =?"; // ɾ����ʱ��Ҫ���ݹؼ�����ɾ��
		String[] whereArgs = { String.valueOf(id) };
		db.delete(TABLE_NAME, whereClause, whereArgs);
	}

	public void update(int id, String name, double aplow, double apup,
			double rplow, double rpup) {
		// Ҫ������Ӧ�Ĺؼ������ҵ�Ҫ�޸ĵļ�¼
		SQLiteDatabase db = this.getWritableDatabase();
		String whereClause = FIELD_ID + " =?"; // ���µ�ʱ��Ҫ���ݹؼ�������
		Log.i("���µ�SQL���", whereClause);
		String[] whereArgs = { String.valueOf(id) };
		Log.i("����", whereArgs[0]);
		ContentValues cv = new ContentValues();
		cv.put(FIELD_NAME, name);
		cv.put(FIELD_aplow, aplow);
		cv.put(FIELD_apup, apup);
		cv.put(FIELD_rplow, rplow);
		cv.put(FIELD_rpup, rpup);
		db.update(TABLE_NAME, cv, whereClause, whereArgs);

	}

}
