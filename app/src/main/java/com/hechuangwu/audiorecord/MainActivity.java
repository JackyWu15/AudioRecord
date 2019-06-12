package com.hechuangwu.audiorecord;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
    }

    public void record(View view) {
        if (PermissionsUtil.hasPermission( MainActivity.this, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE )) {
            startActivity( new Intent( this,AudioActivity.class ) );
        } else {
            PermissionsUtil.requestPermission( MainActivity.this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
                    startActivity( new Intent( MainActivity.this,AudioActivity.class ) );
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
                }
            }, new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE} );
        }
    }
}
