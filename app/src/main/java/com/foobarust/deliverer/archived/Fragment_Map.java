//package com.foobarust.deliverer;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//public class Fragment_Map extends Fragment {
//
//    Button button_1;
//
//    View v;
//
//
//    private OnMapReadyCallback callback = new OnMapReadyCallback() {
//
//        /**
//         * Manipulates the map once available.
//         * This callback is triggered when the map is ready to be used.
//         * This is where we can add markers or lines, add listeners or move the camera.
//         * In this case, we just add a marker near Sydney, Australia.
//         * If Google Play services is not installed on the device, the user will be prompted to
//         * install it inside the SupportMapFragment. This method will only be triggered once the
//         * user has installed Google Play services and returned to the app.
//         */
//        @Override
//        public void onMapReady(GoogleMap googleMap) {
//
//            // Add a marker in Sydney and move the camera
//            LatLng HKUST_address = new LatLng(22.337503, 114.262971);
//            googleMap.addMarker(new MarkerOptions().position(HKUST_address).title("Atrium, HKUST - 中庭，香港科技大學"));
//
//            googleMap.getUiSettings().setZoomControlsEnabled(true);  // 右下角的放大縮小功能
//            googleMap.getUiSettings().setMapToolbarEnabled(true);    // 右下角的導覽及開啟 Google Map功能
//
//            Log.d("aaa", "最高放大層級：" + googleMap.getMaxZoomLevel());
//            Log.d("bbb", "最低放大層級：" + googleMap.getMinZoomLevel());
//
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(HKUST_address));
//            googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));   // 放大地圖到 17 倍大
//
//        }
//    };
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//
//        v = inflater.inflate(R.layout.fragment_map, container, false);
//
//
//        button_1 = (Button) v.findViewById(R.id.Order_Detail_button);
//        button_1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent_3 = new Intent(getActivity(), com.foobarust.deliverer.pending_order_activity.class);
//                startActivity(intent_3);
//            }
//        });
//
//
//        return v;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        SupportMapFragment mapFragment =
//                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(callback);
//        }
//
//
//    }
//}