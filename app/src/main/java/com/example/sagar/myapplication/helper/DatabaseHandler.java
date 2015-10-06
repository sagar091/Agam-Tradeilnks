package com.example.sagar.myapplication.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.model.ProductCart;
import com.example.sagar.myapplication.model.Scheme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagartahelyani on 18-09-2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String KEY_ROWID = "_id";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "agamtradelinks.db";
    private static final String TABLE_CART_ITEM = "Cart";
    private static final String TABLE_SCHEME = "Scheme";

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

    public boolean addCartProduct(ArrayList<String> cartProductDetails, List<Scheme> schemes) {

        myDataBase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("product_id", Integer.parseInt(cartProductDetails.get(0))); // id
        values.put("name", cartProductDetails.get(1)); // name
        values.put("price", Integer.parseInt(cartProductDetails.get(2))); // price
        values.put("qty", Integer.parseInt(cartProductDetails.get(3))); // qty
        values.put("colors", cartProductDetails.get(4)); // colors
        myDataBase.insert(TABLE_CART_ITEM, null, values);

        for (int i = 0; i < schemes.size(); i++) {
            ContentValues cv = new ContentValues();
            cv.put("product_id", Integer.parseInt(cartProductDetails.get(0))); // id
            String strScheme = "Buy " + schemes.get(i).quantity + " at  " + context.getResources().getString(R.string.Rs) + " " + schemes.get(i).price;
            cv.put("scheme", strScheme); // scheme text
            cv.put("scheme_id", schemes.get(i).id); // scheme id
            myDataBase.insert(TABLE_SCHEME, null, values);

        }

        return true;

    }

    public boolean productExist(String productID) {
        boolean available = false;
        myDataBase = this.getWritableDatabase();
        Cursor cursor;
        String selectQuery = "SELECT * FROM " + TABLE_CART_ITEM + " WHERE product_id =" + productID;
        cursor = myDataBase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.getCount() > 0) {
            available = true;
            cursor.moveToFirst();
        }
        return available;
    }

    public ArrayList<ProductCart> getProducts() {
        ArrayList<ProductCart> products = new ArrayList<>();

        myDataBase = this.getWritableDatabase();
        Cursor cursor;
        String selectQuery = "SELECT * FROM " + TABLE_CART_ITEM;
        cursor = myDataBase.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            do {
                ProductCart cart = new ProductCart();
                cart.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                cart.setPrice(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("price"))));
                cart.setProductId(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("product_id"))));
                cart.setQty(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("qty"))));
                cart.setColors(cursor.getString(cursor.getColumnIndexOrThrow("colors")));
                products.add(cart);
            } while (cursor.moveToNext());
        }

        return products;
    }

}
