package com.example.picdemo;



public class imageFile {
 
    //private variables
    int _id;
    String _name;
    String _upload_status;
    String _progress_status;
 
    public String get_progress_status() {
		return _progress_status;
	}
	public void set_progress_status(String _progress_status) {
		this._progress_status = _progress_status;
	}
	// Empty constructor
    public imageFile(){
 
    }
    // constructor
    public imageFile (String name, String upload_status, String progress_status){
        this._id = 0;
        this._name = name;
        this._upload_status = upload_status;
        this._progress_status = progress_status;
    }
 
    // constructor
    public imageFile(String name, String upload_status){
        this._name = name;
        this._upload_status = upload_status;
    }
  
    // getting ID
    public int getID(){
        return this._id;
    }
 
    // setting id
    public void setID(int id){
        this._id = id;
    }
 
    // getting name
    public String getName(){
    	String name[] = this._name.split("/");
		String filenameurl = name[name.length - 1];
        return filenameurl;
    }
 
    // setting name
    public void setName(String name){
        this._name = name;
    }
 
    // getting phone number
    public String getImageUploadStatus(){
        return this._upload_status;
    }
 
    // setting phone number
    public void setImageUploadStatus(String upload_status){
        this._upload_status = upload_status;
    }
}