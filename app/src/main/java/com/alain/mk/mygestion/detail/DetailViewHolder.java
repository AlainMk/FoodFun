package com.alain.mk.mygestion.detail;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alain.mk.mygestion.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.activity_detail_item_member_name) TextView textView;
    @BindView(R.id.activity_detail_item_member_date) TextView phoneNumber;

    public DetailViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateWithRegisterMember(String member){

        this.textView.setText(member);
    }

    public void updateWithRegisterDate(String dateCreat){

        this.phoneNumber.setText(dateCreat);
    }

}
