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
import com.example.luxevista.models.Service;
import com.example.luxevista.AddServiceActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminServiceAdapter extends RecyclerView.Adapter<AdminServiceAdapter.ServiceViewHolder> {
    private List<Service> serviceList;
    private Context context;

    public AdminServiceAdapter(List<Service> serviceList, Context context) {
        this.serviceList = serviceList;
        this.context = context;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.bind(service);
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    class ServiceViewHolder extends RecyclerView.ViewHolder {
        private ImageView serviceImageView;
        private TextView serviceNameTextView;
        private TextView serviceCategoryTextView;
        private TextView servicePriceTextView;
        private ImageView editButton;
        private ImageView deleteButton;

        ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceImageView = itemView.findViewById(R.id.serviceImageView);
            serviceNameTextView = itemView.findViewById(R.id.serviceNameTextView);
            serviceCategoryTextView = itemView.findViewById(R.id.serviceCategoryTextView);
            servicePriceTextView = itemView.findViewById(R.id.servicePriceTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        void bind(Service service) {
            serviceNameTextView.setText(service.getName());
            serviceCategoryTextView.setText(service.getCategory());
            servicePriceTextView.setText("LKR " + service.getPrice());

            if (service.getImageUrl() != null && !service.getImageUrl().isEmpty()) {
                Glide.with(context)
                    .load(service.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(serviceImageView);
            } else {
                serviceImageView.setImageResource(R.drawable.placeholder_image);
            }

            editButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, AddServiceActivity.class);
                intent.putExtra("service", service);
                context.startActivity(intent);
            });

            deleteButton.setOnClickListener(v -> {
                FirebaseFirestore.getInstance().collection("services").document(service.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Service deleted successfully", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete service", Toast.LENGTH_SHORT).show());
            });
        }
    }
} 