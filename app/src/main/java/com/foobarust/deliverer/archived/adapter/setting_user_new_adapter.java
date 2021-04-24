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
//import com.foobarust.deliverer.model.setting_user_new_model;
//
//import java.util.List;
//
//public class setting_user_new_adapter extends RecyclerView.Adapter<setting_user_new_adapter.setting_user_new_adapter_ViewHolder>{
//
//    Context context;
//    List<setting_user_new_model> setting_user_new_List;
//
//    RecyclerViewClickListener ClickListener_1;
//
//    public setting_user_new_adapter(Context context, List<setting_user_new_model> setting_user_new_List, RecyclerViewClickListener ClickListener) {
//        this.context = context;
//        this.setting_user_new_List = setting_user_new_List;
//        this.ClickListener_1 = ClickListener;
//    }
//
//    @NonNull
//    @Override
//    public setting_user_new_adapter.setting_user_new_adapter_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//        View view = LayoutInflater.from(context).inflate(R.layout.archived_user_account_new_item, parent, false);
//
//        return new setting_user_new_adapter_ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull setting_user_new_adapter.setting_user_new_adapter_ViewHolder holder, int position) {
//        holder.Image.setImageResource(setting_user_new_List.get(position).getImageUrl());
//        holder.name.setText(setting_user_new_List.get(position).getName());
//
//        holder.description.setText(setting_user_new_List.get(position).getDescription());
//        holder.arrow_forward_image.setImageResource(setting_user_new_List.get(position).getArrow_forward_image());
//    }
//
//    @Override
//    public int getItemCount() {
//        return setting_user_new_List.size();
//    }
//
//    public class setting_user_new_adapter_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
//
//        ImageView Image;
//        TextView name;
//
//        TextView description;
//        ImageView arrow_forward_image;
//
//        public setting_user_new_adapter_ViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            Image = itemView.findViewById(R.id.avatar_image_view_new_setting);
//            name = itemView.findViewById(R.id.username_text_view_new_setting);
//
//            description = itemView.findViewById(R.id.description_text_new_view);
//            arrow_forward_image = itemView.findViewById(R.id.arrow_forward_image_view_new_setting);
//
//            itemView.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View view) {
//            ClickListener_1.onClick(view, getAdapterPosition());
//        }
//    }
//
//    public interface RecyclerViewClickListener{
//        void onClick(View v, int position);
//    }
//}
