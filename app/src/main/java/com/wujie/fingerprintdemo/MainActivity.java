package com.wujie.fingerprintdemo;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.wujie.fingerprintdemo.base.Constants;
import com.wujie.fingerprintdemo.google.AuthCallBack;
import com.wujie.fingerprintdemo.soter.SoterClient;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private TextView resultTv;
    private Button cancelBtn;
    private Button okBtn;
    private MyHandler myHandler;


    private FingerprintManagerCompat fingerprintManager = null;
    private AuthCallBack myAuthCallBack;
    private android.support.v4.os.CancellationSignal cancellationSignal;

    private SoterClient soterClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        myHandler = new MyHandler(this);
        soterClient = new SoterClient(this, myHandler);
        soterClient.init();
        //initFingerMannager();
    }

    private void initView() {
        resultTv = (TextView) findViewById(R.id.tv_result);
        okBtn = (Button) findViewById(R.id.btn_ok);
        cancelBtn = (Button) findViewById(R.id.btn_cancel);
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    private void initFingerMannager() {
        fingerprintManager = FingerprintManagerCompat.from(this);
        if (!fingerprintManager.isHardwareDetected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("没有指纹功能")
                    .setCancelable(false)
                    .setMessage("该设备没有指纹识别功能")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.create().show();
        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
            // no fingerprint image has been enrolled.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("没有录入指纹");
            builder.setMessage("请去指纹库，录入指纹");
            builder.setIcon(android.R.drawable.stat_sys_warning);
            builder.setCancelable(false);
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            // show this dialog
            builder.create().show();
        } else {
            try {
                myAuthCallBack = new AuthCallBack(myHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showResult(String result) {
        resultTv.setText(result);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
//            SubscriptionManager manager = SubscriptionManager.from(this);
//            List<SubscriptionInfo> list = manager.getActiveSubscriptionInfoList();
//            resultTv.setText(list.size()+"haha");
//        }


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                cancelBtn.setEnabled(true);
                okBtn.setEnabled(false);
                soterClient.prepare();
                soterClient.startAuth();
//                try {
//                    CryptoObjectHelper helper = new CryptoObjectHelper();
//                    if (cancellationSignal == null) {
//                        cancellationSignal = new android.support.v4.os.CancellationSignal();
//                    }
//                    fingerprintManager.authenticate(helper.buildCryptoObject(), 0,
//                            cancellationSignal,  myAuthCallBack, null );
//                } catch (KeyStoreException e) {
//                    e.printStackTrace();
//                } catch (CertificateException e) {
//                    e.printStackTrace();
//                } catch (NoSuchAlgorithmException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (UnrecoverableKeyException e) {
//                    e.printStackTrace();
//                } catch (NoSuchProviderException e) {
//                    e.printStackTrace();
//                } catch (InvalidAlgorithmParameterException e) {
//                    e.printStackTrace();
//                }

                break;
            case R.id.btn_cancel:
                okBtn.setEnabled(true);
                cancelBtn.setEnabled(false);
                soterClient.cancel();
//                cancellationSignal.cancel();
//                cancellationSignal = null;
                break;
            default:
                break;
        }
    }

    class MyHandler extends Handler {
        WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity mainActivity) {
            this.mActivity = new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if(activity != null) {
                switch (msg.what) {
                    case Constants.FINGER_AUTH_ERROR:
                        activity.showResult((String) msg.obj);
                        break;
                    case Constants.FINGER_AUTH_FAILED:
                        activity.showResult("指纹验证失败");
                        break;
                    case Constants.FINGER_AUTH_HELP:
                        activity.showResult((String) msg.obj);
                        break;
                    case Constants.FINGER_AUTH_SUCCESS:
                        activity.showResult("指纹验证成功");
                        break;
                }
            }
        }
    }
}
