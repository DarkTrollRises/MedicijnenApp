package nl.rijnijssel.medicijnenapp.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import nl.rijnijssel.medicijnenapp.R;
import nl.rijnijssel.medicijnenapp.medicine.Medicine;

/**
 * Created by Rik on 3-12-2015.
 */
public class AlarmActivity extends AppCompatActivity
{
    private ArrayList<CheckBox> cbList;
    private Button buttonConfirm;
    private AlarmManager am;
    private long time;

    /**
     * Method to run when this activity is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_alarm);

        am = ((AlarmManager) getSystemService(Context.ALARM_SERVICE));

        TextView tvClock = ((TextView) findViewById(R.id.tvClock));
        GregorianCalendar cal = new GregorianCalendar();
        tvClock.setText(String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));

        LinearLayout medLayout = ((LinearLayout) findViewById(R.id.medsLayout));
        final Bundle extras = getIntent().getExtras();
        ArrayList<Medicine> medList = extras.getParcelableArrayList("EXTRA_MEDICINE");
        ArrayList<Integer> amountList = extras.getIntegerArrayList("EXTRA_AMOUNT");
        cbList = new ArrayList<>();

        Uri alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        final Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmTone);
        ringtone.setStreamType(AudioManager.STREAM_ALARM);
        ringtone.play();

        /**
         * Set a timer to stop the alarm after 5 minutes and make it run again 10 minutes later
         */
        final Thread timer = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(1000*60*5); // 5 minutes
                    ringtone.stop();
                    if(extras.getInt("EXTRA_TIMES") > 1)
                    {
                        Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
                        extras.putInt("EXTRA_TIMES", extras.getInt("EXTRA_TIMES")-1);
                        time = extras.getLong("EXTRA_TIME") + (1000 * 60 * 10); // 10 minutes
                        extras.putLong("EXTRA_TIME", time);
                        alarmIntent.putExtras(extras);
                        PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(), extras.getInt("EXTRA_REQUEST"), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        am.setExact(AlarmManager.RTC_WAKEUP, extras.getLong("EXTRA_TIME"), pIntent);
                    }
                    finish();
                }
                catch(InterruptedException e)
                {
                }
            }
        });
        timer.start();

        buttonConfirm = ((Button) findViewById(R.id.buttonConfirm));
        buttonConfirm.setEnabled(false);
        buttonConfirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                timer.interrupt();
                ringtone.stop();
                RunningPendingIntents.list.remove(((int) time));
                finish();
            }
        });

        /**
         * Set the properties of all checkboxes
         */
        for(int i = 0; i < medList.size(); i++)
        {
            Medicine med = medList.get(i);
            int amount = amountList.get(i);

            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setGravity(Gravity.CENTER_HORIZONTAL);

            final CheckBox checkBox = new CheckBox(this);
            checkBox.setText("Ingenomen");
            checkBox.setTextColor(Color.BLACK);
            checkBox.setVisibility(View.GONE);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    checkBoxes();
                }
            });
            cbList.add(checkBox);

            Button medButton = new Button(this);
            medButton.setText(String.format("%s %d %s", med.getName(), amount, med.getUnit()));
            medButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(checkBox.getVisibility() == View.GONE)
                    {
                        checkBox.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        checkBox.setVisibility(View.GONE);
                        checkBox.setChecked(false);
                        //cbList.remove(checkBox);
                    }
                    checkBoxes();
                }
            });
            ll.addView(medButton);
            ll.addView(checkBox);
            medLayout.addView(ll);
        }
    }

    /**
     * Check the state of every checkbox to see if the button needs to be activated
     */
    private void checkBoxes()
    {
        boolean allChecked = true;

        if(cbList.size() != 0)
        {
            for(CheckBox cb : cbList)
                if(!cb.isChecked())
                {
                    allChecked = false;
                    break;
                }

            if(allChecked)
            {
                buttonConfirm.setEnabled(true);
                buttonConfirm.setBackground(getResources().getDrawable(R.drawable.button_confirm_alarm));
            }
            else
            {
                buttonConfirm.setEnabled(false);
                buttonConfirm.setBackground(getResources().getDrawable(R.drawable.button_confirm_alarm_dim));
            }
        }
    }
}
