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
import com.example.luxevista.ServiceDetailActivity;
import com.example.luxevista.models.Service;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private Context context;
    private List<Service> serviceList;

    public ServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        
        holder.serviceNameTextView.setText(service.getName());
        holder.serviceCategoryTextView.setText(service.getCategory());
        holder.servicePriceTextView.setText("LKR " + service.getPrice());
        holder.serviceDurationTextView.setText(service.getDuration() + " min");
        
        // Load image using Glide
        if (service.getImageUrl() != null && !service.getImageUrl().isEmpty()) {
            Glide.with(context)
                .load(service.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
//                .error(R.drawable.error_image)
                .into(holder.serviceImageView);
        } else {
            holder.serviceImageView.setImageResource(R.drawable.placeholder_image);
        }
        
        // Set click listener
        holder.serviceCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ServiceDetailActivity.class);
                intent.putExtra("service", service);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public class ServiceViewHolder extends RecyclerView.ViewHolder {
        CardView serviceCardView;
        ImageView serviceImageView;
        TextView serviceNameTextView, serviceCategoryTextView, servicePriceTextView, serviceDurationTextView;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceCardView = itemView.findViewById(R.id.serviceCardView);
            serviceImageView = itemView.findViewById(R.id.serviceImageView);
            serviceNameTextView = itemView.findViewById(R.id.serviceNameTextView);
            serviceCategoryTextView = itemView.findViewById(R.id.serviceCategoryTextView);
            servicePriceTextView = itemView.findViewById(R.id.servicePriceTextView);
            serviceDurationTextView = itemView.findViewById(R.id.serviceDurationTextView);
        }
    }
}
