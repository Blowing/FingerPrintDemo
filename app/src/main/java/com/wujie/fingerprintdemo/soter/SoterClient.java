package com.wujie.fingerprintdemo.soter;

import android.content.Context;
import android.os.CancellationSignal;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.tencent.soter.wrapper.SoterWrapperApi;
import com.tencent.soter.wrapper.wrap_callback.SoterProcessAuthenticationResult;
import com.tencent.soter.wrapper.wrap_callback.SoterProcessCallback;
import com.tencent.soter.wrapper.wrap_callback.SoterProcessKeyPreparationResult;
import com.tencent.soter.wrapper.wrap_callback.SoterProcessNoExtResult;
import com.tencent.soter.wrapper.wrap_fingerprint.SoterFingerprintCanceller;
import com.tencent.soter.wrapper.wrap_fingerprint.SoterFingerprintStateCallback;
import com.tencent.soter.wrapper.wrap_net.ISoterNetCallback;
import com.tencent.soter.wrapper.wrap_net.IWrapUploadKeyNet;
import com.tencent.soter.wrapper.wrap_task.AuthenticationParam;
import com.tencent.soter.wrapper.wrap_task.InitializeParam;
import com.wujie.fingerprintdemo.base.Constants;

/**
 * Created by wujie on 2018/4/28/028.
 *
 */

public class SoterClient {

    private Context context;
    private Handler handler;

    public SoterClient(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    /**
     * 初始化
     */
    public void init() {
        InitializeParam param = new InitializeParam.InitializeParamBuilder()
                .setScenes(0)
                .build();
        SoterWrapperApi.init(context, new SoterProcessCallback<SoterProcessNoExtResult>() {
            @Override
            public void onResult(@NonNull SoterProcessNoExtResult result) {
                handler.obtainMessage(Constants.FINGER_AUTH_ERROR, result.errMsg).sendToTarget();
            }
        }, param);
    }

    /**
     * 在验证前，准备好秘钥
     */
    public void prepare() {
        SoterWrapperApi.prepareAppSecureKey(new SoterProcessCallback<SoterProcessKeyPreparationResult>() {



            @Override
            public void onResult(@NonNull SoterProcessKeyPreparationResult result) {

            }
        }, false, new IWrapUploadKeyNet() {
            @Override
            public void setRequest(@NonNull UploadRequest requestDataModel) {

            }

            @Override
            public void execute() {

            }

            @Override
            public void setCallback(ISoterNetCallback<UploadResult> callback) {

            }
        });
    }

    /**
     * 开始认证
     */
    public void startAuth() {
        AuthenticationParam param = new AuthenticationParam.AuthenticationParamBuilder()
                .setScene(0)
                .setContext(context)
                .setFingerprintCanceller(new SoterFingerprintCanceller() {
                    @Override
                    public boolean asyncCancelFingerprintAuthentication() {
                        return super.asyncCancelFingerprintAuthentication();
                    }

                    @Override
                    public boolean asyncCancelFingerprintAuthenticationInnerImp(boolean shouldPublishCancel) {
                        return super.asyncCancelFingerprintAuthenticationInnerImp
                                (shouldPublishCancel);
                    }

                    @Override
                    public void refreshCancellationSignal() {
                        super.refreshCancellationSignal();
                    }

                    @NonNull
                    @Override
                    public CancellationSignal getSignalObj() {
                        return super.getSignalObj();
                    }
                }).setPrefilledChallenge("测试挑战")
                .setSoterFingerprintStateCallback(new SoterFingerprintStateCallback() {
                    @Override
                    public void onStartAuthentication() {

                    }

                    @Override
                    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                        handler.obtainMessage(Constants.FINGER_AUTH_HELP,helpCode, 0,
                                helpString).sendToTarget();
                    }

                    @Override
                    public void onAuthenticationSucceed() {
                        handler.obtainMessage(Constants.FINGER_AUTH_SUCCESS).sendToTarget();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        handler.obtainMessage(Constants.FINGER_AUTH_FAILED).sendToTarget();
                    }

                    @Override
                    public void onAuthenticationCancelled() {

                    }

                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errorString) {
                        handler.obtainMessage(Constants.FINGER_AUTH_ERROR, errorCode, 0,
                                errorString);

                    }
                }).build();
        SoterWrapperApi.requestAuthorizeAndSign(new SoterProcessCallback<SoterProcessAuthenticationResult>() {



            @Override
            public void onResult(@NonNull SoterProcessAuthenticationResult result) {
               // Log.i("wujie", result.getExtData().getFid());

            }
        }, param);

    }

    public void cancel() {
        SoterWrapperApi.tryStopAllSoterTask();
    }

    /**
     * 释放资源
     */
    public void release() {
        SoterWrapperApi.release();
    }
}
