package com.supremesir.lockpick;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author HaoFan Fang
 * @date 2020/9/16 14:47
 */

public class MyViewModel extends ViewModel {

    private MutableLiveData<Boolean> networkStatus;


    public MutableLiveData<Boolean> getNetworkStatus() {
        if (networkStatus == null) {
            networkStatus = new MutableLiveData<>();
            networkStatus.postValue(false);
        }
        return networkStatus;
    }

    public void setNetworkStatus(boolean status) {
        networkStatus.postValue(status);
    }
}
