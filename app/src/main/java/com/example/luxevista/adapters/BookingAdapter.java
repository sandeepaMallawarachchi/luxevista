package com.example.luxevista.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luxevista.R;
import com.example.luxevista.models.Booking;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private Context context;
    private List<Booking> bookingList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public BookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        
        holder.bookingItemNameTextView.setText(booking.getItemName());
        holder.bookingItemTypeTextView.setText(booking.getItemType());
        holder.bookingDateTextView.setText(dateFormat.format(booking.getStartDate()) +  " - " + dateFormat.format(booking.getEndDate()));
        holder.bookingPriceTextView.setText("LKR " + booking.getTotalPrice());
        holder.bookingStatusTextView.setText(booking.getStatus());
        
        // Set status color
        if (booking.getStatus().equals("confirmed")) {
            holder.bookingStatusTextView.setTextColor(context.getResources().getColor(R.color.colorConfirmed));
        } else if (booking.getStatus().equals("pending")) {
            holder.bookingStatusTextView.setTextColor(context.getResources().getColor(R.color.colorPending));
        } else if (booking.getStatus().equals("cancelled")) {
            holder.bookingStatusTextView.setTextColor(context.getResources().getColor(R.color.colorCancelled));
        }
        
        // Set cancel button visibility
        if (booking.getStatus().equals("confirmed") || booking.getStatus().equals("pending")) {
            holder.cancelBookingButton.setVisibility(View.VISIBLE);
        } else {
            holder.cancelBookingButton.setVisibility(View.GONE);
        }
        
        // Set cancel button click listener
        holder.cancelBookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance()
                        .collection("bookings")
                        .document(booking.getId())
                        .delete();
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public class BookingViewHolder extends RecyclerView.ViewHolder {
        CardView bookingCardView;
        TextView bookingItemNameTextView, bookingItemTypeTextView, bookingDateTextView, 
                 bookingPriceTextView, bookingStatusTextView;
        Button cancelBookingButton;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingCardView = itemView.findViewById(R.id.bookingCardView);
            bookingItemNameTextView = itemView.findViewById(R.id.bookingItemNameTextView);
            bookingItemTypeTextView = itemView.findViewById(R.id.bookingItemTypeTextView);
            bookingDateTextView = itemView.findViewById(R.id.bookingDateTextView);
            bookingPriceTextView = itemView.findViewById(R.id.bookingPriceTextView);
            bookingStatusTextView = itemView.findViewById(R.id.bookingStatusTextView);
            cancelBookingButton = itemView.findViewById(R.id.cancelBookingButton);
        }
    }
}
