package com.freshplanet.ane.AirVideo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class CreateFileTask extends AsyncTask<InputStream, Integer, String> {

	private static String TAG = "CreateFileTask";
	
	private String filePath = null;
	
	
	@Override
	protected String doInBackground(InputStream... inputs) {

		if (inputs == null || inputs.length == 0)
		{
			Log.d(TAG, "param is null");
			return null;
		}
		
		InputStream input = inputs[0];

		if (input == null)
		{
			Log.d(TAG, "stream is null");
			return null;
		}
		
		Log.d(TAG, "creating file");
		File tempFile;
		try {
			tempFile = File.createTempFile("video", ".mp4");
		} catch (IOException e1) {
			Log.e(TAG, "couldn't create tmp file");
			e1.printStackTrace();
			return null;
		}
		
		filePath = tempFile.getAbsolutePath();
		Log.d(TAG, "getting file temp: "+filePath);

	    FileOutputStream out = null;
		try {
			out = new FileOutputStream(tempFile);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "file out not found");
			e.printStackTrace();
		}   
		
		Log.d(TAG, "start buffering");
	    byte buf[] = new byte[16384];
	    Log.d(TAG, "start buffering2 ");
	    do {
	        int numread = -1;
			try {
				numread = input.read(buf);
			} catch (IOException e) {
				Log.e(TAG, "couldn't read");
				e.printStackTrace();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			
	        if (numread <= 0) break;   
	        try {
				out.write(buf, 0, numread);
			} catch (IOException e) {
				Log.e(TAG, "couldn't write");
				e.printStackTrace();
			}
	    } while (input != null);

	    Log.d(TAG, "out of the loop ");
		if (out != null)
		{
			try {
				out.close();
			} catch (IOException e) {
				Log.e(TAG, "couldn't close");
				e.printStackTrace();
			}
		}
	    
		return null;
	}

	@Override
    protected void onPostExecute(String result) {
		
		Log.d(TAG, "setting the video param");
		Extension.context.getVideoView().setVideoURI(Uri.parse(filePath));
		Log.d(TAG, "starting the video");
		
		Extension.context.getVideoView().start();

	}

	
}
