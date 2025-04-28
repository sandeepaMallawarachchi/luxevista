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
import com.example.luxevista.R;
import com.example.luxevista.models.Attraction;
import com.example.luxevista.AddAttractionActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminAttractionAdapter extends RecyclerView.Adapter<AdminAttractionAdapter.AttractionViewHolder> {
    private List<Attraction> attractionList;
    private Context context;

    public AdminAttractionAdapter(List<Attraction> attractionList, Context context) {
        this.attractionList = attractionList;
        this.context = context;
    }

    @NonNull
    @Override
    public AttractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_attraction, parent, false);
        return new AttractionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttractionViewHolder holder, int position) {
        Attraction attraction = attractionList.get(position);
        holder.bind(attraction);
    }

    @Override
    public int getItemCount() {
        return attractionList.size();
    }

    class AttractionViewHolder extends RecyclerView.ViewHolder {
        private ImageView attractionImageView;
        private TextView attractionNameTextView;
        private TextView attractionCategoryTextView;
        private TextView attractionDistanceTextView;
        private ImageView editButton;
        private ImageView deleteButton;

        AttractionViewHolder(@NonNull View itemView) {
            super(itemView);
            attractionImageView = itemView.findViewById(R.id.attractionImageView);
            attractionNameTextView = itemView.findViewById(R.id.attractionNameTextView);
            attractionCategoryTextView = itemView.findViewById(R.id.attractionCategoryTextView);
            attractionDistanceTextView = itemView.findViewById(R.id.attractionDistanceTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        void bind(Attraction attraction) {
            attractionNameTextView.setText(attraction.getName());
            attractionCategoryTextView.setText(attraction.getCategory());
            attractionDistanceTextView.setText(attraction.getDistance() + " km");

            if (attraction.getImageUrl() != null && !attraction.getImageUrl().isEmpty()) {
                Glide.with(context)
                    .load(attraction.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(attractionImageView);
            } else {
                attractionImageView.setImageResource(R.drawable.placeholder_image);
            }

            editButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, AddAttractionActivity.class);
                intent.putExtra("attraction", attraction);
                context.startActivity(intent);
            });

            deleteButton.setOnClickListener(v -> {
                FirebaseFirestore.getInstance().collection("attractions").document(attraction.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Attraction deleted successfully", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete attraction", Toast.LENGTH_SHORT).show());
            });
        }
    }
} 