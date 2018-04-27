package com.wujie.fingerprintdemo.google;

import android.os.Handler;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import com.wujie.fingerprintdemo.base.Constants;

/**
 * Created by wujie on 2018/4/27/027.
 * 指纹识别验证的回调
 */

public class AuthCallBack extends FingerprintManagerCompat.AuthenticationCallback{

    private Handler handler;

    public AuthCallBack(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        super.onAuthenticationError(errMsgId, errString);
        if(handler != null) {
            handler.obtainMessage(Constants.FINGER_AUTH_ERROR, errMsgId, 0, errString).sendToTarget();
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        super.onAuthenticationHelp(helpMsgId, helpString);
        if(handler != null) {
            handler.obtainMessage(Constants.FINGER_AUTH_HELP, helpMsgId, 0, helpString).sendToTarget();
        }

    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        if(handler != null) {
            handler.obtainMessage(Constants.FINGER_AUTH_SUCCESS).sendToTarget();
        }
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        if(handler != null) {
            handler.obtainMessage(Constants.FINGER_AUTH_FAILED).sendToTarget();
        }
    }
}
