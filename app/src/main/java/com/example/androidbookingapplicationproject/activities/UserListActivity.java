package com.example.androidbookingapplicationproject.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidbookingapplicationproject.R;
import com.example.androidbookingapplicationproject.adapters.UserListAdapter;
import com.example.androidbookingapplicationproject.db.DatabaseHelper;
import com.example.androidbookingapplicationproject.models.UserDisplay;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private ListView listViewUsers;
    private List<UserDisplay> userList = new ArrayList<>();
    private UserListAdapter adapter;
    private DatabaseReference conversationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        listViewUsers = findViewById(R.id.listViewUsers);
        adapter = new UserListAdapter(this, userList);
        listViewUsers.setAdapter(adapter);

        // ✅ Chỉ hiển thị user có role là "customer" từ SQLite
        conversationsRef = FirebaseDatabase.getInstance()
                .getReference("Messages/conversations");

        conversationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot conversation : snapshot.getChildren()) {
                    String key = conversation.getKey(); // ví dụ: user_12
                    if (key != null && key.startsWith("user_")) {
                        try {
                            int userId = Integer.parseInt(key.split("_")[1]);
                            String name = getCustomerNameFromSQLite(userId); // 🔥 lọc role = customer
                            if (name != null) {
                                userList.add(new UserDisplay(userId, name));
                            }
                        } catch (Exception e) {
                            Log.e("UserList", "Lỗi xử lý userId", e);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(UserListActivity.this, "Lỗi tải danh sách", Toast.LENGTH_SHORT).show();
            }
        });

        // ✅ Khi chọn user → mở AdminChatActivity
        listViewUsers.setOnItemClickListener((parent, view, position, id) -> {
            UserDisplay selected = userList.get(position);
            Intent intent = new Intent(UserListActivity.this, AdminChatActivity.class);
            intent.putExtra("userId", selected.userId);
            intent.putExtra("userName", selected.userName);
            startActivity(intent);
        });
    }

    /**
     * 🔍 Truy vấn tên người dùng nếu và chỉ nếu Role = 'customer'
     * Trả về null nếu không đúng role.
     */
    private String getCustomerNameFromSQLite(int userId) {
        String name = null;
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.createDatabase();
            SQLiteDatabase db = dbHelper.openDatabase();

            Cursor cursor = db.rawQuery(
                    "SELECT Name FROM User\n" +
                            "WHERE UserId = ?\n" +
                            "  AND LOWER(TRIM(Role)) = 'customer';\n",
                    new String[]{String.valueOf(userId)}
            );

            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e("UserList", "Lỗi đọc SQLite", e);
        }
        return name;
    }
}
