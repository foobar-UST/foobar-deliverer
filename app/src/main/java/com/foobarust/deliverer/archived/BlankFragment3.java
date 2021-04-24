//package com.foobarust.deliverer;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.foobarust.deliverer.adapter.setting_item_adapter;
//import com.foobarust.deliverer.adapter.setting_user_new_adapter;
//import com.foobarust.deliverer.model.setting_item_model;
//import com.foobarust.deliverer.model.setting_user_new_model;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link BlankFragment3#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class BlankFragment3 extends Fragment {
//
//    setting_user_new_adapter.RecyclerViewClickListener ClickListener_1;
//    setting_item_adapter.RecyclerViewClickListener ClickListener_2;
//
//    View v;
//    RecyclerView setting_RecyclerView_1;
//    RecyclerView setting_RecyclerView_2;
//
//
//    setting_item_adapter setting_item_adapter_0;
//    List<setting_item_model> setting_item_ArrayList;
//
//
//    setting_user_new_adapter setting_user_adapter_new_0;
//    List<setting_user_new_model> setting_user_new_ArrayList;
//
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
//    public BlankFragment3() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment BlankFragment3.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static BlankFragment3 newInstance(String param1, String param2) {
//        BlankFragment3 fragment = new BlankFragment3();
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
//
//
//        setOnClickListener_1();
//
//        setting_user_new_ArrayList = new ArrayList<>();
//        setting_user_new_ArrayList.add(new setting_user_new_model("User", R.drawable.ic_user, "View My Profile", R.drawable.ic_arrow_forward));
//
//
//        setOnClickListener_2();
//
//        setting_item_ArrayList = new ArrayList<>();
//        setting_item_ArrayList.add(new setting_item_model("Notifications", R.drawable.ic_notification_important));
//        setting_item_ArrayList.add(new setting_item_model("Features", R.drawable.ic_whatshot));
//        setting_item_ArrayList.add(new setting_item_model("Contact Us", R.drawable.ic_live_help));
//        setting_item_ArrayList.add(new setting_item_model("License", R.drawable.ic_copyright));
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        //return inflater.inflate(R.layout.fragment_blank3, container, false);
//
//
//        v = inflater.inflate(R.layout.fragment_blank3, container, false);
//
//
//        setting_RecyclerView_1 = v.findViewById(R.id.setting_recycler_view_1);
//
//        RecyclerView.LayoutManager layoutManager_1 = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
//        setting_RecyclerView_1.setLayoutManager(layoutManager_1);
//
//        setting_user_adapter_new_0 = new setting_user_new_adapter(getContext(), setting_user_new_ArrayList, ClickListener_1);
//        setting_RecyclerView_1.setAdapter(setting_user_adapter_new_0);
//
//
//        setting_RecyclerView_2 = v.findViewById(R.id.setting_recycler_view_2);
//
//        RecyclerView.LayoutManager layoutManager_2 = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
//        setting_RecyclerView_2.setLayoutManager(layoutManager_2);
//
//        setting_item_adapter_0 = new setting_item_adapter(getContext(), setting_item_ArrayList,  ClickListener_2);
//        setting_RecyclerView_2.setAdapter(setting_item_adapter_0);
//
//
//        return v;
//    }
//
//    private void setOnClickListener_1() {
//
//        ClickListener_1 = new setting_user_new_adapter.RecyclerViewClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                Intent intent = new Intent();
//                switch (position){
//                    case 0:
//                        intent =  new Intent(getContext(), user_account_activity.class);
//                        break;
//                }
//                startActivity(intent);
//            }
//        };
//    }
//
//    private void setOnClickListener_2() {
//
//        ClickListener_2 = new setting_item_adapter.RecyclerViewClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                Intent intent = new Intent();
//                switch (position){
//                    case 0:
//                        intent =  new Intent(getContext(), setting_list_activity.class);
//                        break;
//                    case 1:
//                        intent =  new Intent(getContext(), setting_list_activity.class);
//                        break;
//                    case 2:
//                        intent =  new Intent(getContext(), setting_list_activity.class);
//                        break;
//
//                    default:
//                        intent =  new Intent(getContext(), setting_list_activity.class);
//                        break;
//
//                }
//                startActivity(intent);
//            }
//        };
//    }
//}