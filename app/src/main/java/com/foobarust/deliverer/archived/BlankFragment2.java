//package com.foobarust.deliverer.archived;
//
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
//import com.foobarust.deliverer.archived.adapter.advertises_basic_adapter;
//import com.foobarust.deliverer.model.advertises_basic_model;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.ArrayList;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link BlankFragment2#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class BlankFragment2 extends Fragment {
//
//    private FirebaseFirestore db;
//    //Context context;
//    //ArrayList<advertises_basic_model> list;
//
//    View v;
//    RecyclerView order_RecyclerView;
//    //order_adapter order_adapter;
//    //List<order_model> order_ArrayList;
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
//    public BlankFragment2() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment BlankFragment2.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static BlankFragment2 newInstance(String param1, String param2) {
//        BlankFragment2 fragment = new BlankFragment2();
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
//        }
///*
//        order_ArrayList = new ArrayList<>();
//        order_ArrayList.add(new order_model("Order No.1"));
//        order_ArrayList.add(new order_model("Order No.2"));
//        order_ArrayList.add(new order_model("Order No.3"));
//*/
//        db = FirebaseFirestore.getInstance();
//
//    }
//
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//
//        v = inflater.inflate(R.layout.archived_fragment_blank2, container, false);
//
//        order_RecyclerView = v.findViewById(R.id.order_recycler_view);
//
//
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
//        order_RecyclerView.setLayoutManager(layoutManager);
//
//
//        db.collection("sellers")
//                .document("kZr4pBjju7gQniYGS0kN")
//                .collection("sections_basic")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            ArrayList<advertises_basic_model> list = new ArrayList<>();
//
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                //Log.d("aaa", document.getId() + " => " + document.toObject(advertises_basic_model.class)); //document.getData()
//
//                                list.add(document.toObject(advertises_basic_model.class));
//                            }
//
//                            advertises_basic_adapter device_tokens_adapter_1 = new advertises_basic_adapter(requireContext(), list);
//                            order_RecyclerView.setAdapter(device_tokens_adapter_1);
//
//
//                        } else {
//                            Log.d("aaa", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//
//        return v;
//
//    }
//
//
//}