package com.example.luxevista.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luxevista.R;
import com.example.luxevista.models.Booking;
import com.example.luxevista.AdminBookingActivity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;
    private Context context;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public AdminBookingAdapter(List<Booking> bookingList, Context context) {
        this.bookingList = bookingList;
        this.context = context;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        private TextView itemNameTextView;
        private TextView bookingTypeTextView;
        private TextView checkInDateTextView;
        private TextView checkOutDateTextView;
        private TextView totalPriceTextView;
        private TextView statusTextView;
        private Button approveButton;
        private Button rejectButton;

        BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            bookingTypeTextView = itemView.findViewById(R.id.bookingTypeTextView);
            checkInDateTextView = itemView.findViewById(R.id.checkInDateTextView);
            checkOutDateTextView = itemView.findViewById(R.id.checkOutDateTextView);
            totalPriceTextView = itemView.findViewById(R.id.totalPriceTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }

        void bind(Booking booking) {
            itemNameTextView.setText(booking.getItemName());
            bookingTypeTextView.setText(booking.getItemType());
            checkInDateTextView.setText(dateFormat.format(booking.getStartDate()));
            checkOutDateTextView.setText(dateFormat.format(booking.getEndDate()));
            totalPriceTextView.setText("LKR " + booking.getTotalPrice());
            statusTextView.setText(booking.getStatus());

            // Show/hide buttons based on current status
            if (booking.getStatus().equals("pending")) {
                approveButton.setVisibility(View.VISIBLE);
                rejectButton.setVisibility(View.VISIBLE);
                statusTextView.setVisibility(View.GONE);
            } else {
                approveButton.setVisibility(View.GONE);
                rejectButton.setVisibility(View.GONE);
                statusTextView.setVisibility(View.VISIBLE);
            }

            approveButton.setOnClickListener(v -> {
                ((AdminBookingActivity) context).updateBookingStatus(booking.getId(), "approved");
            });

            rejectButton.setOnClickListener(v -> {
                ((AdminBookingActivity) context).updateBookingStatus(booking.getId(), "rejected");
            });
        }
    }
} 