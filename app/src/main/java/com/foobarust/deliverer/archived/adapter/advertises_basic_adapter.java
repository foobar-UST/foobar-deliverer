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
//import com.foobarust.deliverer.model.advertises_basic_model;
//
//import java.util.ArrayList;
//
//public class advertises_basic_adapter extends RecyclerView.Adapter<advertises_basic_adapter.advertises_basic_adapter_ViewHolder>{
//
//    Context context;
//    ArrayList<advertises_basic_model> list;
//
//    public advertises_basic_adapter(Context context, ArrayList<advertises_basic_model> list) {
//        this.context = context;
//        this.list = list;
//    }
//
//    @NonNull
//    @Override
//    public advertises_basic_adapter_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//        View view = LayoutInflater.from(context).inflate(R.layout.archived_order_item, parent, false);
//        return new advertises_basic_adapter_ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull advertises_basic_adapter_ViewHolder holder, int position) {
//        holder.order_no.setText(list.get(position).getId());
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    public class advertises_basic_adapter_ViewHolder extends RecyclerView.ViewHolder{
//
//        TextView order_no;
//
//        public advertises_basic_adapter_ViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            order_no = itemView.findViewById(R.id.id_name);
//
//        }
//    }
//}
