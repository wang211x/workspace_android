package com.example.picdemo;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
	 
    // All Static variables
    // Database Version
    public static int DATABASE_VERSION = 2;
 
    // Database Name
    private static final String DATABASE_NAME = "ImageManager";
 
    // Image table name
    private static final String IMAGE_FILES = "imageFiles";
    
    
    // Images Table Columns names for image table
    private static final String IMAGE_ID = "id";
    private static final String IMAGE_NAME = "image";
    private static final String IMAGE_UPLOAD_STATUS = "image_upload_status";
    private static final String IMAGE_UPLOAD_PROGRESS = "image_upload_progress";
    
    //Time table name
    private static final String TIME_TABLE = "timetable";
    
    //Time table column name
    private static final String TIME_ID = "id";
    private static final String TIME = "time";
    
    
    
    private static DatabaseHandler instance;
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public static synchronized DatabaseHandler getHelper(Context context)
    {
        if (instance == null)
            instance = new DatabaseHandler(context);
   
        return instance;
    }
    
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_IMAGES_TABLE = "CREATE TABLE " + IMAGE_FILES + "("
                + IMAGE_ID + " INTEGER PRIMARY KEY," + IMAGE_NAME + " TEXT,"
                + IMAGE_UPLOAD_STATUS + " TEXT," + IMAGE_UPLOAD_PROGRESS + " TEXT"+")";
        String CREATE_TIME_TABLE = "CREATE TABLE " + TIME_TABLE + "("
                + TIME_ID + " INTEGER PRIMARY KEY," + TIME + " TEXT"
                +")";
        db.execSQL(CREATE_IMAGES_TABLE);
        db.execSQL(CREATE_TIME_TABLE);
       
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + IMAGE_FILES);
        db.execSQL("DROP TABLE IF EXISTS " + TIME_TABLE);
        // Create tables again
        onCreate(db);
    }
    public void addContact(imageFile imageFile) {
        SQLiteDatabase db = this.getWritableDatabase();
     
        ContentValues values = new ContentValues();
        values.put(IMAGE_NAME, imageFile.getName()); // File  Name
        values.put(IMAGE_UPLOAD_STATUS, imageFile.getImageUploadStatus()); // Upload Status
        values.put(IMAGE_UPLOAD_PROGRESS, imageFile.get_progress_status()); // Upload Progress
        // Inserting Row
        db.insert(IMAGE_FILES, null, values);
        
        db.close(); // Closing database connection
        
    }
    
    public void addTime(String time) {
        SQLiteDatabase db = this.getWritableDatabase();
     
        ContentValues values = new ContentValues();
        values.put(TIME, time); // Add time 
        System.out.println("Time value"+time);
       // System.out.println("Addition count"+getTime());
        // Inserting Row
        try{
        db.insert(TIME_TABLE, null, values);
        }
        catch(Exception e){
        	e.printStackTrace();
        }
        db.close(); // Closing database connection
        
    }
    
    
    public String getTime(){
    	
    	String countQuery = "SELECT * FROM " + TIME_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        String startTime = "0";
        if (cursor.moveToFirst()) {
            startTime = new String(cursor.getString(1));
        }
        cursor.close();
        db.close();
        System.out.println("sTatus check"+startTime);
    	return startTime;
    }
    
    public void addFileUploadStatus(imageFile imageFile){
    	SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(IMAGE_NAME, imageFile.getName()); // Upload Filename
        values.put(IMAGE_UPLOAD_STATUS, "true"); // Upload Progress
     
        // Updating Row
        int valueafter = db.update(IMAGE_FILES, values, IMAGE_NAME+" = ?", new String[] {imageFile.getName()});
        db.close();
    }
    
    public void addFileUploadProgress(imageFile imageFile){
    	SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(IMAGE_NAME, imageFile.getName()); // Upload Filename
        values.put(IMAGE_UPLOAD_PROGRESS, imageFile.get_progress_status()); // Progress Status
     
        // Updating Row
        int valueafter = db.update(IMAGE_FILES, values, IMAGE_NAME+" = ?", new String[] {imageFile.getName()});
        System.out.println("Printing from upload fileprogress"+valueafter);
        db.close();
    }
    
    public int getImageUploadCount() {
    	String countQuery = "SELECT * FROM " + IMAGE_FILES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            do {
            	
                if(cursor.getString(2).contains("true"))
                		{
                	count++;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return count;
    }
    public int getImageCount() {
        String countQuery = "SELECT * FROM " + IMAGE_FILES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        // looping through all rows and adding to list
        System.out.println("Count total"+count);
        cursor.close();
        db.close();
        // return count
        return count;
    }
    public String getCurrentImageUploadStatus(){
    	String countQuery = "SELECT * FROM " + IMAGE_FILES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        // looping through all rows and adding to list
        int progress_bar=0;
        int image_number=0;
        if (cursor.moveToFirst()) {
            do {
            	
                if(!cursor.getString(3).contains("0") && !cursor.getString(3).contains("100"))
                		{
                	image_number = count;
                }
                count++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    	return String.valueOf(image_number) + "/" + String.valueOf(progress_bar);
    	
    }
    public void getAllContacts() {
        //List<imageFile> contactList = new ArrayList<imageFile>();
        // Select All Query
        String selectQuery = "SELECT  COUNT(*) FROM " + IMAGE_FILES +" WHERE IMAGE_UPLOAD_STATUS = \"true\" ";
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
 
        // return contact list
        
    }
    
    public ArrayList<String> getImageNotUploadCount() {
    	String countQuery = "SELECT * FROM " + IMAGE_FILES;
    	ArrayList<String> filePathList = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            do {
            	
                if(cursor.getString(2).contains("false"))
                		{
                		filePathList.add(cursor.getString(1));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return filePathList;
    }
}