package com.as.schat;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.net.ParseException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class FriendsFragment extends ListFragment{
	protected ParseRelation<ParseUser> mFriendsRelation;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_friends, container,
				false);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		mFriendsRelation = ParseUser.getCurrentUser().getRelation(ParseConstants.KEY_FRIENDS_RELATION);
		mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> friends, com.parse.ParseException e) {
				if(e == null) {
					String frnds[] = new String[friends.size()];
					int i = 0;
					for(ParseUser friend: friends) {
						frnds[i] = friend.getUsername().toString();
						i++;
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<>(getListView().getContext(), android.R.layout.simple_list_item_1, frnds);
					setListAdapter(adapter);
				}
			}
		});
	}	
	
}
