package com.ecommerce.merchant.fypproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.ecommerce.merchant.fypproject.adapter.UploadProcess;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class FeedbackActivity extends AppCompatActivity {
    private Spinner spinner;
    private EditText edtDesc;
    private TextView wordcount;
    private String  email;
    private String desc;
    private String category;
    private String uid;
    private Button btnClear;
    private Button btnSubmit;
    private ProgressDialog progressDialog;
    private final String FeedbackUploadURL = "http://ecommercefyp.000webhostapp.com/retailer/customer_function.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        GetFirebaseAuth();

        Toolbar feebacktoolbar = findViewById(R.id.feebacktoolbar);
        setSupportActionBar(feebacktoolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.feedback));


        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        spinner = findViewById(R.id.spinnerFeedback);
        edtDesc = findViewById(R.id.edtFeedDesc);
        btnSubmit = findViewById(R.id.btnFeedSubmit);
        btnClear = findViewById(R.id.btnClear);
        wordcount = findViewById(R.id.textView34);
        edtDesc.addTextChangedListener(textWatcher);
        btnClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                edtDesc.setError(null);
                spinner.setSelection(0);
                edtDesc.setText("");
            }
        });

        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                desc = edtDesc.getText().toString();
                category = spinner.getSelectedItem().toString();

                if (desc.equals("")) {
                    edtDesc.setError(getResources().getString(R.string.emptyMsg));
                } else if (desc.length() < 10) {
                    edtDesc.setError(getResources().getString(R.string.minFeedback));
                }
                if (!(desc.equals("")) && desc.length() > 10) {
                    UploadtoServer();
                }

            }
        });
    }

    private void UploadtoServer() {
        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // Showing progress dialog at image upload time.

                progressDialog = ProgressDialog.show(FeedbackActivity.this, getResources().getString(R.string.submitFeedback), getResources().getString(R.string.plsWait), false, false);
            }

            @Override
            protected String doInBackground(Void... voids) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,JSONObject> HashMapParams = new HashMap<>();
                JSONObject JSONFeedback = new JSONObject();

                try {
                    JSONFeedback.put("Uid",uid);
                    JSONFeedback.put("Role","Retailer");
                    JSONFeedback.put("Name","null");
                    JSONFeedback.put("Category",category);
                    JSONFeedback.put("Email",email);
                    JSONFeedback.put("Desc",desc);
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                    // handle your exception here!
                    e.printStackTrace();
                }

                Log.e("Json",JSONFeedback.toString());
                HashMapParams.put("feedback", JSONFeedback);

                return ProcessClass.HttpRequestObject(FeedbackUploadURL, HashMapParams);
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                Log.d("Response",string1);
                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                Toast.makeText(FeedbackActivity.this, getResources().getString(R.string.submitFeedbackScc), Toast.LENGTH_LONG).show();
                finish();

            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }
    private void GetFirebaseAuth(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this,getResources().getString(R.string.sessionexp),Toast.LENGTH_LONG).show();
        }else{
            email = firebaseAuth.getCurrentUser().getEmail();
            uid = firebaseAuth.getCurrentUser().getUid();
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String word = 300-edtDesc.length()+getResources().getString(R.string.wordremain2);
            wordcount.setText(word);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    public  boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
