package com.example.admin.googleplaces.di.mainactivity;

import com.example.admin.googleplaces.mainactivity.MainActivityPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Admin on 11/2/2017.
 */

@Module
public class MainActivityModule {

    @Provides
    MainActivityPresenter provideMainActivityPresenter(){return new MainActivityPresenter();}
}
