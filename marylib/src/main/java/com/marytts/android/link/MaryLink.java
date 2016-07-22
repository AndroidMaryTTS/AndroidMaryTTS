package com.marytts.android.link;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import lib.sound.sampled.AudioInputStream;
import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.server.Mary;

public class MaryLink {

    private static MaryLink instance = null;
    protected MaryLink(Context context) {
        mContext = context;
        loadConfigs();
    }

    public static void load(Context context) {
        if(instance == null) {
            instance = new MaryLink(context);
        }
    }

    public static MaryLink getInstance() {
        return instance;
    }



    private String configs[] = {
            "marytts/config/marybase.config",
            "marytts/language/en/en.config",
            "marytts/voice/CmuSltHsmm/voice.config"
    };

    private final static HashMap<String, String> properties = new HashMap<>();


    private static Context mContext;

    private  MaryInterface marytts;


    private boolean m_stop = false;
    private final int sampleRate = 48000;
    private AudioTrack audioTrack;
    private int minSize = 8000;


    public static Context getContext() {
        return mContext;
    }


    public static HashMap<String, String> getProperties() {
        return properties;
    }





    public void startTTS(final String text) {
        m_stop = false;

        if (text != null && !text.isEmpty()) {
            startPlayer();

            final Thread thread2 = new Thread(new Runnable() {
                public void run() {
                    String tt= text;
                    if (tt.length()> 200)
                        tt=tt.substring(0,200);
                    generateSound(tt);
                }
            });
            thread2.start();
        }
    }

    private void generateSound(String text) {
        try {
            if (marytts != null) {

                AudioInputStream inputStream = marytts.generateAudio(text);
                Thread.currentThread().sleep(1001);
                if (inputStream != null && !m_stop) {
                    byte[] noiseData;
                    while (inputStream.read(noiseData = new byte[minSize], 0, noiseData.length) > -1) {
                        audioTrack.write(noiseData, 0, noiseData.length);
                    }

                    audioTrack.stop();
                    audioTrack.release();
                    audioTrack = null;
                }
            } else {
                Toast.makeText(MaryLink.getContext(), "Waiting marytts loading ...", Toast.LENGTH_LONG).show();
            }
        } catch (Throwable t) {
            Log.d("Error ... ", t.toString());
            m_stop = false;
        }
    }


    public void stopTTS() {
        m_stop = true;
        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.release();
        }
    }

    private void startPlayer() {
        minSize = AudioTrack.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, minSize,
                AudioTrack.MODE_STREAM);
        audioTrack.play();
    }


    private  void loadConfigs() {
        try {
            String line;

            for (String path : configs) {
                BufferedReader confFile = new BufferedReader(new InputStreamReader(mContext.getAssets().open(path)));
                String key = "", value = "";
                while ((line = confFile.readLine()) != null) {

                    if (line.trim().equals("") || line.startsWith("#"))
                        continue;

                    if (line.contains("=") && line.split("=").length > 1) {
                        key = line.split("=")[0].trim();
                        value = line.split("=")[1].trim();
                        MaryLink.getProperties().put(key, value);
                    } else {
                        value = MaryLink.getProperties().get(key);
                        value += line.trim();
                        MaryLink.getProperties().put(key, value);
                    }
                }
                confFile.close();
            }

            MaryTTSinit maryTTSinit = new MaryTTSinit();
            if (Build.VERSION.SDK_INT >= 11) {
                maryTTSinit.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                maryTTSinit.execute();
            }

        } catch (Throwable e) {
            Log.e(Mary.LOG + "+_+_+_+", e.toString());
        }
    }

    private  class MaryTTSinit extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (marytts == null) {
                    marytts = new LocalMaryInterface();
                    Set<String> voices = marytts.getAvailableVoices();
                    marytts.setLocale(Locale.US);
                    // marytts.setInputType("RAWMARYXML");
                    marytts.setVoice(voices.iterator().next());
                }
            } catch (Exception e) {
                marytts = null;
                Log.d(Mary.LOG, e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}