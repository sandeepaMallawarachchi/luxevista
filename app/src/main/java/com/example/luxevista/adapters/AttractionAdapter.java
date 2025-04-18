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
import com.example.luxevista.AttractionDetailActivity;
import com.example.luxevista.R;
import com.example.luxevista.models.Attraction;

import java.util.List;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder> {

    private Context context;
    private List<Attraction> attractionList;

    public AttractionAdapter(Context context, List<Attraction> attractionList) {
        this.context = context;
        this.attractionList = attractionList;
    }

    @NonNull
    @Override
    public AttractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attraction, parent, false);
        return new AttractionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttractionViewHolder holder, int position) {
        Attraction attraction = attractionList.get(position);
        
        holder.attractionNameTextView.setText(attraction.getName());
        holder.attractionCategoryTextView.setText(attraction.getCategory());
        holder.attractionDistanceTextView.setText(attraction.getDistance() + " km away");
        
        if (attraction.isRecommended()) {
            holder.recommendedTextView.setVisibility(View.VISIBLE);
        } else {
            holder.recommendedTextView.setVisibility(View.GONE);
        }
        
        // Load image using Glide
        if (attraction.getImageUrl() != null && !attraction.getImageUrl().isEmpty()) {
            Glide.with(context)
                .load(attraction.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
//                .error(R.drawable.error_image)
                .into(holder.attractionImageView);
        } else {
            holder.attractionImageView.setImageResource(R.drawable.placeholder_image);
        }
        
        // Set click listener
        holder.attractionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AttractionDetailActivity.class);
                intent.putExtra("attraction", attraction);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return attractionList.size();
    }

    public class AttractionViewHolder extends RecyclerView.ViewHolder {
        CardView attractionCardView;
        ImageView attractionImageView;
        TextView attractionNameTextView, attractionCategoryTextView, attractionDistanceTextView, recommendedTextView;

        public AttractionViewHolder(@NonNull View itemView) {
            super(itemView);
            attractionCardView = itemView.findViewById(R.id.attractionCardView);
            attractionImageView = itemView.findViewById(R.id.attractionImageView);
            attractionNameTextView = itemView.findViewById(R.id.attractionNameTextView);
            attractionCategoryTextView = itemView.findViewById(R.id.attractionCategoryTextView);
            attractionDistanceTextView = itemView.findViewById(R.id.attractionDistanceTextView);
            recommendedTextView = itemView.findViewById(R.id.recommendedTextView);
        }
    }
}
