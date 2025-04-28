package com.example.luxevista.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.luxevista.FirebaseManager;
import com.example.luxevista.R;
import com.example.luxevista.models.Room;
import com.example.luxevista.AddRoomActivity;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class AdminRoomAdapter extends RecyclerView.Adapter<AdminRoomAdapter.RoomViewHolder> {

    private List<Room> roomList;
    private Context context;

    public AdminRoomAdapter(List<Room> roomList, Context context) {
        this.roomList = roomList;
        this.context = context;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.bind(room);
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    class RoomViewHolder extends RecyclerView.ViewHolder {
        private ImageView roomImageView;
        private TextView roomNameTextView;
        private TextView roomTypeTextView;
        private TextView roomPriceTextView;
        private ImageView editButton;
        private ImageView deleteButton;

        RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomImageView = itemView.findViewById(R.id.roomImageView);
            roomNameTextView = itemView.findViewById(R.id.roomNameTextView);
            roomTypeTextView = itemView.findViewById(R.id.roomTypeTextView);
            roomPriceTextView = itemView.findViewById(R.id.roomPriceTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        void bind(Room room) {
            roomNameTextView.setText(room.getName());
            roomTypeTextView.setText(room.getType());
            roomPriceTextView.setText("LKR " + room.getPricePerNight() + " / night");

            if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
                Glide.with(context)
                    .load(room.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(roomImageView);
            } else {
                roomImageView.setImageResource(R.drawable.placeholder_image);
            }

            editButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, AddRoomActivity.class);
                intent.putExtra("room", room);
                context.startActivity(intent);
            });

            deleteButton.setOnClickListener(v -> {
                DatabaseReference roomRef = FirebaseManager.getInstance().getRoomsReference().child(room.getId());
                roomRef.removeValue()
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Room deleted successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete room", Toast.LENGTH_SHORT).show());
            });
        }
    }
} 