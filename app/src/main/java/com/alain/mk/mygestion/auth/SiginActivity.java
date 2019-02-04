package com.alain.mk.mygestion.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.alain.mk.mygestion.HomeActivity;
import com.alain.mk.mygestion.MainActivity;
import com.alain.mk.mygestion.R;
import com.alain.mk.mygestion.base.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.OnClick;

public class SiginActivity extends BaseActivity {

    @BindView(R.id.sigin_activity_email) TextView emailInput;
    @BindView(R.id.sigin_activity_inputpassword) TextView inputPassword;

    private FirebaseAuth mAuth;
    private DatabaseReference database;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance().getReference().child("users");

        this.configureToolbar();
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_sigin;
    }

    @OnClick(R.id.sigin_activity_button_login)
    public void onClickSiginButtoon(){

        this.checkLogin();
    }

    private void checkLogin() {

        String email = emailInput.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {

            Toast.makeText(SiginActivity.this, "Les champs sont vides, veuillez les remplir...", Toast.LENGTH_LONG).show();
        }else {
            progressDialog.setMessage("Checking Login ...");
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            progressDialog.dismiss();

                            if (task.isSuccessful()) {
                                checkUserExist();

                            } else {
                                Toast.makeText(SiginActivity.this, "Vous n'avez pas de compte, ou mot de passe incorect", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }

    }

    private void checkUserExist() {

        if (mAuth.getCurrentUser() != null) {

            final String user_id = mAuth.getCurrentUser().getUid();

            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user_id)) {

                        Intent mainIntent = new Intent(SiginActivity.this, HomeActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);

                    } else {

                        Toast.makeText(SiginActivity.this, "Vous n'avez pas de compte, veuillez en créer un", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
