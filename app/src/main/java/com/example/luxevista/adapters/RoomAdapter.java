package com.example.luxevista.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.luxevista.R;
import com.example.luxevista.RoomDetailActivity;
import com.example.luxevista.models.Room;
import android.widget.Toast;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private Context context;
    private List<Room> roomList;

    public RoomAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);

        holder.roomNameTextView.setText(room.getName());
        holder.roomTypeTextView.setText(room.getType());

        // Show price aligned to right
        holder.roomPriceTextView.setText("LKR " + room.getPricePerNight() + " / night");
        holder.roomPriceTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

        // Show availability and occupancy
        String occupancyInfo = "Max " + room.getMaxOccupancy() + " Guests";
        holder.roomAvailabilityTextView.setText(occupancyInfo);

        if (room.isHasOceanView()) {
            holder.oceanViewTextView.setVisibility(View.VISIBLE);
        } else {
            holder.oceanViewTextView.setVisibility(View.GONE);
        }

        if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(room.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.roomImageView);
        } else {
            holder.roomImageView.setImageResource(R.drawable.placeholder_image);
        }

        holder.roomCardView.setOnClickListener(v -> {
            if (room.getId() == null || room.getId().isEmpty()) {
                Toast.makeText(context, "Missing room ID", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(context, RoomDetailActivity.class);
            intent.putExtra("room", room);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder {
        CardView roomCardView;
        ImageView roomImageView;
        TextView roomNameTextView, roomTypeTextView, roomPriceTextView, oceanViewTextView, roomAvailabilityTextView;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomCardView = itemView.findViewById(R.id.roomCardView);
            roomImageView = itemView.findViewById(R.id.roomImageView);
            roomNameTextView = itemView.findViewById(R.id.roomNameTextView);
            roomTypeTextView = itemView.findViewById(R.id.roomTypeTextView);
            roomPriceTextView = itemView.findViewById(R.id.roomPriceTextView);
            oceanViewTextView = itemView.findViewById(R.id.oceanViewTextView);
            roomAvailabilityTextView = itemView.findViewById(R.id.roomAvailabilityTextView);
        }
    }
}
