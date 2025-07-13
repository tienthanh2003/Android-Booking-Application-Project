package com.example.androidbookingapplicationproject.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.androidbookingapplicationproject.R;
import com.example.androidbookingapplicationproject.models.UserDisplay;

import java.util.List;

public class UserListAdapter extends ArrayAdapter<UserDisplay> {

    private final Activity context;
    private final List<UserDisplay> users;

    public UserListAdapter(Activity context, List<UserDisplay> users) {
        super(context, R.layout.item_chat_user, users);
        this.context = context;
        this.users = users;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserDisplay user = users.get(position);
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        ((TextView) view.findViewById(android.R.id.text1)).setText(user.userName );
        return view;
    }
}
