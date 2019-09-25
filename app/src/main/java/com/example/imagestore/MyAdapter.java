package com.example.imagestore;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private Context context;
    private List<Upload> uploadList;
    private OnItemClickListener listener;

    public MyAdapter(Context context, List<Upload> uploadList) {
        this.context = context;
        this.uploadList = uploadList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Upload upload = uploadList.get(position);
        holder.textView.setText(upload.getImageName());
        Picasso.with(context)
                .load(upload.getImageUrl())
                .placeholder(R.mipmap.ic_launcher_round) //default image
                .fit()
                .centerCrop()
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return uploadList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        TextView textView;
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewID);
            imageView = itemView.findViewById(R.id.imageViewID);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if(listener!=null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    listener.onItemClick(position);
                }
            }
        }

        @Override //this method for create a menu
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Choose Action");
            MenuItem doAnyTask = menu.add(Menu.NONE,1,1,"Do Any Task");
            MenuItem delete = menu.add(Menu.NONE,2,2,"Delete");

            doAnyTask.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override //this method for item clickable
        public boolean onMenuItemClick(MenuItem item) {
            if(listener!=null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    switch (item.getItemId()){
                        case 1:
                            listener.onDoAnyTask(position);
                            return true;
                        case 2:
                            listener.onDelete(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }


    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDoAnyTask(int position);
        void onDelete(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
