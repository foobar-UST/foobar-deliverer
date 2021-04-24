//package com.foobarust.deliverer;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.foobarust.deliverer.model.rest_order_sections_detail_model;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.TimeZone;
//
//public class restaurant_accept_order_activity extends AppCompatActivity {
//
//    private FirebaseFirestore db;
//
//    Button order_accept_button;
//
//    String sessionId;
//
//    View v;
//    RecyclerView res_RecyclerView;  //res_recycler_view
//
//    TextView lunch_order, description, description_zh, delivery_time, joined_users_count, address;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.restaurant_accept_order_page);
//
//        sessionId = getIntent().getStringExtra("sctt");   //get id
//
//
//        Toolbar ttt = findViewById(R.id.toolbar_rest_accept_order);
//        setSupportActionBar(ttt);
//        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//
//        lunch_order = findViewById(R.id.lunch_order_textView);
//        description = findViewById(R.id.description_textView);
//        description_zh = findViewById(R.id.description_zh_textView);
//        delivery_time = findViewById(R.id.delivery_time_textView);
//        joined_users_count = findViewById(R.id.joined_users_count_textView);
//        address = findViewById(R.id.address_textView);
//
//
//        db = FirebaseFirestore.getInstance();
//
//        db.collection("sellers")
//                .document("kZr4pBjju7gQniYGS0kN")
//                .collection("sections")
//                .document(sessionId)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//
//                            rest_order_sections_detail_model document = task.getResult().toObject(rest_order_sections_detail_model.class);
//
//
//                            lunch_order.setText(document.getTitle() + " - " + document.getTitle_zh());
//                            description.setText(document.getDescription());
//                            description_zh.setText(document.getDescription_zh());
//
//                            Date time_d = new Date(document.getDelivery_time().getTime());
//                            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
//                            sdFormat.setTimeZone(TimeZone.getTimeZone("Asia/Hong_Kong"));
//                            String Delivery_time = sdFormat.format(time_d);
//                            delivery_time.setText(Delivery_time);
//                            //delivery_time.setText(document.getDelivery_time());
//
//                            Integer max_user = new Integer(document.getMax_users());
//                            joined_users_count.setText("0/"+ max_user.toString() +" User Joined");
//                            //joined_users_count.setText(document.getMax_users());
//
//                            address.setText("       " + document.getDelivery_location().getAddress() + "\n" + document.getDelivery_location().getAddress_zh());
//
//
//                        } else {
//                            Log.d("bbb", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//    }
//
//    public void dialog_event(View view){
//        order_accept_button = (Button) findViewById(R.id.order_accept_button);
//
//        AlertDialog.Builder aaa = new AlertDialog.Builder(restaurant_accept_order_activity.this);
//        aaa.setMessage("Are you sure to accept this order?\n您確定要接受此訂單嗎？")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        //String section_id_2 = sessionId;
//                        //Intent intent = new Intent(restaurant_accept_order_activity.this, pending_order_activity.class);
//                        //intent.putExtra("sctt_2", section_id_2);
//                        //startActivity(intent);
//
//                        pass_section_id(sessionId);
//
//                        finish();
//
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        dialogInterface.cancel();
//
//                    }
//                })
//                .show();
//    }
//
//    public void pass_section_id(String section_id){
//        String section_id_2 = sessionId;
//        Intent intent_2 = new Intent(restaurant_accept_order_activity.this, pending_order_activity.class);
//        intent_2.putExtra("sctt_2", section_id_2);
//        startActivity(intent_2);
//    }
//}
