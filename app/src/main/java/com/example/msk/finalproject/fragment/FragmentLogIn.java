package com.example.msk.finalproject.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.StrictMode;
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
import com.example.msk.finalproject.manager.HttpManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class FragmentLogIn extends Fragment implements View.OnClickListener {

    private EditText EdtEmail,EdtPassword;
    private Button BtnSignIn,BtnCreateAcc;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder ad;

    private String strStatusID = "0";
    private String strFirstname = "0";
    private String strLastname = "0";
    private String strError = "Unknow User!";
    private Integer intUserID = 0;
    private Integer intIsAdmin = 0;

    private SharedPreferences preferences;


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

        preferences = getContext().getSharedPreferences(Constant.USER_PREF,0);
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
            checkLogin(EdtEmail.getText().toString(), EdtPassword.getText().toString());
        }
    }



    private boolean checkLogin(String email,String password) {
        boolean valid = true;

        email = EdtEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            EdtEmail.setError("Required.");
            valid = false;
        } else {
            EdtEmail.setError(null);
        }

        password = EdtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            EdtPassword.setError("Required.");
            valid = false;
        } else {
            EdtPassword.setError(null);
        }

        //showProgressDialog();
        progressDialog.setMessage("Logging in");
        progressDialog.show();

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("strUser", email));
        params.add(new BasicNameValuePair("strPass", password));

        /** Get result from Server (Return the JSON Code)
         * StatusID = ? [0=Failed,1=Complete]
         * MemberID = ? [Eg : 1]
         * Error	= ?	[On case error return custom error message]
         *
         * Eg Login Failed = {"StatusID":"0","MemberID":"0","Error":"Incorrect Username and Password"}
         * Eg Login Complete = {"StatusID":"1","MemberID":"2","Error":""}
         */

        String resultServer = HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_LOGIN, params);

        JSONObject c;
        try {
            c = new JSONObject(resultServer);
            strStatusID = c.getString("StatusID");
            intUserID = c.getInt("userID");
            strFirstname = c.getString("firstname");
            strLastname = c.getString("lastname");
            strError = c.getString("Error");
            intIsAdmin = c.getInt("isAdmin");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ad = new AlertDialog.Builder(getActivity());
        // Prepare Login
        if (strStatusID.equals("0")) {
            // Dialog
            progressDialog.dismiss();
            ad.setTitle("Error! ");
            ad.setIcon(android.R.drawable.btn_star_big_on);
            ad.setPositiveButton("Close", null);
            ad.setMessage(strError);
            ad.show();
            EdtEmail.setText("");
            EdtPassword.setText("");
        } else {
            Toast.makeText(getActivity(), "Login", Toast.LENGTH_SHORT).show();
            saveUserPreference();
            progressDialog.dismiss();
            LoginSuccess();

        }

        return valid;
    }

    private void saveUserPreference() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Constant.USER_ID,intUserID);
        editor.putString(Constant.USER_FNAME,strFirstname);
        editor.putString(Constant.USER_LNAME,strLastname);
        if (intIsAdmin == 1){
            editor.putBoolean(Constant.IS_ADMIN,true);
        }else {
            editor.putBoolean(Constant.IS_ADMIN,false);
        }
        editor.putBoolean(Constant.IS_LOGGED_IN,true);
        editor.apply();
    }

    private void LoginSuccess() {

        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
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
