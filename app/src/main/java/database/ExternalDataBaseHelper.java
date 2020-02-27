package database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ExternalDataBaseHelper extends SQLiteOpenHelper {

    private static String DB_PATH =  null ;
    private static String DB_NAME = "bibleverses.sqlite";
    public static final String KEY_ROWID="_id";
    public static final String KEY_HEADING="heading";
    public static final String KEY_VERSE="verse";
    public static final String KEY_TABLE_NAME="name";

    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public ExternalDataBaseHelper(Context context) {

        super(context, DB_NAME, null, 3);
        if(android.os.Build.VERSION.SDK_INT >= 17){
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        }
        else
        {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.myContext = context;
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if (!dbExist)
        {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            Log.i(myPath,"DB_Path___________________________________________");
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
        }

        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void deleteDatabase()
    {
        myContext.deleteDatabase(DB_NAME);
    }

    public SQLiteDatabase openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        return  myDataBase;
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if(newVersion>oldVersion)
        {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    public ArrayList<String> fetchAllVersesCategories(SQLiteDatabase db) {

        ArrayList<String> arrTblNames = new ArrayList<String>();
        String tableName="";

        Cursor mCursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table'", null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }


        if (mCursor.moveToFirst()) {
            while ( !mCursor.isAfterLast() ) {
                tableName = mCursor.getString( mCursor.getColumnIndex("name"));
                tableName = tableName.substring(0, 1).toUpperCase() + tableName.substring(1);
                if ( ! tableName.equals("Android_metadata")) {
                    arrTblNames.add(tableName);
                }
                mCursor.moveToNext();
            }
        }

        return arrTblNames;
    }

    public HashMap<Integer,String> fetchAllVerses(String SQLITE_TABLE) {

        HashMap<Integer,String> mapVerses = new HashMap<Integer,String>();
        String verseHeading = "", verse = "", finalVerse = "";
        Integer verseId = 0;

        Cursor mCursor = myDataBase.query(SQLITE_TABLE, new String[] {KEY_ROWID,
                        KEY_HEADING, KEY_VERSE},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        if (mCursor.moveToFirst()) {
            while ( !mCursor.isAfterLast() ) {
                verseHeading = mCursor.getString( mCursor.getColumnIndex(KEY_HEADING));
                verse = mCursor.getString( mCursor.getColumnIndex(KEY_VERSE));
                verseId = mCursor.getInt(mCursor.getColumnIndex(KEY_ROWID));

                //SpannableString finalVerse = new SpannableString(verseHeading + "\n \n" + verse);
                //finalVerse.setSpan(new StyleSpan(Typeface.ITALIC), 0, verseHeading.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

               finalVerse = verseHeading + "@" + verse;
                mapVerses.put(verseId,finalVerse);
                mCursor.moveToNext();
            }
        }
        return mapVerses;
    }

    public int generateRamdomNumber(int endIndex){

        Random random = new Random();
        int randomNumber = random.nextInt(endIndex) + 1;
        return randomNumber;
    }

    public String fetchVerse(String SQLITE_TABLE){
        Integer verseId = 0;
        String verseHeading = "", verse = "", finalVerse = "";
        SQLITE_TABLE = SQLITE_TABLE.toLowerCase();
        if(SQLITE_TABLE.equals("change")) {
            verseId = generateRamdomNumber(74);
        }
        else if (SQLITE_TABLE.equals("comfort")){
            verseId = generateRamdomNumber(86);
        }
        else if (SQLITE_TABLE.equals("courage")){
            verseId = generateRamdomNumber(72);
        }
        else if (SQLITE_TABLE.equals("eternal_life")){
            verseId = generateRamdomNumber(58);
        }
        else if (SQLITE_TABLE.equals("happiness")){
            verseId = generateRamdomNumber(25);
        }
        else if (SQLITE_TABLE.equals("healing") || SQLITE_TABLE.equals("health")){
            verseId = generateRamdomNumber(45);
        }
        else if (SQLITE_TABLE.equals("marriage")){
            verseId = generateRamdomNumber(68);
        }
        else if (SQLITE_TABLE.equals("pain")){
            verseId = generateRamdomNumber(66);
        }
        else if (SQLITE_TABLE.equals("path")){
            verseId = generateRamdomNumber(76);
        }
        else if (SQLITE_TABLE.equals("patience")){
            verseId = generateRamdomNumber(55);
        }
        else if (SQLITE_TABLE.equals("success")){
            verseId = generateRamdomNumber(28);
        }
        else {
            verseId = generateRamdomNumber(100);
        }

        Log.d("Category", verseId + "");

        String query = "Select " + KEY_HEADING + "," + KEY_VERSE + " from " + SQLITE_TABLE + " WHERE " + KEY_ROWID + "=" + verseId;
        Log.d("query", query);
        Cursor mCursor = myDataBase.rawQuery(query, null);
        mCursor.moveToFirst();

        verseHeading = mCursor.getString(0);
        verse = mCursor.getString(1);
        mCursor.close();
        finalVerse = verseHeading + "@" + verse;
        return finalVerse;
    }

}
