package com.example.drhello.ui.writepost;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.drhello.R;
import com.example.drhello.adapter.ReactionsListener;
import pl.droidsonroids.gif.GifImageView;

public class FBReactionDialog extends DialogFragment implements View.OnClickListener {
    View view;
    GifImageView ic_like,ic_love,ic_haha,ic_sad,ic_wow,ic_angry;
    ReactionsListener listener;

    public FBReactionDialog(ReactionsListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.dialog_fb_reactions,container,false);
        initializeComponents();
        return view;
    }

    private void initializeComponents() {
        if (getView()==null)
            return;
        ic_like=getView().findViewById(R.id.ic_like);
        ic_love=getView().findViewById(R.id.ic_love);
        ic_haha=getView().findViewById(R.id.ic_haha);
        ic_sad=getView().findViewById(R.id.ic_sad);
        ic_wow=getView().findViewById(R.id.ic_wow);
        ic_angry=getView().findViewById(R.id.ic_angry);

        ic_like.setOnClickListener(this);
        ic_love.setOnClickListener(this);
        ic_haha.setOnClickListener(this);
        ic_sad.setOnClickListener(this);
        ic_wow.setOnClickListener(this);
        ic_angry.setOnClickListener(this);

    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }

    @Override
    public void onClick(View v) {
        if (listener!=null){
            switch (v.getId()){
                case R.id.ic_like:
                    Log.e("reaction", "ic_like");
                    listener.onReactionsSelected(0);
                    getDialog().dismiss();
                    break;
                case R.id.ic_love:
                    Log.e("reaction", "ic_love");
                    listener.onReactionsSelected(1);
                    getDialog().dismiss();
                    break;
                case R.id.ic_haha:
                    listener.onReactionsSelected(2);
                    getDialog().dismiss();
                    break;
                case R.id.ic_sad:
                    listener.onReactionsSelected(3);
                    getDialog().dismiss();
                    break;
                case R.id.ic_wow:
                    listener.onReactionsSelected(4);
                    getDialog().dismiss();
                    break;
                case R.id.ic_angry:
                    listener.onReactionsSelected(5);
                    getDialog().dismiss();
                    break;
            }
        }

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        WindowManager.LayoutParams manager=new WindowManager.LayoutParams();
        manager.width=WindowManager.LayoutParams.MATCH_PARENT;
        manager.height=WindowManager.LayoutParams.WRAP_CONTENT;
        manager.dimAmount=0.0f;
        dialog.getWindow().getDecorView().setBackground(getResources().getDrawable(android.R.color.transparent));
        dialog.setCanceledOnTouchOutside(true);
        dialog.onBackPressed();
    }

//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        try {
//            listener=(ReactionsListener)context;
//        }catch (ClassCastException exception){
//            Log.e("error", exception.getMessage().toString());
//        }
//    }
//
//    @Override
//    public void onAttach(@NonNull Activity activity) {
//        super.onAttach(activity);
//        try {
//            listener=(ReactionsListener)activity;
//        }catch (ClassCastException exception){
//            Log.e("error", exception.getMessage().toString());
//        }
//    }
}
