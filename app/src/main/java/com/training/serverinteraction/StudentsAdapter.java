package com.training.serverinteraction;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by GOD on 4/13/2017.
 */

public class StudentsAdapter extends ArrayAdapter<Student> {

    Context context;
    int resource;
    ArrayList<Student> studentList,tempList;


    public StudentsAdapter(Context context, int resource, ArrayList<Student> objects) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
        studentList = objects;
        tempList = new ArrayList<>();
        tempList.addAll(studentList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = null;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(resource,parent,false);

        TextView txtName = (TextView)view.findViewById(R.id.textViewName);
        TextView txtGender = (TextView)view.findViewById(R.id.textViewGender);

        Student student = studentList.get(position);
        txtName.setText(student.getName());
        txtGender.setText(student.getGender());

        return view;
    }

    public void filter(String str){

        studentList.clear();

        if (str.length()==0){
            studentList.addAll(tempList);
        }
        else {
            for (Student s : tempList){
                if (s.getName().toLowerCase().contains(str.toLowerCase())){
                    studentList.add(s);
                }
            }
        }
        notifyDataSetChanged();

    }
}
