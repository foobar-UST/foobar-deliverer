//package com.foobarust.deliverer;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
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
//public class pending_order_activity extends AppCompatActivity {
//
//    private FirebaseFirestore db;
//
//    Button order_accept_button;
//
//    String sessionId_2;
//    String save_sessionId;
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
//        setContentView(R.layout.pending_order_page);
//
//
//        if(savedInstanceState != null){
//            String get_save_sessionId = savedInstanceState.getString(save_sessionId);
//            sessionId_2 = get_save_sessionId;
//        }
//        else{
//            sessionId_2 = getIntent().getStringExtra("sctt_2");   //get id
//            //onRestoreInstanceState(savedInstanceState);
//        }
//
//
//        Toolbar ttt = findViewById(R.id.toolbar_penting_order);
//        setSupportActionBar(ttt);
//        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//
//        lunch_order = findViewById(R.id.lunch_order_penting_textView);
//        description = findViewById(R.id.description_penting_textView);
//        description_zh = findViewById(R.id.description_zh_penting_textView);
//        delivery_time = findViewById(R.id.delivery_time_penting_textView);
//        joined_users_count = findViewById(R.id.joined_users_count_penting_textView);
//        address = findViewById(R.id.address_penting_textView);
//
//
//        db = FirebaseFirestore.getInstance();
//
//        db.collection("sellers")
//                .document("kZr4pBjju7gQniYGS0kN")
//                .collection("sections")
//                .document(sessionId_2)//sessionId_2  //"42810619-3d54-4be7-8c0e-dfaa3315e17a"
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
//
//
//    public void onRestoreInstanceState(Bundle savedInstanceState_1) {
//        super.onRestoreInstanceState(savedInstanceState_1);
//
//        savedInstanceState_1.putString(save_sessionId, "42810619-3d54-4be7-8c0e-dfaa3315e17a");  //get sessionId_2 data and save to save_sessionId
//    }
//
//}
