package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ecommerce.Admin.AdminNewOrdersActivity;
import com.example.ecommerce.Admin.AdminUserProductsActivity;
import com.example.ecommerce.Model.History;
import com.example.ecommerce.Model.History;
import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView historyList;
    private DatabaseReference historyRef;
    RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyList = findViewById(R.id.history_list);
        layoutManager = new LinearLayoutManager(this);
        historyList.setLayoutManager(layoutManager);


        historyRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<History> options=
                new FirebaseRecyclerOptions.Builder<History>()
                .setQuery(historyRef.orderByChild("user").equalTo(Prevalent.currentOnlineUser.getUsername()), History.class)
                .build();

        FirebaseRecyclerAdapter<History, HistoryViewHolder> adapter = new FirebaseRecyclerAdapter<History, HistoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HistoryViewHolder holder, int position, @NonNull History model) {
                holder.historyId.setText("Order ID: " + model.getOid());
                holder.historyName.setText(model.getName());
                holder.historyPhoneNumber.setText(model.getPhone());
                holder.historyTotalPrice.setText("Total Amount: Rp." + model.getTotalAmount());
                holder.historyDateTime.setText("Orders at: " + model.getDate() + " " + model.getTime());
                holder.historyShippingAddress.setText("Shipping Address: " + model.getAddress() + ", " + model.getCity() + ", " + model.getProvince());
                holder.historyState.setText("State: " + model.getState());

                holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HistoryActivity.this, HistoryProductsActivity.class);
                        intent.putExtra("historyID", model.getOid());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_layout, parent, false);
                HistoryViewHolder holder = new HistoryViewHolder(view);
                return holder;
            }
        };

        historyList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder{
        public TextView historyTotalPrice, historyDateTime, historyShippingAddress, historyId, historyName, historyPhoneNumber, historyState;
        public Button showOrdersBtn;

        public HistoryViewHolder(View itemView){
            super(itemView);

            historyTotalPrice = itemView.findViewById(R.id.history_total_price);
            historyDateTime = itemView.findViewById(R.id.history_date_time);
            historyShippingAddress = itemView.findViewById(R.id.history_address_city_province);
            showOrdersBtn = itemView.findViewById(R.id.history_all_products_btn);
            historyId = itemView.findViewById(R.id.history_id);
            historyName = itemView.findViewById(R.id.history_name);
            historyPhoneNumber = itemView.findViewById(R.id.history_phone_number);
            historyState = itemView.findViewById(R.id.history_state);
        }
    }
}