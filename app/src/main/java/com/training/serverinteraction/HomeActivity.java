package com.training.serverinteraction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HomeActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @InjectView(R.id.textViewTitle)
    TextView txtTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.inject(this);

        preferences = getSharedPreferences(Util.PREFS_NAME,MODE_PRIVATE);
        editor = preferences.edit();

        String name = preferences.getString(Util.KEY_NAME,"");
        String email = preferences.getString(Util.KEY_EMAIL,"");
        String phone = preferences.getString(Util.KEY_PHONE,"");

        txtTitle.setText("Welcome Home, "+name+"\nYour Email: "+email+"\nYour Phone: "+phone);

        getSupportActionBar().setTitle("Welcome "+name);




    }

    public void clickHandler(View view){
        editor.clear();
        editor.commit();
        Intent i = new Intent(HomeActivity.this,SplashActivity.class);
        startActivity(i);
        finish();

    }
}
