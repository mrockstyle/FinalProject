package com.example.msk.finalproject.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
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
import android.widget.Toast;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.controller.Constant;
import com.example.msk.finalproject.controller.MainActivity;
import com.example.msk.finalproject.dao.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class FragmentLogIn extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private EditText EdtEmail,EdtPassword;
    private Button BtnSignIn,BtnCreateAcc;
    public User users;
    private ProgressDialog progressDialog;
    //private int Success;

    public FragmentLogIn() {
        super();
    }

    public static FragmentLogIn newInstance() {
        FragmentLogIn fragment = new FragmentLogIn();
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
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        users = new User();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState
        EdtEmail = rootView.findViewById(R.id.EdtEmail);
        EdtPassword  = rootView.findViewById(R.id.EdtPassword);
        BtnSignIn = rootView.findViewById(R.id.BtnSignIn);
        BtnCreateAcc = rootView.findViewById(R.id.BtnCreateAcc);
        BtnSignIn.setOnClickListener(this);
        BtnCreateAcc.setOnClickListener(this);

        BtnCreateAcc.setPaintFlags(BtnCreateAcc.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        progressDialog = new ProgressDialog(getContext());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.BtnCreateAcc){
            FragmentSignUp fragmentSignUp = new FragmentSignUp();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.loginContainer,fragmentSignUp);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else if (v.getId() == R.id.BtnSignIn){
            signIn(EdtEmail.getText().toString(), EdtPassword.getText().toString());
        }
    }


    private void signIn(final String email, String password) {

        if (!validateForm()) {
            return;
        }

        //showProgressDialog();
        progressDialog.setMessage("Logging in");
        progressDialog.show();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("test", "signInWithEmail:success");
                            Toast.makeText(getContext(),"LogIn Successful", Toast.LENGTH_LONG).show();

                            //Change Activity
                            getActivity().finish();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("test", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // [START_EXCLUDE]
                        /*Uif (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();*/
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }


    private boolean validateForm() {
        boolean valid = true;

        String email = EdtEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            EdtEmail.setError("Required.");
            valid = false;
        } else {
            EdtEmail.setError(null);
        }

        String password = EdtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            EdtPassword.setError("Required.");
            valid = false;
        } else {
            EdtPassword.setError(null);
        }

        return valid;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance (Fragment level's variables) State here
        //outState.putInt("success",Success);
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance (Fragment level's variables) State here
        //Success = savedInstanceState.getInt("success",Success);
    }

}
