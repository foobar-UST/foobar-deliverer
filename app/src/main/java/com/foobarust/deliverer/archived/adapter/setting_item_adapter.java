//package com.foobarust.deliverer.archived.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.foobarust.deliverer.R;
//import com.foobarust.deliverer.model.setting_item_model;
//
//import java.util.List;
//
//public class setting_item_adapter extends RecyclerView.Adapter<setting_item_adapter.setting_item_adapter_ViewHolder> {
//
//
//    Context context;
//    List<setting_item_model> setting_item_List;
//
//    RecyclerViewClickListener ClickListener_2;
//
//    public setting_item_adapter(Context context, List<setting_item_model> setting_item_List, setting_item_adapter.RecyclerViewClickListener ClickListener) {
//        this.context = context;
//        this.setting_item_List = setting_item_List;
//        this.ClickListener_2 = (RecyclerViewClickListener) ClickListener;
//    }
//
//    @NonNull
//    @Override
//    public setting_item_adapter_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//        View view = LayoutInflater.from(context).inflate(R.layout.archived_settings_section_item, parent, false);
//
//        return new setting_item_adapter_ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull setting_item_adapter_ViewHolder holder, int position) {
//
//        holder.Image.setImageResource(setting_item_List.get(position).getImageUrl());
//        holder.name.setText(setting_item_List.get(position).getName());
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return setting_item_List.size();
//    }
//
//    public class setting_item_adapter_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
//
//        ImageView Image;
//        TextView name;
//
//        public setting_item_adapter_ViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            Image = itemView.findViewById(R.id.imageView_setting);
//            name = itemView.findViewById(R.id.textView_setting);
//
//            itemView.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View view) {
//            ClickListener_2.onClick(view, getAdapterPosition());
//        }
//    }
//
//    public interface RecyclerViewClickListener{
//        void onClick(View v, int position);
//    }
//}
