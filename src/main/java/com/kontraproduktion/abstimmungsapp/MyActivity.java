package com.kontraproduktion.abstimmungsapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Vector;


public class MyActivity extends Activity {
    private String TAG = "MyActivity";
    private Vector<Integer> clicks = new Vector<Integer>();
    private String mood = "";
    private Button lastPressed = null;
    private Animation anim;
    private Animation re_anim;
    private Vibrator vib;
    private ImageButton send;
    private Button question_btn;


    private void clickFeedback(View v) {
        Button[] buttons = {(Button) findViewById(R.id.button_dg), (Button) findViewById(R.id.button_lg),
                (Button) findViewById(R.id.button_y), (Button) findViewById(R.id.button_or), (Button) findViewById(R.id.button_dr)};

        vib.vibrate(120);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int i = 0;
        if(size.x > size.y) {
            // Horizontal
            i = (int) (v.getX() / (size.x/5));
        } else {
            // Vertical
            i = (int) (v.getY() / (size.y/5));
        }
        Log.d(TAG, "Touch button " + i + " y is " + v.getY());
        // clicks.set(i, clicks.get(i)+1);
        mood = "" + buttons[i].getText();
        // Setting send button to overlay the current buttoin
        v.clearAnimation();
        v.startAnimation(anim);
        if(lastPressed != null) lastPressed.clearAnimation();
        lastPressed = (Button) v;

        // Setting send to overlay button
        send.setX(v.getX() - 1);
        send.setY(v.getY() - 1);
        Log.d(TAG, "Setting send to "  + send.getX() + " and " + send.getY());
        Log.d(TAG, send.getParent().getClass().toString());
        send.setMaxHeight(v.getHeight() + 2);
        send.setMinimumHeight(v.getHeight() + 2);
        send.setMaxWidth(v.getWidth() + 2);
        send.setMinimumWidth(v.getWidth() + 2);

        send.setVisibility(View.VISIBLE);
        send.clearAnimation();
        send.startAnimation(re_anim);
        Log.d(TAG, "clicks is " + i + ": " + clicks.get(i));
    }

    // Create ui
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        String [] texts = {"\ud83d\ude03", "\ud83d\ude0A", "\ud83d\ude14", "\ud83d\ude20", "\ud83d\ude21"};
        Typeface font = Typeface.createFromAsset(getAssets(), "Symbola.ttf");

        anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        re_anim = AnimationUtils.loadAnimation(this, R.anim.alpha_back);
        vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        send = (ImageButton) findViewById(R.id.button_send);
        question_btn = (Button) findViewById(R.id.button_question);
        Button[] buttons = {(Button) findViewById(R.id.button_dg), (Button) findViewById(R.id.button_lg),
                (Button) findViewById(R.id.button_y), (Button) findViewById(R.id.button_or), (Button) findViewById(R.id.button_dr)};

        LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);

        main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When touched somewhere hide send button
                if(send.getVisibility() == View.GONE)
                    return;
                Log.d(TAG, "No button ");
                if(lastPressed != null) lastPressed.clearAnimation();
                send.setVisibility(View.GONE);
                send.clearAnimation();
                send.setAnimation(anim);
            }
        });

        final View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClickListener " + v.getId() );
                clickFeedback(v);
            }
        };

        for (int i = 0; i < buttons.length; i++) {
            clicks.add(i, 0);
            buttons[i].setText(texts[i]);
            buttons[i].setTypeface(font);
            buttons[i].setOnClickListener(clickListener);
            //buttons[i].setOnTouchListener(touchListener);
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vib.vibrate(120);
                v.clearAnimation();
                v.startAnimation(anim);

                Log.d(TAG, "Send btn clicked");
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mood);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        question_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Question btn clicked");
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.question_txt));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        LinearLayout layout = (LinearLayout) findViewById(R.id.main_layout);
        Button question_btn = (Button) findViewById(R.id.button_question);

        // Hiding send button on change
        findViewById(R.id.button_send).setVisibility(View.GONE);

        Button[] buttons = {(Button) findViewById(R.id.button_dg), (Button) findViewById(R.id.button_lg),
                (Button) findViewById(R.id.button_y), (Button) findViewById(R.id.button_or), (Button) findViewById(R.id.button_dr)};

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layout.setOrientation(LinearLayout.HORIZONTAL);
            for(int i = 0; i < buttons.length; i++) {
                buttons[i].setPadding(0, buttons[i].getPaddingLeft(), 0, buttons[i].getPaddingLeft());
            }
            question_btn.setGravity(Gravity.BOTTOM);
            question_btn.getLayoutParams().toString();
            Log.d(TAG, question_btn.getLayoutParams().toString());
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            layout.setOrientation(LinearLayout.VERTICAL);
            for(int i = 0; i < buttons.length; i++) {
                  buttons[i].setPadding(buttons[i].getPaddingTop(), 0, buttons[i].getPaddingTop(), 0);
            }
            question_btn.setGravity(Gravity.CENTER_VERTICAL);
        }
        Log.d(TAG, buttons[0].getWidth() + " changed " +  buttons[0].getHeight() );
    }
}
