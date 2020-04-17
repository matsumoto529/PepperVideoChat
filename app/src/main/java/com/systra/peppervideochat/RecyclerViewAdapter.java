package com.systra.peppervideochat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "onClick";

    private String[] viewName;
    private String[] peerId;
    private int cntName;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvViewName;
        final LinearLayout linearLayout;

        ViewHolder(View view) {
            super(view);
            tvViewName = view.findViewById(R.id.opName);
            linearLayout = itemView.findViewById(R.id.linear_layout);
        }
    }

    RecyclerViewAdapter(String[] tvViewName) {
        viewName = tvViewName;
//        cntName = Item;
    }

    // Layoutの設定
    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_recycler_view, parent, false);

        final ViewHolder holder = new ViewHolder(view);

        view.setOnClickListener(v -> {
            final int position = holder.getAdapterPosition();
            String peer = peerId[position];
            Log.d(TAG, peer);
            Context context = v.getContext();
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("PeerID", peer);
            context.startActivity(intent);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvViewName.setText(viewName[position]);
    }

    @Override
    public int getItemCount() {
        return cntName;
    }
}
