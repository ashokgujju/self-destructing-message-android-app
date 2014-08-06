package com.as.schat;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.ParseException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditFriendsActivity extends ListActivity {

	protected List<ParseUser> mUsers;
	protected ParseRelation<ParseUser> mFriendsRelation;
	protected ParseUser mCurrentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_friends);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		super.onResume();
		mCurrentUser = ParseUser.getCurrentUser();
		
		mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.orderByAscending(ParseConstants.KEY_USERNAME);
		query.setLimit(100);
		query.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> users, com.parse.ParseException e) {
				if(e == null) {
					mUsers = users;
					String[] usernames = new String[mUsers.size()];
					int i=0;
					for(ParseUser user: mUsers) {
						usernames[i] = user.getUsername();
						i++;
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<>(
							EditFriendsActivity.this, android.R.layout.simple_list_item_checked, 
							usernames);
					setListAdapter(adapter);
					addFriendCheckmarks();
				}
				else {
					AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
					builder.setTitle("We're sorry!");
					builder.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			}

			private void addFriendCheckmarks() {
				mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
					@Override
					public void done(List<ParseUser> friends,
							com.parse.ParseException e) {
						if(e == null) {
							for(int i = 0; i < mUsers.size(); i++) {
								ParseUser user = mUsers.get(i);
								for(ParseUser friend : friends) {
									if(friend.getObjectId().equals(user.getObjectId()))
										getListView().setItemChecked(i, true);
								}
							}
						}
					}
				});
				
			}
		});
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		if(getListView().isItemChecked(position)) {
			//add friend
			mFriendsRelation.add(mUsers.get(position));
			mCurrentUser.saveInBackground(new SaveCallback() {
				
				@Override
				public void done(com.parse.ParseException arg0) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		else {
			mFriendsRelation.remove(mUsers.get(position));
			mCurrentUser.saveInBackground(new SaveCallback() {

				@Override
				public void done(com.parse.ParseException arg0) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
	}
}
