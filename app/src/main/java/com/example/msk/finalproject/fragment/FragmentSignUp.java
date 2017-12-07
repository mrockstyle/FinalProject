package com.example.msk.finalproject.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.dao.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class FragmentSignUp extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mRootRef,mUserRef;
    private User user;

    EditText Edt_Email,Edt_Password,Edt_Fname,Edt_Lname,Edt_Role;
    private String Email,Password,Fname,Lname,Role;
    Button btn_SignUp;
    private ProgressDialog progressDialog;

    public FragmentSignUp() {
        super();
    }

    public static FragmentSignUp newInstance() {
        FragmentSignUp fragment = new FragmentSignUp();
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
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserRef = mRootRef.child("users");


        user = new User();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState

        progressDialog = new ProgressDialog(getContext());

        Edt_Fname = rootView.findViewById(R.id.Edt_FName);
        Edt_Lname = rootView.findViewById(R.id.Edt_LName);
        Edt_Email = rootView.findViewById(R.id.Edt_Email);
        Edt_Password = rootView.findViewById(R.id.Edt_Password);
        Edt_Role = rootView.findViewById(R.id.Edt_Role);
        btn_SignUp = rootView.findViewById(R.id.BtnSignUp);
        btn_SignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int v = view.getId();
        if (v == R.id.BtnSignUp){
            createAccount(Edt_Email.getText().toString(), Edt_Password.getText().toString());
        }
    }

    private void createAccount(String email, String password) {
        if (!validateForm()){
            return;
        }

        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Test", "createUserWithEmail:success");
                            Toast.makeText(getContext().getApplicationContext(), "Create accout success.",
                                    Toast.LENGTH_SHORT).show();

                            FirebaseUser userID = task.getResult().getUser();
                            saveUserInformation(userID.getUid());

                            goToLogIn();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Test", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = Edt_Email.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Edt_Email.setError("Required.");
            valid = false;
        } else {
            Edt_Email.setError(null);
        }

        String password = Edt_Password.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Edt_Password.setError("Required.");
            valid = false;
        } else {
            Edt_Password.setError(null);
        }

        return valid;
    }

    private void goToLogIn(){
        FragmentLogIn fragmentLogin = new FragmentLogIn();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.loginContainer,fragmentLogin);
        transaction.commit();
    }

    private void saveUserInformation(String userID) {

        Email = Edt_Email.getText().toString();
        Password = Edt_Password.getText().toString();
        Fname = Edt_Fname.getText().toString();
        Lname = Edt_Lname.getText().toString();
        Role = Edt_Role.getText().toString();

        user = new User(Email,Password,Fname,Lname,Role);
        mUserRef.child(userID).setValue(user);
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
