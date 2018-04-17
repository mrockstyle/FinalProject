package com.example.msk.finalproject.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.StrictMode;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.msk.finalproject.R;
import com.example.msk.finalproject.controller.Constant;
import com.example.msk.finalproject.manager.HttpManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FragmentSignUp extends Fragment implements View.OnClickListener {
    //Variables
    private EditText Edt_Username,Edt_Password,Edt_Fname,Edt_Lname,Edt_tel;
    private CheckBox cb_admin;
    private String Email,Password,Fname,Lname;
    private Button btn_SignUp;
    private ProgressDialog progressDialog;
    private Integer isAdmin = 0; //0 -> user //1 -> admin
    private AlertDialog.Builder ad;
    private String strStatusID = "0";
    private String strError = "Unknow User!";

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
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState

        progressDialog = new ProgressDialog(getContext());

        Edt_Fname = rootView.findViewById(R.id.Edt_FName);
        Edt_Lname = rootView.findViewById(R.id.Edt_LName);
        Edt_tel = rootView.findViewById(R.id.Edt_tel);
        Edt_Username = rootView.findViewById(R.id.Edt_Username);
        Edt_Password = rootView.findViewById(R.id.Edt_Password);
        cb_admin = rootView.findViewById(R.id.cb_admin);
        btn_SignUp = rootView.findViewById(R.id.BtnSignUp);
        btn_SignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int v = view.getId();
        if (v == R.id.BtnSignUp){
            createAccount(Edt_Username.getText().toString()
                    , Edt_Password.getText().toString()
                    , Edt_Fname.getText().toString()
                    , Edt_Lname.getText().toString()
                    , Edt_tel.getText().toString());
        }
    }

    private void createAccount(String username, String password, String fname,String lname,String tel) {
        if (!validateForm()){
            return;
        }

        //Dialog
        ad = new AlertDialog.Builder(getActivity());
        ad.setTitle("Error!");
        ad.setIcon(android.R.drawable.btn_star_big_on);
        ad.setPositiveButton("Close",null);

        progressDialog.setMessage("Registering User...");
        progressDialog.show();


        if (cb_admin.isChecked()){
            isAdmin = 1;
        }

        //saveUserData
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("firstname", fname));
        params.add(new BasicNameValuePair("lastname", lname));
        params.add(new BasicNameValuePair("tel", tel));
        params.add(new BasicNameValuePair("isAdmin",String.valueOf(isAdmin)));
        params.add(new BasicNameValuePair("isFirstTime",String.valueOf(1)));

        //sendUserData
        String resultServer  = HttpManager.getInstance().getHttpPost(Constant.URL+Constant.URL_REGISTER,params);

        JSONObject c;
        try {
            c = new JSONObject(resultServer);
            strStatusID = c.getString("StatusID");
            strError = c.getString("Error");
        }catch (JSONException e){
            e.printStackTrace();
        }

        progressDialog.dismiss();

        // Prepare Save Data
        if (strStatusID.equals("0")) {

            ad.setMessage(strError);
            ad.show();

        } else {
            Toast.makeText(getActivity(), "Save Data Successfully", Toast.LENGTH_SHORT).show();
            Edt_Username.setText("");
            Edt_Password.setText("");
            Edt_Fname.setText("");
            Edt_Lname.setText("");
            Edt_tel.setText("");

            goToLogIn();
        }

    }

    private boolean validateForm() {
        boolean valid = true;

        String username = Edt_Username.getText().toString();
        if (TextUtils.isEmpty(username)) {
            Edt_Username.setError("Required.");
            valid = false;
        } else {
            Edt_Username.setError(null);
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
