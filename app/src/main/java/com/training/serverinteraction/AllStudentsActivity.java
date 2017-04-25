package com.training.serverinteraction;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.text.TextWatcher;


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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AllStudentsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @InjectView(R.id.listView)
    ListView listView;

    @InjectView(R.id.editTextSearch)
    EditText eTxtSearch;

    ArrayList<Student> studentList;

    Student student;

    RequestQueue requestQueue;

    ProgressDialog progressDialog;

    StudentsAdapter adapter;

    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_students);

        ButterKnife.inject(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        requestQueue = Volley.newRequestQueue(this);


        eTxtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = charSequence.toString();
                if(adapter!=null){
                    adapter.filter(str);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        retrieveFromCloud();


    }


    void retrieveFromCloud(){
        progressDialog.show();

        studentList = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Util.Retrieve_STUDENT_PHP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("students");


                    int id=0;
                    String n="",p="",e="",g="",c="";
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jObj = jsonArray.getJSONObject(i);

                        id = jObj.getInt("id");
                        n = jObj.getString("name");
                        p = jObj.getString("phone");
                        e = jObj.getString("email");
                        g = jObj.getString("gender");
                        c = jObj.getString("city");

                        studentList.add(new Student(id,n,p,e,g,c));
                    }
                    adapter = new StudentsAdapter(AllStudentsActivity.this,R.layout.list_item,studentList);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(AllStudentsActivity.this);



                    progressDialog.dismiss();
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(AllStudentsActivity.this,"SomeException: " + e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(AllStudentsActivity.this,"Some Error",Toast.LENGTH_LONG).show();

            }
        });



       requestQueue.add(stringRequest);

    }





    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        pos = position;
        student = studentList.get(position);
        Log.i("hey",student.toString());
        showOptions();

    }

    void showOptions(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items ={"View","Update","Delete"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch (i){
                    case 0:
                        showStudent();
                        break;

                    case 1:
                        Intent intent = new Intent(AllStudentsActivity.this,MainActivity.class);
                        intent.putExtra("keyStudent",student);
                        startActivityForResult(intent,101);
                        break;

                    case 2:
                        deleteStudent();
                        break;
                }

            }
        });

        builder.create().show();

    }

    void showStudent(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Details of "+student.getName());
        builder.setMessage(student.toString());
        builder.setPositiveButton("Done",null);
        builder.create().show();
    }

    void deleteStudent(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete "+ student.getName());
        builder.setMessage("Do you wish to Delete?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteFromCloud();

            }
        });
        builder.setNegativeButton("Cancel",null);
        builder.create().show();
    }

    void deleteFromCloud(){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Util.Delete_STUDENT_PHP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("success");
                    String message = jsonObject.getString("message");

                    if (success==1){
                        studentList.remove(pos);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(AllStudentsActivity.this,message,Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }else {
                        Toast.makeText(AllStudentsActivity.this,message,Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AllStudentsActivity.this,"Some Exception: " + e.getMessage(),Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AllStudentsActivity.this,"Some Volley Error: " + error.getMessage(),Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("id",String.valueOf(student.getId()));
                return map;
            }
        }
        ;
        requestQueue.add(stringRequest);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && resultCode == 201) {
            Student st = (Student) data.getSerializableExtra("keyStudent");
            studentList.set(st.getId(),st);
            adapter.notifyDataSetChanged();
        }
    }

}
