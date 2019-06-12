package com.hechuangwu.audiorecord;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioActivity extends AppCompatActivity {

    private final int sampleRate = 44100;
    private boolean isRecording =false;
    private AudioRecord mAudioRecord;
    private Button mBt_record;
    private File mFile;
    private Button mBt_play;
    private boolean isPlaying;
    private AudioTrack mAudioTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_audio );
        mBt_record = findViewById( R.id.bt_record );
        mBt_play = findViewById( R.id.bt_play );

    }

    public void record(View view) {
        if(!isRecording) {
            mBt_record.setText( "停止" );
            final int minBufferSize = AudioRecord.getMinBufferSize( sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT );
            mAudioRecord = new AudioRecord( MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize );
            final byte[] buffer = new byte[minBufferSize];
            mFile = new File( Environment.getExternalStorageDirectory() + File.separator + "record_test.pcm" );
            if (mFile != null && mFile.exists()) {
                mFile.delete();
            }
            mAudioRecord.startRecording();
            isRecording = true;

            new Thread( new Runnable() {
                @Override
                public void run() {
                    FileOutputStream fileOutputStream = null;
                    try {
                        fileOutputStream = new FileOutputStream( mFile );
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (fileOutputStream != null) {
                        while (isRecording) {
                            int read = mAudioRecord.read( buffer, 0, minBufferSize );
                            if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                                try {
                                    fileOutputStream.write( buffer );
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } ).start();
        }else {
            if(mAudioRecord!=null){
                mBt_record.setText( "录音" );
                isRecording = false;
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
            }
        }

    }

    public void play(View view) {
        if (!isPlaying) {
            isPlaying = true;
            mBt_play.setText( "停止" );
            final int minBufferSize = AudioTrack.getMinBufferSize( sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT );
            mAudioTrack = new AudioTrack( new AudioAttributes.Builder().setUsage( AudioAttributes.USAGE_MEDIA ).setContentType( AudioAttributes.CONTENT_TYPE_MUSIC ).build(),
                    new AudioFormat.Builder().setSampleRate( sampleRate )
                            .setEncoding( AudioFormat.ENCODING_PCM_16BIT )
                            .setChannelMask( AudioFormat.CHANNEL_OUT_MONO )
                            .build(),
                    minBufferSize,
                    AudioTrack.MODE_STREAM,
                    AudioManager.AUDIO_SESSION_ID_GENERATE );
            mAudioTrack.play();
            try {
                final FileInputStream fileInputStream = new FileInputStream( mFile );
                new Thread( new Runnable() {
                    @Override
                    public void run() {
                        byte[] buffer = new byte[minBufferSize];
                        try {
                            while (fileInputStream.available() > 0) {
                                int readCount = fileInputStream.read( buffer );
                                if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
                                    continue;
                                }
                                if (readCount != 0 && readCount != -1) {
                                    mAudioTrack.write( buffer, 0, minBufferSize );
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } ).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }   else {
            if(mAudioTrack!=null){
                mBt_play.setText( "播放" );
                isPlaying = false;
                mAudioTrack.stop();
                mAudioTrack.release();
                mAudioTrack = null;
            }
        }
    }
}
