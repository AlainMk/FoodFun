package com.alain.mk.mygestion.auth;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alain.mk.mygestion.R;
import com.alain.mk.mygestion.base.BaseActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;

import butterknife.BindView;
import butterknife.OnClick;

public class ProfileActivity extends BaseActivity {

    //FOR DESIGN
    @BindView(R.id.profile_activity_imageview_profile) ImageView imageViewProfile;
    @BindView(R.id.profile_activity_edit_text_username) TextInputEditText textInputEditTextUsername;
    @BindView(R.id.profile_activity_text_view_email) TextView textViewEmail;
    @BindView(R.id.profile_activity_progress_bar) ProgressBar progressBar;

    // 2 - Identify each Http Request
    private static final int SIGN_OUT_TASK = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureToolbar();
        this.updateUIWhenCreating(); // Update UI
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_profile;
    }

    // 4 - Adding requests to button listeners

    @OnClick(R.id.profile_activity_button_sign_out)
    public void onClickSignOutButton() { this.signOutUserFromFirebase(); }

    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    // --------------------
    // UI
    // --------------------

    // Update UI when activity is creating
    private void updateUIWhenCreating(){

        if (this.getCurrentUser() != null){

            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageViewProfile);
            }

            //Get email & username from Firebase
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            String username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();

            //Update views with data
            this.textInputEditTextUsername.setText(username);
            this.textViewEmail.setText(email);
        }
    }

    // 3 - Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case SIGN_OUT_TASK:
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }

}
