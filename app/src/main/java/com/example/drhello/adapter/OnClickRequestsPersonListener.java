package com.example.drhello.adapter;

import com.example.drhello.model.UserAccount;

public interface OnClickRequestsPersonListener {
    void onClickAccept(UserAccount userAccount);
    void onClickDelete(UserAccount userAccount);
}
