package com.example.pageindicator;

/**
 * Created by Administrator on 12/20/2016.
 */

class AdapterNotFoundException extends Exception {
    @Override
    public String getMessage() {
        return "Adapter not found";
    }
}
