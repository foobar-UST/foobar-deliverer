//package com.foobarust.deliverer.archived.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.foobarust.deliverer.R;
//import com.foobarust.deliverer.model.order_model;
//
//import java.util.List;
//
//public class order_adapter extends RecyclerView.Adapter<order_adapter.order_adapter_ViewHolder>{
//
//    Context context;
//    List<order_model> order_model_list;
//
//    public order_adapter(Context context, List<order_model> order_model_list) {
//        this.context = context;
//        this.order_model_list = order_model_list;
//    }
//
//    @NonNull
//    @Override
//    public order_adapter_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.archived_order_item, parent, false);
//
//        return new order_adapter_ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull order_adapter_ViewHolder holder, int position) {
//        holder.name.setText(order_model_list.get(position).getName());
//    }
//
//    @Override
//    public int getItemCount() {
//        return order_model_list.size();
//    }
//
//    public class order_adapter_ViewHolder extends RecyclerView.ViewHolder {
//
//        TextView name;
//
//        public order_adapter_ViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            name = itemView.findViewById(R.id.id_name);
//        }
//    }
//}
