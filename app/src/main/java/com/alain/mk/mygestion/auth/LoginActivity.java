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

public class LoginActivity extends BaseActivity {

    @BindView(R.id.login_activity_nameInput) TextView nameInput;
    @BindView(R.id.login_activity_email) TextView emailInput;
    @BindView(R.id.login_activity_inputpassword) TextView inputPassword;

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
        return R.layout.activity_login;
    }

    @OnClick(R.id.login_activity_button_login)
    public void onClickSigin(){

        this.starSiginActivity();
    }

    @OnClick(R.id.sigin_activity_text_see)
    public void onClickTextToSigin(){

        Intent intent = new Intent(LoginActivity.this, SiginActivity.class);
        startActivity(intent);
    }

    private void starSiginActivity(){

        final String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){

            Toast.makeText(LoginActivity.this, "Les champs sont vides, veuillez les remplir...", Toast.LENGTH_LONG).show();
        }else {

            progressDialog.setMessage("Connexion...");
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                String userId = mAuth.getCurrentUser().getUid();

                                DatabaseReference currentUser = database.child(userId);

                                currentUser.child("code").setValue(name);

                                progressDialog.dismiss();

                                Toast.makeText(LoginActivity.this, "Authentification Réussie", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }else {

                                checkUserExist();
                                Toast.makeText(LoginActivity.this, "Vous avez déjà un compte. Veuillez vous connecter", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
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

                        Toast.makeText(LoginActivity.this, "Vous avez déjà un compte. Veuillez vous connecter", Toast.LENGTH_LONG).show();
                        Intent mainIntent = new Intent(LoginActivity.this, HomeActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
