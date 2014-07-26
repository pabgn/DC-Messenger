package com.example.pablo.app2;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.example.pablo.app2.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends Activity {
    String background;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String username = settings.getString("username", "");
        background = settings.getString("background", "1");

        if(username.length()>0){
            EditText usernameInput = (EditText)findViewById(R.id.usernameInput);
            usernameInput.setText(username);
        }
        EditText t = (EditText)findViewById(R.id.usernameInput);
        t.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                AQuery aq = new AQuery(getApplicationContext());
                ImageView imgView = (ImageView) findViewById(R.id.loginAvatar);
                //MD5 user
                String user = s.toString();
                MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                md.update(user.getBytes());
                byte[] digest = md.digest();
                StringBuffer sb = new StringBuffer();
                for (byte b : digest) {
                    sb.append(String.format("%02x", b & 0xff));
                }

                aq.id(imgView).image("http://vanillicon.com/"+sb.toString()+"_200.png");
            }
        });
        //editor.putInt(getString(R.string.saved_high_score), newHighScore);
        //editor.commit();
    }
    public void doLogin(View v){

        Intent intent = new Intent(Login.this, MyActivity.class);
        Bundle b = new Bundle();
        EditText usernameInput = (EditText)findViewById(R.id.usernameInput);
        EditText teamInput = (EditText)findViewById(R.id.teamInput);
        if(usernameInput.getText().toString().length()>0) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("username",  usernameInput.getText().toString());
            editor.commit();

            b.putString("username", usernameInput.getText().toString());
            b.putString("team", teamInput.getText().toString());
            b.putString("background", background);

            intent.putExtras(b); //Put your id to your next Intent
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(this.getApplicationContext(), "Introduce un usuario", Toast.LENGTH_LONG).show();

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
