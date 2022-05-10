package com.example.drhello;

import com.example.drhello.model.UserAccount;

public interface OnDoctorsClickLinstener {
    void OnClickCall(int position,String phone);
    void OnClickChat(int position,UserAccount userAccount);
    void OnClickPlace(int position,String location);
}
