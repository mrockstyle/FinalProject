package com.example.msk.finalproject.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.controller.Constant;
import com.example.msk.finalproject.controller.LoginActivity;
import com.example.msk.finalproject.dao.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FragmentMenu extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference mUserRef;
    private User user;

    private Button btn_editData,btn_map,btn_signOut;
    private TextView tv_Fname,tv_Lname;

    public FragmentMenu() {
        super();
    }

    public static FragmentMenu newInstance() {
        FragmentMenu fragment = new FragmentMenu();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mUserRef = database.getReference("users");
        user = new User();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState
        tv_Fname = rootView.findViewById(R.id.tv_Fname);
        tv_Lname = rootView.findViewById(R.id.tv_Lname);
        btn_editData = rootView.findViewById(R.id.btn_editData);
        btn_map = rootView.findViewById(R.id.btn_map);
        btn_signOut = rootView.findViewById(R.id.btn_signOut);
        btn_editData.setOnClickListener(this);
        btn_map.setOnClickListener(this);
        btn_signOut.setOnClickListener(this);
        showProfile();
    }

    @Override
    public void onClick(View view) {
        int v = view.getId();
        if (v == R.id.btn_signOut){
            signOut();
        }else if (v == R.id.btn_editData){
            FragmentEditData fragmentEditData = new FragmentEditData();
            changeFragment(R.id.mainContainer,fragmentEditData);
        }else if (v == R.id.btn_map){
            FragmentMap fragmentMap = new FragmentMap();
            changeFragment(R.id.mainContainer,fragmentMap);
        }
    }

    private void changeFragment(int id,Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(id,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private void showProfile() {

        mUserRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    Toast.makeText(getContext(), "Error: could not fetch user.", Toast.LENGTH_LONG).show();
                } else {
                    tv_Fname.setText("Hello "+user.getFirstname());
                    tv_Lname.setText(user.getLastname());
                    //Log.i("Value","User : "+user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", databaseError.getMessage());
            }
        });

    }

    private void signOut() {
        mAuth.signOut();

        getActivity().finish();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance (Fragment level's variables) State here
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance (Fragment level's variables) State here
    }
}
