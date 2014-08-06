package com.as.schat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class MainActivity extends Activity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	public static final int TAKE_PHOTO_REQUEST = 0;
	public static final int TAKE_VIDEO_REQUEST = 1;
	public static final int CHOOSE_PHOTO_REQUEST = 2;
	public static final int CHOOSE_VIDEO_REQUEST = 3;
	
	public static final int MEDIA_TYPE_IMAGE = 4;
	public static final int MEDIA_TYPE_VIDEO = 5;
	
	public static final int FILE_SIZE_LIMIT = 1024*1024*10;
	
	protected Uri mMediaUri;
	
	DialogInterface.OnClickListener mDialogueListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch(which) {
				case 0:
					//take picture
					Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
					takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
					startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
					break;
				case 1:
					//take video
					Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
					mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
					takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
					takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
					takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
					startActivityForResult(takeVideoIntent, TAKE_VIDEO_REQUEST);
					break;
				case 2:
					//choose picture
					Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
					choosePhotoIntent.setType("image/*");
					startActivityForResult(choosePhotoIntent, CHOOSE_PHOTO_REQUEST);
					break;
				case 3:
					//choose video
					Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
					chooseVideoIntent.setType("video/*");
					startActivityForResult(chooseVideoIntent, CHOOSE_VIDEO_REQUEST);
			}
		}
		
		private Uri getOutputMediaFileUri(int mediaType) {
			 // To be safe, you should check that the SDCard is mounted
		    // using Environment.getExternalStorageState() before doing this.
			File mediaStorageDir = null;
			if(isExternalStorageAvailable()) {
				mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
			              Environment.DIRECTORY_PICTURES), "SChat");
			}
			else {
				mediaStorageDir = new File(Environment.DIRECTORY_DCIM);
				//Toast.makeText(MainActivity.this, mediaStorageDir.getParent()+"", Toast.LENGTH_LONG).show();
			}
			 if (! mediaStorageDir.exists()){
			        if (! mediaStorageDir.mkdirs()){
			            Log.d("MyCameraApp", "failed to create directory");
			            return null;
			        }
			    }
			
			 String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			 File mediaFile;
			    if (mediaType == MEDIA_TYPE_IMAGE){
			        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
			        "IMG_"+ timeStamp + ".jpg");
			        Log.d("MyCameraApp", mediaFile.toString());
			    } else if(mediaType == MEDIA_TYPE_VIDEO) {
			        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
			        "VID_"+ timeStamp + ".mp4");
			    } else {
			        return null;
			    }
			 return Uri.fromFile(mediaFile);
		}

		private boolean isExternalStorageAvailable() {
			String state = Environment.getExternalStorageState();
			if(state == Environment.MEDIA_MOUNTED)
				return true;
			else
				return false;
		}
	};
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	
	
	ViewPager mViewPager;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ParseAnalytics.trackAppOpened(getIntent());
		
		if(ParseUser.getCurrentUser() == null){
			navigateToLogin();
		}
		
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(this, getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK) {
			// add to the gallery
			if(requestCode == CHOOSE_PHOTO_REQUEST || requestCode == CHOOSE_VIDEO_REQUEST) {
				if(data == null) {
					Toast.makeText(this, "Sorry! Something went wrong", Toast.LENGTH_LONG).show();
				}
				else {
					mMediaUri = data.getData();
				}
				if(requestCode == CHOOSE_VIDEO_REQUEST){
					int fileSize = 0;
					InputStream inputStream = null;
					try {
						inputStream = getContentResolver().openInputStream(mMediaUri);
						fileSize = inputStream.available();
					} catch (IOException e) {
						Toast.makeText(this, "Sorry! Something went wrong", Toast.LENGTH_LONG).show();
						return;
					}
					finally {
						try {
							inputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if(fileSize >= FILE_SIZE_LIMIT) {
						Toast.makeText(this, "The selected file is too large! Select a new file.", Toast.LENGTH_LONG).show();
						return;
					}
					
				}
			}
			else{
				Intent g = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				g.setData(mMediaUri);
				sendBroadcast(g);
			}
			
			Intent reciepientIntent = new Intent(this, RecipientsActivity.class);
			reciepientIntent.setData(mMediaUri);
			
			String fileType;
			if (requestCode == CHOOSE_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST) {
				fileType = ParseConstants.TYPE_IMAGE;
			}
			else {
				fileType = ParseConstants.TYPE_VIDEO;
			}
			reciepientIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
			startActivity(reciepientIntent);
		}
		else if (resultCode != RESULT_CANCELED) {
			
		}
	}

	private void navigateToLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_logout:
			ParseUser.logOut();
			navigateToLogin();
			break;
		case R.id.action_edit_friends:
			Intent a = new Intent(this, EditFriendsActivity.class);
			startActivity(a);
			break;
		case R.id.action_camera:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setItems(R.array.camera_choices, mDialogueListener);
			AlertDialog dialog = builder.create();
			dialog.show();
	}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
}
