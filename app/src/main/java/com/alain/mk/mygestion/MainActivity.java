package com.alain.mk.mygestion;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.widget.Button;

import com.alain.mk.mygestion.api.UserHelper;
import com.alain.mk.mygestion.auth.ProfileActivity;
import com.alain.mk.mygestion.base.BaseActivity;
import com.alain.mk.mygestion.register.RegisterActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    //FOR DESIGN
    // Get Coordinator Layout
    @BindView(R.id.main_activity_coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.main_activity_button_login) Button buttonLogin;

    // Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 123;

    @Override
    public int getFragmentLayout() { return R.layout.activity_main; }

    @Override
    protected void onResume() {
        super.onResume();
        // Update UI when activity is resuming
        this.updateUIWhenResuming();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle SignIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    // --------------------
    // ACTIONS
    // --------------------
    @OnClick(R.id.main_activity_button_login)
    public void onClickLoginButton() {
        // Start appropriate activity
        if (this.isCurrentUserLogged()){
            this.startProfileActivity();
        } else {
            this.startSignInActivity();
        }
    }

    @OnClick(R.id.main_activity_button_register)
    public void onClickRegisterButton() {
        // Check if user is connected before launching MentorActivity
        if (this.isCurrentUserLogged()){
            this.startRegisterActivity();
        } else {
            this.showSnackBar(this.coordinatorLayout, getString(R.string.error_not_connected));
        }
    }

    // --------------------
    // REST REQUEST
    // --------------------

    // Http request that create user in firestore
    private void createUserInFirestore(){

        if (this.getCurrentUser() != null){

            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();

            UserHelper.createUser(uid, username, urlPicture).addOnFailureListener(this.onFailureListener());
        }
    }

    // Show Snack Bar with a message
    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message){
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void updateUIWhenResuming(){
        this.buttonLogin.setText(this.isCurrentUserLogged() ? getString(R.string.button_login_text_logged) : getString(R.string.button_login_text_not_logged));
    }

    // --------------------
    // UTILS
    // --------------------

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                // CREATE USER IN FIRESTORE
                this.createUserInFirestore();
                showSnackBar(this.coordinatorLayout, getString(R.string.connection_succeed));
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_authentication_canceled));
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }

    // Starting RegisterActivity
    private void startRegisterActivity(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void startProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
    // --------------------
    // NAVIGATION
    // --------------------

    // Launch Sign-In Activity
    private void startSignInActivity(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_logo_auth)
                        .build(),
                RC_SIGN_IN);
    }

}