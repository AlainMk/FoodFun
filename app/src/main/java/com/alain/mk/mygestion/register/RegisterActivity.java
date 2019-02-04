package com.alain.mk.mygestion.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alain.mk.mygestion.MainActivity;
import com.alain.mk.mygestion.R;
import com.alain.mk.mygestion.base.BaseActivity;
import com.alain.mk.mygestion.detail.DetailActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.ServerTimestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.register_activity_nameInput) EditText nameInput;
    @BindView(R.id.register_activity_inputNumber) EditText inputNumber;
    @BindView(R.id.register_activity_text_see) TextView textSee;

    public static final int REGISTER_MEMBER = 30;
    ProgressDialog progressDialog;

    private DatabaseReference database;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseRefUser;
    @ServerTimestamp
    Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
        database = FirebaseDatabase.getInstance().getReference().child("member");
        databaseRefUser = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());

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

    @OnClick(R.id.register_activity_text_see)
    public void onClickSeeRegisterText(){

        startActivity(new Intent(this, DetailActivity.class));
    }

    private void createMember(){

        progressDialog.setMessage("Enregistrement...");

        String value = nameInput.getText().toString().trim();
        String val = inputNumber.getText().toString().trim();

        if (!TextUtils.isEmpty(value) && !TextUtils.isEmpty(val)){

            progressDialog.show();
            final DatabaseReference newPost = database.push();

            newPost.child("username").setValue(value).addOnFailureListener(onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(REGISTER_MEMBER));
            newPost.child("phoneNumber").setValue(val).addOnFailureListener(onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(REGISTER_MEMBER));
            newPost.child("dateDeCreation").setValue(this.convertDateToHour(date)).addOnFailureListener(onFailureListener());
            newPost.child("uid").setValue(currentUser.getUid()).addOnFailureListener(onFailureListener());

            databaseRefUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    newPost.child("code").setValue(dataSnapshot.child("code").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

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