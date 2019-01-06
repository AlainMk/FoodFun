package com.alain.mk.mygestion.register;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.alain.mk.mygestion.R;
import com.alain.mk.mygestion.base.BaseActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.ServerTimestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.register_activity_nameInput) EditText nameInput;
    @BindView(R.id.register_activity_inputNumber) EditText inputNumber;

    public static final int REGISTER_MEMBER = 30;
    ProgressDialog progressDialog;

    private DatabaseReference database;
    @ServerTimestamp
    Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);
        database = FirebaseDatabase.getInstance().getReference().child("member");

        this.configureToolbar();
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_register;
    }

    @OnClick(R.id.register_activity_button_register)
    public void onClickRegisterButton() {
        this.createMember();
    }

    private void createMember(){

        progressDialog.setMessage("Enregistrement...");

        String value = nameInput.getText().toString().trim();
        String val = inputNumber.getText().toString().trim();

        if (!TextUtils.isEmpty(value) && !TextUtils.isEmpty(val)){

            progressDialog.show();
            DatabaseReference newPost = database.push();

            newPost.child("username").setValue(value).addOnFailureListener(onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(REGISTER_MEMBER));
            newPost.child("phoneNumber").setValue(val).addOnFailureListener(onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(REGISTER_MEMBER));
            newPost.child("dateDeCreation").setValue(this.convertDateToHour(date)).addOnFailureListener(onFailureListener());
            newPost.child("createur").setValue(this.getCurrentUser()).addOnFailureListener(onFailureListener());

            this.nameInput.setText("");
            this.inputNumber.setText("");
        }
    }

    // --------------------
    // UI
    // --------------------

    // Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case REGISTER_MEMBER:
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.register_succeed), Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private String convertDateToHour(Date date){
        DateFormat dfTime = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        return dfTime.format(date);
    }

}