package com.example.aabtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.BuildCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.play.core.splitinstall.SplitInstallHelper;
import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final String DYNAMIC_FEATURE = "EnglishPackage";

    private SplitInstallManager splitInstallManager;
    private SplitInstallRequest request;

    private Dialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(this);
        TextView textView = findViewById(R.id.textView);

        Log.d("Yanyao", getResources().getConfiguration().locale.toString());
        textView.setText(R.string.Text);

        splitInstallManager = SplitInstallManagerFactory.create(this);

        loading = createDialog();
        request = SplitInstallRequest.newBuilder()
                .addModule(DYNAMIC_FEATURE)
                .build();
        initSplitManager();
    }

    private Dialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you need something amazing?")
                .setPositiveButton("Yes!!Yes!!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        splitInstallManager.startInstall(request)
                                .addOnSuccessListener(new OnSuccessListener<Integer>() {
                                    @Override
                                    public void onSuccess(Integer integer) {
                                        Toast.makeText(MainActivity.this, "成功下载插件", Toast.LENGTH_SHORT).show();
                                        onSuccessfulLoad();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                })
                                .addOnCompleteListener(new OnCompleteListener<Integer>() {
                                    @Override
                                    public void onComplete(Task<Integer> task) {
                                    }
                                });
                    }
                }).setNegativeButton("Sorry~ I do not.", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    private void onSuccessfulLoad() {
        try {
            Intent intent = new Intent(this, Class.forName("com.example.englishpackage.EnglishActivity"));
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            Toast.makeText(MainActivity.this, "跳转失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        loading.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "动态模块处于运行期");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "动态模块处于暂停期");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "动态模块处于重新运行期");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "动态模块处于销毁期");
    }

    private void initSplitManager(){
        splitInstallManager.registerListener(new SplitInstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(SplitInstallSessionState splitInstallSessionState) {
                if (splitInstallSessionState.status() == SplitInstallSessionStatus.INSTALLED) {
                    Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                } else if (splitInstallSessionState.status() == SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION) {
                    try {
                        startIntentSender(splitInstallSessionState.resolutionIntent().getIntentSender(),
                                null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                } else if (splitInstallSessionState.status() == SplitInstallSessionStatus.DOWNLOADING) {
                    long totalBytes = splitInstallSessionState.totalBytesToDownload();
                    long progress = splitInstallSessionState.bytesDownloaded();
                }
            }
        });
    }
}
