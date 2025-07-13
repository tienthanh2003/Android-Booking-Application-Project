package com.example.androidbookingapplicationproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidbookingapplicationproject.R;
import com.example.androidbookingapplicationproject.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> chatList;

    private static final int TYPE_USER = 0;
    private static final int TYPE_ADMIN = 1;

    public ChatAdapter(List<ChatMessage> chatList) {
        this.chatList = chatList;
    }

    // 🔎 Xác định loại người gửi để dùng layout khác nhau
    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatList.get(position);
        if ("staff".equalsIgnoreCase(message.senderRole)) {
            return TYPE_ADMIN;  // Admin/staff gửi → layout admin
        } else {
            return TYPE_USER;   // Customer gửi → layout user
        }
    }

    // 🔧 Tạo ViewHolder với layout tương ứng
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_USER) {
            View view = inflater.inflate(R.layout.item_chat_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_chat_admin, parent, false);
            return new AdminViewHolder(view);
        }
    }

    // 📝 Gán dữ liệu vào từng dòng chat
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatList.get(position);
        if (holder instanceof UserViewHolder) {
            UserViewHolder vh = (UserViewHolder) holder;
            vh.tvSenderName.setText(message.senderName);
            vh.tvMessage.setText(message.message);
            vh.tvTime.setText(message.timestamp);
        } else if (holder instanceof AdminViewHolder) {
            AdminViewHolder vh = (AdminViewHolder) holder;
            vh.tvSenderName.setText(message.senderName);
            vh.tvMessage.setText(message.message);
            vh.tvTime.setText(message.timestamp);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    // 👤 ViewHolder cho user (customer)
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvSenderName, tvMessage, tvTime;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }

    // 👤 ViewHolder cho admin (staff)
    static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView tvSenderName, tvMessage, tvTime;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
