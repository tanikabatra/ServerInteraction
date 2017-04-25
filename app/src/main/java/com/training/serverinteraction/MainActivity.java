package com.training.serverinteraction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    @InjectView(R.id.editTextName)
    EditText eTxtName ;

    @InjectView(R.id.editTextPhone)
    EditText eTxtPhone;

    @InjectView(R.id.editTextEmail)
    EditText eTxtEmail;

    @InjectView(R.id.radioButtonMale)
    RadioButton rbMale;

    @InjectView(R.id.radioButtonFemale)
    RadioButton rbFemale;

    @InjectView(R.id.spinner)
    Spinner spinnerCity;
    ArrayAdapter<String> adapter;

    @InjectView(R.id.buttonSubmit)
    Button btnSubmit;

    Student student, rcvStudent;

    boolean updateMode;

    RequestQueue requestQueue;

    ProgressDialog progressDialog;

    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        student = new Student();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
        adapter.add("--Select City--");
        adapter.add("Ludhiana");
        adapter.add("Chandigarh");
        adapter.add("Delhi");
        adapter.add("Bangalore");
        adapter.add("California");

        spinnerCity.setAdapter(adapter);

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    student.setCity(adapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        rbMale.setOnCheckedChangeListener(this);
        rbFemale.setOnCheckedChangeListener(this);

        requestQueue = Volley.newRequestQueue(this);

        Intent rcv = getIntent();
        updateMode = rcv.hasExtra("keyStudent");


        if (updateMode) {
            rcvStudent = (Student) rcv.getSerializableExtra("keyStudent");
            eTxtName.setText(rcvStudent.getName());
            eTxtPhone.setText(rcvStudent.getPhone());
            eTxtEmail.setText(rcvStudent.getEmail());


            if (rcvStudent.getGender().equals("Male")) {
                rbMale.setChecked(true);
            } else {
                rbFemale.setChecked(true);
            }

            int p = 0;
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).equals(rcvStudent.getCity())) {
                    p = i;
                    break;
                }
            }

            spinnerCity.setSelection(p);

            btnSubmit.setText("Update");
        }


            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please Wait..");
            progressDialog.setCancelable(false);



    }


    public void clickHandler(View view){

        student.setName(eTxtName.getText().toString().trim());
        student.setPhone(eTxtPhone.getText().toString().trim());
        student.setEmail(eTxtEmail.getText().toString().trim());


        if (validateFields()){
            if (isNetworkConnected()){
                insertIntoCloud();

                if (updateMode){
                    Intent data = new Intent();
                    data.putExtra("keyStudent",student);
                    setResult(201,data);
                }
            }else {
                Toast.makeText(this,"Please Connect to Internet",Toast.LENGTH_LONG).show();

            }

        }else {
            Toast.makeText(this,"Please Correct your Input",Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();

        if (isChecked){
            if (id==R.id.radioButtonMale)
                student.setGender("Male");
            else
                student.setGender("Female");
        }

    }


    void insertIntoCloud(){

        String url;

        if (!updateMode){
            url = Util.INSERT_STUDENT_PHP;
        }else {
            url= Util.Update_STUDENT_PHP;
        }

        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("success");
                    String message = jsonObject.getString("message");
                    Toast.makeText(MainActivity.this,message ,Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

                    if (success==1){
                        finish();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"Some Exception: "+ e.getMessage() ,Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

                }


            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                Toast.makeText(MainActivity.this,"Some Error: "+ error.getMessage() ,Toast.LENGTH_LONG).show();


            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                if (updateMode)
                    map.put("id",String.valueOf(rcvStudent.getId()));

                map.put("name1",student.getName());
                map.put("phone",student.getPhone());
                map.put("email",student.getEmail());
                map.put("gender",student.getGender());
                map.put("city",student.getCity());

                return map;
            }
        };


        requestQueue.add(request);
        clearFields();
    }

    void clearFields(){
        eTxtName.setText("");
        eTxtEmail.setText("");
        eTxtPhone.setText("");
        spinnerCity.setSelection(0);
        rbMale.setChecked(false);
        rbFemale.setChecked(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0,101,0,"All Students");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==101){
            Intent i = new Intent(MainActivity.this,AllStudentsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    boolean isNetworkConnected(){

        connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();


        return (networkInfo!=null && networkInfo.isConnected());

    }



    boolean validateFields(){
        boolean flag = true;

        if (student.getName().isEmpty()){
            flag = false;
            eTxtName.setError("Please Enter Name");
        }
        if (student.getPhone().isEmpty()){
            flag = false;
            eTxtPhone.setError("Please Enter Phone");
        }else {
            if (student.getPhone().length()<10){
                flag = false;
                eTxtPhone.setError("Please Enter 10 digit phone number");
            }
        }
        if (student.getEmail().isEmpty()){
            flag = false;
            eTxtEmail.setError("Please Enter Email");
        }else {
            if (!(student.getEmail().contains("@")&& student.getEmail().contains("."))){
                flag = false;
                eTxtEmail.setError("Please Enter the correct Email");
            }
        }

        return flag;
    }

}
