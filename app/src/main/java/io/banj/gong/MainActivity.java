package io.banj.gong;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import io.banj.gong.models.GongModel;
import io.banj.gong.models.GongRecyclerAdapter;

public class MainActivity extends AppCompatActivity {

    //network stuff

    boolean toastDebug = false;

    long touchTime = Long.MAX_VALUE;

    public static final String NETWORK_STUFF = "http://67.168.109.2";
    public static final String LOCAL_NETWORK_STUFF = "http://192.168.1.101";

    public static final int NETWORK_STUFF_PORT = 1337;
    public static final int LOCAL_NETWORK_STUFF_PORT = 4664;


    private static final float UP_GONG_BASE_DURATION = 600;

    float[] gongScalarSequenceUp = {1, 1.1f, 1.25f, 1.1f, 1};
    float[] gongScalarSequenceDown = {1, .8f,};

    boolean longGong = false;
    long touchDurration = 0;


    ObjectAnimator scaleElevationAnimator;
    View topLevelView;
    CardView gong;
    MediaPlayer mp;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    GongRecyclerAdapter mRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mp = MediaPlayer.create(this, R.raw.taco_bell_bong);

        gong = findViewById(R.id.gong);

        mRecyclerView = findViewById(R.id.recyclerView);

        setupTopLevelGesture();

        setupGong();

        setupRecycler();

    }


    float unprocessedX = -1;

    void setupTopLevelGesture(){

        topLevelView = findViewById(R.id.top_frame);

        topLevelView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                    unprocessedX = motionEvent.getX();
                }

                if(motionEvent.getAction() == MotionEvent.ACTION_MOVE){

//                    ((TextView)findViewById(R.id.text)).setText("unprocessedX: " + unprocessedX );

                    gong.setTranslationX(gong.getTranslationX() + (motionEvent.getX() - unprocessedX)*1.1f);
                    mRecyclerView.setTranslationX(mRecyclerView.getTranslationX() + (motionEvent.getX() - unprocessedX) * 1.5f);


                    unprocessedX = motionEvent.getX();


                    return false;
                }



                if(motionEvent.getAction() == MotionEvent.ACTION_UP){

                    settleViews();
                    return true;
                }

                return true;

            }
        });



    }

    void settleViews(){




    }

    void setupRecycler() {

        GongModel.staticGenerateFakeData();

        mRecyclerAdapter = new GongRecyclerAdapter(MainActivity.this, GongModel.gongs);

        mRecyclerView.setAdapter(mRecyclerAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));


    }

    @SuppressLint("ClickableViewAccessibility")
    void setupGong() {

        scaleElevationAnimator = ObjectAnimator.ofFloat(gong, "scaleX", gongScalarSequenceDown);
        scaleElevationAnimator.setInterpolator(new FastOutLinearInInterpolator());
        scaleElevationAnimator.setDuration((long) UP_GONG_BASE_DURATION);

        scaleElevationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                gong.setElevation(((float) valueAnimator.getAnimatedValue() * 2 + Math.min(10, Math.max(2, touchDurration / 100))) * densityCoefficient());
                gong.setScaleY((float) valueAnimator.getAnimatedValue());
            }
        });

//        gong.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//
//                longGong = true;
//                return false;
//            }
//        });


        gong.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {


                    scaleElevationAnimator.setDuration((long) ((long) UP_GONG_BASE_DURATION / 1.5));

                    gongScalarSequenceDown[0] = gong.getScaleX();
                    scaleElevationAnimator.setFloatValues(gongScalarSequenceDown);

                    if(!scaleElevationAnimator.isRunning()){
                        if(toastDebug)
                            Toast.makeText(MainActivity.this, "not running sttung true", Toast.LENGTH_SHORT).show();
                        scaleElevationAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                longGong = true;
                                scaleElevationAnimator.removeAllListeners();
                                if(toastDebug)
                                Toast.makeText(MainActivity.this, "animation ended!", Toast.LENGTH_SHORT).show();


                            }
                        });
                    }else{
                        if(toastDebug)
                            Toast.makeText(MainActivity.this, "not running sttung true", Toast.LENGTH_SHORT).show();

                    }

                    scaleElevationAnimator.start();


                }

                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP) {
                    if (longGong) {


                        gongScalarSequenceUp[0] = gong.getScaleX();
                        scaleElevationAnimator.setFloatValues(gongScalarSequenceUp);
                        scaleElevationAnimator.setDuration((long) ((UP_GONG_BASE_DURATION / 1.5 / .2) * Math.max(.2f, (gongScalarSequenceUp[1] - gongScalarSequenceUp[0]))));
                        scaleElevationAnimator.setInterpolator(new FastOutSlowInInterpolator());
                        ((TextView) findViewById(R.id.text)).setText("" + scaleElevationAnimator.getDuration() + "ms");


                        longGong = false;

//                        mp.start();

                        scaleElevationAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AsyncNetwork(false).execute();


                                        Snackbar.make(findViewById(R.id.top_frame), "You just sent a", Snackbar.LENGTH_LONG).setAction("GONG (Local)", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                new AsyncNetwork(true).execute();

                                            }
                                        }).show();

                                    }
                                }, 250);
                                scaleElevationAnimator.removeAllListeners();
                                super.onAnimationEnd(animation);
                            }
                        });

                        scaleElevationAnimator.start();


                    } else {

                        scaleElevationAnimator.setFloatValues(1f);
                        scaleElevationAnimator.start();
                        scaleElevationAnimator.setDuration((long) ((UP_GONG_BASE_DURATION / .2) * Math.max(.2f, (gongScalarSequenceUp[1] - gongScalarSequenceUp[0]))));


                    }
                }


                return false;
            }
        });


    }

    float densityCoefficient() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.density;
    }


    public class AsyncNetwork extends AsyncTask<Void, Void, String>{

        private boolean local = false;

        public AsyncNetwork(boolean local){
            this.local = local;
        }

        @Override
        protected String doInBackground(Void... voids) {

            if(!local) {
                getJSONFrom(NETWORK_STUFF + ":" + NETWORK_STUFF_PORT + "/gong");
            }else{
                getJSONFrom(LOCAL_NETWORK_STUFF + ":" + LOCAL_NETWORK_STUFF_PORT + "/gong");
            }


            return null;
        }
    }


    public static String getJSONFrom(String requestURL ) {
        Log.e("IP", "requesturl: " + requestURL);

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(2000);
            conn.setConnectTimeout(2000);
//            conn.setRequestProperty ("Accept", EVENTI_API_SELECT);

            conn.setRequestMethod("POST");

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = responseCode + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

}
