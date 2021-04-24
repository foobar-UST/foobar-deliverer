//package com.foobarust.deliverer.adapter;
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
//import com.bumptech.glide.Glide;
//import com.foobarust.deliverer.R;
//import com.foobarust.deliverer.model.rest_order_sections_basic_model;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.TimeZone;
//
//
//public class rest_order_sections_basic_adapter extends RecyclerView.Adapter<rest_order_sections_basic_adapter.rest_order_sections_basic_adapter_ViewHolder>{
//
//    Context context;
//    ArrayList<rest_order_sections_basic_model> list;
//
//    rest_order_sections_basic_adapter.RecyclerViewClickListener ClickListener;
//
//    public rest_order_sections_basic_adapter(Context context, ArrayList<rest_order_sections_basic_model> list, rest_order_sections_basic_adapter.RecyclerViewClickListener clickListener) {
//        this.context = context;
//        this.list = list;
//        ClickListener = clickListener;
//    }
//
//    @NonNull
//    @Override
//    public rest_order_sections_basic_adapter_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//        View view = LayoutInflater.from(context).inflate(R.layout.archived_restaurant_1_scrolling_item, parent, false);
//        return new rest_order_sections_basic_adapter_ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull rest_order_sections_basic_adapter_ViewHolder holder, int position) {
//
//        String id = list.get(position).getId();
//        View.OnClickListener listener = new View.OnClickListener() {
//            public void onClick(View v) {
//                ClickListener.onClick(id);
//            }
//        };
//
//        holder.itemView.setOnClickListener(listener);
//
//
//        String urlString = list.get(position).getImage_url();
//        Glide.with(context)
//                .load(urlString)
//                .centerCrop()
//                .into(holder.rest_square);
//        //holder.rest_square.setImageResource(list.get(position).getImage_url());
//
//
//        holder.lunch_order.setText(list.get(position).getTitle());
//
//
//        Date time_d = new Date(list.get(position).getDelivery_time().getTime());
//        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
//        sdFormat.setTimeZone(TimeZone.getTimeZone("Asia/Hong_Kong"));
//        String Delivery_time = sdFormat.format(time_d);
//        holder.order_time.setText(Delivery_time);
//
//        //holder.order_time.setText(list.get(position).getDelivery_time().getTime());
//
//
//        Integer max_user = new Integer(list.get(position).getMax_users());
//        holder.user_number.setText("0/"+ max_user.toString() +" User Joined");
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    public class rest_order_sections_basic_adapter_ViewHolder extends RecyclerView.ViewHolder {
//
//        ImageView rest_square;
//        TextView lunch_order, user_number;
//        TextView order_time;
//
//        public rest_order_sections_basic_adapter_ViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            rest_square = itemView.findViewById(R.id.rest_square_image);
//            lunch_order = itemView.findViewById(R.id.lunch_order_text);
//            order_time = itemView.findViewById(R.id.order_time_text);
//            user_number = itemView.findViewById(R.id.user_number_text);
//
//            //itemView.setOnClickListener(this);
//        }
///*
//        @Override
//        public void onClick(View view) {
//            ClickListener.onClick(view, getAdapterPosition());
//        }
//*/
//
//    }
//
//    public interface RecyclerViewClickListener{
//        void onClick(String section_id);
//
//    }
//}
