//package com.foobarust.deliverer;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.MenuItem;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//
//import com.foobarust.deliverer.archived.BlankFragment1;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//
//
//
//public class MainActivity extends AppCompatActivity {
//    private FirebaseAuth mAuth;
//    Integer back = 0;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        //setContentView(R.layout.activity_main);
//
//        setContentView(R.layout.fragment_blank);
//
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                // yourMethod();
//                setContentView(R.layout.restaurant_list);
//                BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_in_res_list);
//                bottomNav.setOnNavigationItemSelectedListener(navListener);
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_in_res_list, new BlankFragment1()).commit();
//            }
//        }, 3000);   //3 seconds
//
//
//
//        //setContentView(R.layout.restaurant_list);
//
//        //BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_in_res_list);
//        //bottomNav.setOnNavigationItemSelectedListener(navListener);
//
//        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_in_res_list, new BlankFragment1()).commit();
//        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_in_res_list_2, new BlankFragment()).commit();
//
//
//
//
//        // Initialize Firebase Auth
//        mAuth = FirebaseAuth.getInstance();
//
//        mAuth.signInWithEmailAndPassword("ivantest@gmail.com", "ivantest")//email, password
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            //Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            //updateUI(user);
//                            String uid = user.getUid();
//                            Log.d("test", uid);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
//                            //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                            //        Toast.LENGTH_SHORT).show();
//                            //updateUI(null);
//                            Log.d("test", task.getException().getMessage());
//                            // ...
//                        }
//
//                        // ...
//                    }
//                });
//
//
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        //updateUI(currentUser);
//
//
//    }
//
//    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
//            new BottomNavigationView.OnNavigationItemSelectedListener() {
//                @Override
//                public boolean onNavigationItemSelected(@NonNull MenuItem item){
//                    Fragment selectedFragment = null;
//
//                    switch (item.getItemId()){
//                        case R.id.blankFragment1:
//                            selectedFragment = new BlankFragment1();
//                            break;
//                        case R.id.blankFragment2:
//                            selectedFragment = new com.foobarust.deliverer.Fragment_Map();
//                            break;
//                        case R.id.blankFragment3:
//                            selectedFragment = new com.foobarust.deliverer.BlankFragment3();
//                            break;
//                    }
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_in_res_list, selectedFragment).commit();
//
//                    return true;
//                }
//            };
//
//
//
//
//
//}
//
//
