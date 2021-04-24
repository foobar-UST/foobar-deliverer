//package com.foobarust.deliverer.archived;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.foobarust.deliverer.R;
//import com.foobarust.deliverer.adapter.rest_order_sections_basic_adapter;
//import com.foobarust.deliverer.archived.adapter.restaurant_1_adapter;
//import com.foobarust.deliverer.model.rest_order_sections_basic_model;
//import com.foobarust.deliverer.model.restaurant_1;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link BlankFragment1#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class BlankFragment1 extends Fragment implements rest_order_sections_basic_adapter.RecyclerViewClickListener {
//
//
//    private FirebaseFirestore db;
//
//    //rest_order_sections_basic_adapter.RecyclerViewClickListener ClickListener;
//
//    View v;
//    RecyclerView res_RecyclerView;  //res_recycler_view
//
//    restaurant_1_adapter restaurant_adapter;
//    List<restaurant_1> restaurant_ArrayList;
//
//
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public BlankFragment1() {
//        // Required empty public constructor
//
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment BlankFragment1.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static BlankFragment1 newInstance(String param1, String param2) {
//        BlankFragment1 fragment = new BlankFragment1();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//
//        }
//
//
//        //setOnClickListener();
///*
//        restaurant_ArrayList = new ArrayList<>();
//        restaurant_ArrayList.add(new restaurant_1("Lunch Order 午餐訂單", R.drawable.b1, "12:30 PM", "12/20 User Joined"));
//        restaurant_ArrayList.add(new restaurant_1("Lunch Order 午餐訂單", R.drawable.b2, "12:30 PM", "12/20 User Joined"));
//        restaurant_ArrayList.add(new restaurant_1("Lunch Order 午餐訂單", R.drawable.b3, "12:30 PM", "12/20 User Joined"));
//*/
//        db = FirebaseFirestore.getInstance();
//
//        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//    }
//
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//
///*
//        v = inflater.inflate(R.layout.fragment_blank1, container, false);
//
//        res_RecyclerView = v.findViewById(R.id.res_recycler_view);
//
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
//        res_RecyclerView.setLayoutManager(layoutManager);
//
//        restaurant_adapter = new restaurant_1_adapter(getContext(), restaurant_ArrayList, ClickListener);
//        res_RecyclerView.setAdapter(restaurant_adapter);
//
//        return v;
//*/
//
//        v = inflater.inflate(R.layout.archived_fragment_blank1, container, false);
//        res_RecyclerView = v.findViewById(R.id.res_recycler_view);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
//        res_RecyclerView.setLayoutManager(layoutManager);
//
//        db.collection("sellers")
//                .document("kZr4pBjju7gQniYGS0kN")
//                .collection("sections_basic")
//                .whereEqualTo("state", "2_preparing")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            ArrayList<rest_order_sections_basic_model> list = new ArrayList<>();
//
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                //Log.d("aaa", document.getId() + " => " + document.toObject(advertises_basic_model.class)); //document.getData()
//
//                                list.add(document.toObject(rest_order_sections_basic_model.class));
//                            }
//
//                            rest_order_sections_basic_adapter rest_order_sections_basic_adapter_1 = new rest_order_sections_basic_adapter(requireContext(), list, BlankFragment1.this);
//                            res_RecyclerView.setAdapter(rest_order_sections_basic_adapter_1);
//
//                        } else {
//                            Log.d("bbb", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//
//        return v;
//
//    }
//
//    @Override
//    public void onClick(String section_id) {
//        Intent intent = new Intent(requireContext(), com.foobarust.deliverer.restaurant_accept_order_activity.class);
//        intent.putExtra("sctt", section_id);
//        startActivity(intent);
//    }
//
///*
//    private void setOnClickListener() {
//
//        ClickListener = new rest_order_sections_basic_adapter.RecyclerViewClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                Intent intent = new Intent();
//                switch (position){
//                    case 0:
//                        intent =  new Intent(getContext(), restaurant_accept_order_activity.class);
//                        break;
//                    case 1:
//                        intent =  new Intent(getContext(), restaurant_accept_order_activity.class);
//                        break;
//                    case 2:
//                        intent =  new Intent(getContext(), restaurant_accept_order_activity.class);
//                        break;
//
//                    default:
//                        intent =  new Intent(getContext(), restaurant_accept_order_activity.class);
//                        break;
//
//                }
//                startActivity(intent);
//            }
//
//        };
//*/
//
//
//}