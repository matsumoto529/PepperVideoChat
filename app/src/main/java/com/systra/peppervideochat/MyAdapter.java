package com.systra.peppervideochat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private String[] displayName;
    private String[] peerId;
    private String email;
    private String pass;
    private int count;

    // 各データ項目のビューへの参照を提供します
    // 複雑なデータアイテムでは、アイテムごとに複数のビューが必要になる場合があります。
    // ビューホルダー内のデータアイテムのすべてのビューへのアクセスを提供します
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // この場合、各データ項目は単なる文字列です
        public TextView disName;
        public MyViewHolder(View v) {
            super(v);
            disName = v.findViewById(R.id.disName);
        }
    }

    // 適切なコンストラクターを提供します（データセットの種類によって異なります）
    public MyAdapter(String[] _displayName, String[] _peerId, String _email, String _pass, int _count) {
        displayName = _displayName;
        peerId = _peerId;
        email = _email;
        pass = _pass;
        count = _count;
    }

    // 新しいビューを作成する（レイアウトマネージャーによって呼び出される）
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        // 新しいビューを作成する
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_text_view, parent, false);

        MyViewHolder vh = new MyViewHolder(view);
        view.setOnClickListener(v -> {
            final int position = vh.getAdapterPosition();
            String peer = peerId[position];
            Context context = v.getContext();

            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("PEERID", peer);
            intent.putExtra("EMAIL", email);
            intent.putExtra("PASS", pass);
            context.startActivity(intent);
            GetGroupInfRequest.AppFinish();
        });
        return vh;
    }

    // ビューのコンテンツを置き換えます（レイアウトマネージャによって呼び出されます）
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // - この位置でデータセットから要素を取得します
        // - ビューのコンテンツをその要素に置き換えます
        holder.disName.setText(displayName[position]);
    }

    // データセットのサイズを返します（レイアウトマネージャーによって呼び出されます）
    @Override
    public int getItemCount() {
        return count;
    }
}
