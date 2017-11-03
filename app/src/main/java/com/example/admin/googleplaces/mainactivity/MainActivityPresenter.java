package com.example.admin.googleplaces.mainactivity;

import android.view.View;

/**
 * Created by Admin on 11/2/2017.
 */

public class MainActivityPresenter implements MainActivityContractor.Presenter {
    MainActivityContractor.View view;

    @Override
    public void addView(MainActivityContractor.View view) {
        this.view = view;
    }

    @Override
    public void removeView() {
        this.view = null;
    }

    public void pickPlace(){
        view.setUpAutoComplete();
    }
}
