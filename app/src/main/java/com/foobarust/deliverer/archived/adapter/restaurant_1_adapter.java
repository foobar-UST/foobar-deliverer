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
//import com.foobarust.deliverer.model.restaurant_1;
//
//import java.util.List;
//
//public class restaurant_1_adapter extends RecyclerView.Adapter<restaurant_1_adapter.restaurant_1_adapter_ViewHolder> {
//
//    Context context;
//    List<restaurant_1> restaurant_1_list;
//
//    RecyclerViewClickListener ClickListener;
//
//
//    public restaurant_1_adapter(Context context, List<restaurant_1> restaurant_1_list, RecyclerViewClickListener ClickListener){
//        this.context = context;
//        this.restaurant_1_list = restaurant_1_list;
//        this.ClickListener = ClickListener;
//    }
//
//    @NonNull
//    @Override
//    public restaurant_1_adapter_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//        View view = LayoutInflater.from(context).inflate(R.layout.archived_restaurant_1_scrolling_item, parent, false);
//
//        return new restaurant_1_adapter_ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull restaurant_1_adapter_ViewHolder holder, int position) {
//
//        holder.rest_image.setImageResource(restaurant_1_list.get(position).getRest_image());
//        holder.lunch_order.setText(restaurant_1_list.get(position).getLunch_order());
//        holder.order_time.setText(restaurant_1_list.get(position).getOrder_time());
//        holder.user_number.setText(restaurant_1_list.get(position).getUser_number());
//    }
//
//    @Override
//    public int getItemCount() {
//        return restaurant_1_list.size();
//    }
//
//    public class restaurant_1_adapter_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//
//        ImageView rest_image;
//
//        TextView lunch_order;
//        TextView order_time;
//        TextView user_number;
//
//        public restaurant_1_adapter_ViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            rest_image = itemView.findViewById(R.id.rest_square_image);
//
//            lunch_order = itemView.findViewById(R.id.lunch_order_text);
//            order_time = itemView.findViewById(R.id.order_time_text);
//            user_number = itemView.findViewById(R.id.user_number_text);
//
//            itemView.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View view) {
//            ClickListener.onClick(view, getAdapterPosition());
//        }
//    }
//
//    public interface RecyclerViewClickListener{
//        void onClick(View v, int position);
//    }
//}
