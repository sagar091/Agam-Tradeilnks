package com.example.sagar.myapplication.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by sagartahelyani on 18-09-2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String KEY_ROWID = "_id";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "agamtradelinks.db";
    private static final String TABLE_CART_ITEM = "Cart";

    private static final String DATABASE_PATH = "/data/data/com.example.sagar.myapplication/databases/";
    private Context context;
    private SQLiteDatabase myDataBase = null;

    public void createDatabase() throws IOException {

        boolean dbExist = checkDataBase();
        if (dbExist) {
//            Log.v("log_tag", "database does exist");
        } else {
//            Log.v("log_tag", "database does not exist");
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error("Error copying database");
            }
        }
    }

    private void copyDataBase() throws IOException {

        InputStream myInput = context.getAssets().open(DATABASE_NAME);
        String outFileName = DATABASE_PATH + DATABASE_NAME;
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

    public boolean openDataBase() throws SQLException {
        String mPath = DATABASE_PATH + DATABASE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return myDataBase != null;

    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    // Constructor
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private boolean checkDataBase() {

        File folder = new File(DATABASE_PATH);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File dbFile = new File(DATABASE_PATH + DATABASE_NAME);
        return dbFile.exists();
    }

    public boolean addCartProduct(ArrayList<String> productDetails) {

        myDataBase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("product_id", Integer.parseInt(productDetails.get(0)));
        values.put("product_qty", Integer.parseInt(productDetails.get(1)));
        values.put("product_colors", productDetails.get(2));
        myDataBase.insert(TABLE_CART_ITEM, null, values);

        return true;

    }

}
