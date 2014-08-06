package com.as.schat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends Activity {

	protected TextView mEmail;
	protected TextView mUsername;
	protected TextView mPassword;
	protected Button mSignUpButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		
		mEmail = (TextView) findViewById(R.id.emailField);
		mUsername = (TextView) findViewById(R.id.username_signup);
		mPassword = (TextView) findViewById(R.id.password_signup);
		mSignUpButton = (Button) findViewById(R.id.signUpButton);
		mSignUpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String email = mEmail.getText().toString();
				String username = mUsername.getText().toString();
				String password = mPassword.getText().toString();
				
				email = email.trim();
				username = username.trim();
				password = password.trim();
				
				if(username.isEmpty() || email.isEmpty() || password.isEmpty()) {
					AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
					builder.setMessage(R.string.signup_error_message)
						.setTitle(R.string.signup_error_title)
						.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
				else {
					
					ParseUser newuser = new ParseUser();
					newuser.setUsername(username);
					newuser.setPassword(password);
					newuser.setEmail(email);
					newuser.signUpInBackground(new SignUpCallback() {
						
						@Override
						public void done(com.parse.ParseException e) {
							if(e == null) {
								ParseUser.logOut();
								Toast.makeText(SignUpActivity.this, "SignUp completed successfully..!!", Toast.LENGTH_SHORT).show();								
								Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
								i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(i);
							}
							else {
								mUsername.setText("");
								mPassword.setText("");
								mEmail.setText("");
								Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
							}
						}
					});					
				}
			}
		});
	}
}
