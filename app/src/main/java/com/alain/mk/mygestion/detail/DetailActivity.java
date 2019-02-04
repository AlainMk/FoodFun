package com.alain.mk.mygestion.detail;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alain.mk.mygestion.R;
import com.alain.mk.mygestion.base.BaseActivity;
import com.alain.mk.mygestion.register.Member;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;

public class DetailActivity extends BaseActivity {

    @BindView(R.id.activity_detail_recycler_view) RecyclerView recyclerView;

    private DatabaseReference database;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance().getReference().child("member");
        query = database.orderByChild("uid").equalTo(this.getCurrentUser().getUid());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_detail;
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Member, DetailViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Member, DetailViewHolder>(
                        Member.class, R.layout.activity_detail_item, DetailViewHolder.class, query
                ) {
                    @Override
                    protected void populateViewHolder(DetailViewHolder viewHolder, Member model, int position) {

                        viewHolder.updateWithRegisterMember(model.getUsername());
                        viewHolder.updateWithRegisterDate(model.getPhoneNumber());
                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
