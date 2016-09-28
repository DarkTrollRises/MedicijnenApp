package nl.rijnijssel.medicijnenapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;

import nl.rijnijssel.medicijnenapp.alarm.Alarm;
import nl.rijnijssel.medicijnenapp.alarm.AlarmReceiver;
import nl.rijnijssel.medicijnenapp.alarm.RequestIntent;
import nl.rijnijssel.medicijnenapp.alarm.RunningPendingIntents;
import nl.rijnijssel.medicijnenapp.edit.AddActivity;
import nl.rijnijssel.medicijnenapp.edit.ChangeActivity;
import nl.rijnijssel.medicijnenapp.edit.Day;
import nl.rijnijssel.medicijnenapp.edit.DayEnum;
import nl.rijnijssel.medicijnenapp.gcm.RegistrationIntentService;
import nl.rijnijssel.medicijnenapp.login.LoginActivity;
import nl.rijnijssel.medicijnenapp.medicine.Medicine;
import nl.rijnijssel.medicijnenapp.mysql.MysqlDeleteUseTask;
import nl.rijnijssel.medicijnenapp.mysql.MysqlGcmUnRegisterTask;
import nl.rijnijssel.medicijnenapp.mysql.MysqlGetUseTask;

/**
 *  Created by Rik on 7-10-2015.
 */
public class AuthActivity extends AppCompatActivity
{
    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    private static final int REQUEST_CODE_ALARM_ADD_SUCCESSFUL = 1002;
    private static final int REQUEST_CODE_ALARM_CHANGE_SUCCESSFUL = 1003;

    private int patientID;
    private BroadcastReceiver broadcastReceiver;
    private final Context context = this;
    private ImageView medImage;
    private Button infoButton, changeButton, deleteButton;
    private ImageButton refreshButton;
    private boolean dim = true;
    private ArrayAdapter<Alarm> adapter;
    private Alarm selectedAlarm;
    private TextView tvMedInfo;
    private ListView medView;
    private Dialog dialog;

    /**
     * Called when the activity is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bundle extras = getIntent().getExtras();

        final SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_name), Context.MODE_PRIVATE);
        preferences.edit().putInt(getString(R.string.preference_patient_id), extras.getInt("EXTRA_PATIENT_ID")).apply();
        preferences.edit().putString(getString(R.string.preference_patient_name), extras.getString("EXTRA_PATIENT_NAME")).apply();
        patientID = preferences.getInt(getString(R.string.preference_patient_id), 0);

        TextView tvName = ((TextView) findViewById(R.id.textViewName));
        tvName.setText("Hallo " + extras.getString("EXTRA_PATIENT_NAME"));

        Calendar cal = Calendar.getInstance();
        String day;
        switch(cal.get(Calendar.DAY_OF_WEEK))
        {
            case Calendar.MONDAY:
                day = "Maandag";
                break;
            case Calendar.TUESDAY:
                day = "Dinsdag";
                break;
            case Calendar.WEDNESDAY:
                day = "Woensdag";
                break;
            case Calendar.THURSDAY:
                day = "Donderdag";
                break;
            case Calendar.FRIDAY:
                day = "Vrijdag";
                break;
            case Calendar.SATURDAY:
                day = "Zaterdag";
                break;
            case Calendar.SUNDAY:
                day = "Zondag";
                break;
            default:
                day = "Maandag";
        }

        String month;
        switch(cal.get(Calendar.MONTH))
        {
            case Calendar.JANUARY:
                month = "Januari";
                break;
            case Calendar.FEBRUARY:
                month = "Februari";
                break;
            case Calendar.MARCH:
                month = "Maart";
                break;
            case Calendar.APRIL:
                month = "April";
                break;
            case Calendar.MAY:
                month = "Mei";
                break;
            case Calendar.JUNE:
                month = "Juni";
                break;
            case Calendar.JULY:
                month = "Juli";
                break;
            case Calendar.AUGUST:
                month = "Augustus";
                break;
            case Calendar.SEPTEMBER:
                month = "September";
                break;
            case Calendar.OCTOBER:
                month = "October";
                break;
            case Calendar.NOVEMBER:
                month = "November";
                break;
            case Calendar.DECEMBER:
                month = "December";
                break;
            default:
                month = "Januari";
                break;

        }
        ((TextView) findViewById(R.id.textDate)).setText(String.format("%s %02d %s %04d", day, cal.get(Calendar.DAY_OF_MONTH), month, cal.get(Calendar.YEAR)));

        if(checkPlayServices())
        {
            broadcastReceiver = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent)
                {
                    if(!preferences.getBoolean(getString(R.string.preference_token_sent), false))
                    {
                        Toast.makeText(context, "Kan niet registreren bij server", Toast.LENGTH_LONG).show();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }
            };

            if(!preferences.getBoolean(getString(R.string.preference_token_sent), false))
            {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }

            medImage = ((ImageView) findViewById(R.id.imageViewMedicine));
            //TODO add image

            infoButton = (Button) findViewById(R.id.buttonInfo);
            infoButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //TODO add info
                }
            });

            changeButton = (Button) findViewById(R.id.buttonChange);
            changeButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(context, ChangeActivity.class);
                    intent.putExtra("EXTRA_ALARM", selectedAlarm);
                    ((AuthActivity) context).startActivityForResult(intent, REQUEST_CODE_ALARM_CHANGE_SUCCESSFUL);
                }
            });

            Button addButton = ((Button) findViewById(R.id.buttonAdd));
            addButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(context, AddActivity.class);
                    ((AuthActivity) context).startActivityForResult(intent, REQUEST_CODE_ALARM_ADD_SUCCESSFUL);
                }
            });

            deleteButton = ((Button) findViewById(R.id.buttonDelete));
            deleteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            switch(which)
                            {
                                case DialogInterface.BUTTON_POSITIVE:
                                    MysqlDeleteUseTask deleteUseTask = new MysqlDeleteUseTask(context);
                                    deleteUseTask.execute(String.valueOf(selectedAlarm.getId()), String.valueOf(patientID));
                                    Alarm.removeAlarm(selectedAlarm);
                                    dialog.dismiss();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setPositiveButton("Ja", dialogOnClickListener);
                    dialog.setNegativeButton("Nee", dialogOnClickListener);
                    dialog.setTitle("Verwijderen");
                    dialog.setMessage("Weet u zeker dat u dit alarm wilt verwijderen?");
                    dialog.show();
                }
            });

            refreshButton = ((ImageButton) findViewById(R.id.buttonRefresh));
            refreshButton.setEnabled(false);
            refreshButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Alarm.list.clear();
                    adapter.notifyDataSetChanged();
                    refreshAlarms();
                    refreshButton.setEnabled(false);
                }
            });

            ImageButton logoutButton = ((ImageButton) findViewById(R.id.buttonLogout));
            logoutButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            switch(which)
                            {
                                case DialogInterface.BUTTON_POSITIVE:
                                    MysqlGcmUnRegisterTask gcmUnRegisterTask = new MysqlGcmUnRegisterTask(context);
                                    gcmUnRegisterTask.execute(String.valueOf(patientID));
                                    Alarm.list.clear();
                                    RequestIntent.list.clear();
                                    AlarmManager am = ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
                                    Intent alarmIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
                                    for(int i: RunningPendingIntents.list)
                                    {
                                        PendingIntent pIntent = PendingIntent.getBroadcast(context.getApplicationContext(), i, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                                        am.cancel(pIntent);
                                    }
                                    RunningPendingIntents.list.clear();
                                    adapter.notifyDataSetChanged();
                                    preferences.edit().clear().apply();
                                    dialog.dismiss();
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setPositiveButton("Ja", dialogOnClickListener);
                    dialog.setNegativeButton("Nee", dialogOnClickListener);
                    dialog.setTitle("Uitloggen");
                    dialog.setMessage("Weet u zeker dat u wilt uitloggen?\n(Dit betekend dat u geen alarmen meer zult krijgen!)");
                    dialog.show();
                }
            });

            if(Alarm.list.isEmpty())
                refreshAlarms();

            tvMedInfo = ((TextView) findViewById(R.id.textViewMedInfo));
            medView = ((ListView) findViewById(R.id.lvMedicine));
            adapter = new ArrayAdapter<>(context, R.layout.custom_list_item, Alarm.list);
            medView.setAdapter(adapter);

            medView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    if(dim)
                    {
                        //infoButton.setBackground(getResources().getDrawable(R.drawable.button_info));
                        //infoButton.setEnabled(true);

                        changeButton.setBackground(getResources().getDrawable(R.drawable.button_change));
                        changeButton.setEnabled(true);

                        deleteButton.setBackground(getResources().getDrawable(R.drawable.button_delete));
                        deleteButton.setEnabled(true);

                        dim = false;
                    }

                    Alarm alarm = ((Alarm) parent.getItemAtPosition(position));
                    setSelectedAlarm(alarm, view);
                }
            });
        }
    }

    /**
     * Called when the activity is resumed
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(getString(R.string.broadcast_registration_complete)));
    }

    /**
     * Called when the activity is paused
     */
    @Override
    protected void onPause()
    {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    /**
     * Set the medicine of a specific alarm in the list
     *
     * @param index index to set at
     * @param medicine medicine to set
     * @param selectedAlarmID current selected alarm id
     */
    public void setAlarmMedicine(int index, Medicine medicine, int selectedAlarmID)
    {
        if(medicine != null)
        {
            Alarm alarm = Alarm.getAlarm(index);
            alarm.setMedicine(medicine);
            if(alarm.getEndDate().startsWith("0002"))
                alarm.setEndDate(new GregorianCalendar(1970, 0, 1));
            if(alarm.getId() == selectedAlarmID)
                setSelectedAlarm(alarm);
            adapter.notifyDataSetChanged();
        }
        if(!refreshButton.isEnabled())
            refreshButton.setEnabled(true);
        dialog.dismiss();
    }

    /**
     * Check if google play services is installed and updated
     *
     * @return true if google play services is installed and updated
     */
    private boolean checkPlayServices()
    {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(result != ConnectionResult.SUCCESS)
        {
            if(GooglePlayServicesUtil.isUserRecoverableError(result))
                showErrorDialog(result);
            else
                Toast.makeText(this, getString(R.string.play_services_not_supported), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * Show google play services error dialog
     *
     * @param errorCode code of the error
     */
    private void showErrorDialog(int errorCode)
    {
        GooglePlayServicesUtil.getErrorDialog(errorCode, this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
    }

    /**
     * Set the current selected alarm
     *
     * @param alarm alarm to be selected
     */
    public void setSelectedAlarm(Alarm alarm)
    {
        if(alarm == null)
        {
            medView.clearChoices();
            medView.setAdapter(adapter);
            selectedAlarm = null;

            //infoButton.setBackground(getResources().getDrawable(R.drawable.button_info_dim));
            //infoButton.setEnabled(false);
            changeButton.setBackground(getResources().getDrawable(R.drawable.button_change_dim));
            changeButton.setEnabled(false);
            deleteButton.setBackground(getResources().getDrawable(R.drawable.button_delete_dim));
            deleteButton.setEnabled(false);
            dim = true;

            tvMedInfo.setText("");
        }
        else
        {
            selectedAlarm = alarm;

            StringBuilder dayString = new StringBuilder();
            for(DayEnum day : alarm.getDaysEnum())
            {
                dayString.append(" ");
                dayString.append(Day.getShortStringFromEnum(day));
            }

            String endDate;
            if(alarm.getEndDate().startsWith("1970"))
                endDate = "Oneindig";
            else
                endDate = alarm.getRegularEndDate();

            tvMedInfo.setText(String.format("%s\n%d %s - %s\n%s\n%s  ->  %s", alarm.getMedicine().getName(), alarm.getAmount(),
                    alarm.getMedicine().getUnit(), alarm.timeToString(), dayString.toString(), alarm.getRegularStartDate(), endDate));
        }
    }

    /**
     * Select specific alarm from a specific view
     *
     * @param alarm alarm to be selected
     * @param view view to select from
     */
    public void setSelectedAlarm(Alarm alarm, View view)
    {
        selectedAlarm = alarm;
        view.setSelected(true);

        StringBuilder dayString = new StringBuilder();
        for(DayEnum day : alarm.getDaysEnum())
        {
            dayString.append(" ");
            dayString.append(Day.getShortStringFromEnum(day));
        }

        String endDate;
        if(alarm.getEndDate().startsWith("1970"))
            endDate = "Oneindig";
        else
            endDate = alarm.getRegularEndDate();
        tvMedInfo.setText(String.format("%s\n%d %s - %s\n%s\n%s  ->  %s", alarm.getMedicine().getName(), alarm.getAmount(),
                alarm.getMedicine().getUnit(), alarm.timeToString(), dayString.toString(), alarm.getRegularStartDate(), endDate));
    }

    /**
     * Refresh the alarms in the list
     */
    private void refreshAlarms()
    {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_dialog_layout);
        LinearLayout llLoading = ((LinearLayout) dialog.findViewById(R.id.llLoadingBar));
        dialog.setContentView(llLoading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        MysqlGetUseTask getUseTask = new MysqlGetUseTask(this);
        getUseTask.execute("all", String.valueOf(patientID));
    }

    /**
     * Called when an activity returns a result to this one
     *
     * @param requestCode unique request code
     * @param resultCode if it was successful
     * @param data extra data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode)
        {
            case REQUEST_CODE_RECOVER_PLAY_SERVICES:
                if(resultCode == RESULT_CANCELED)
                {
                    Toast.makeText(this, getString(R.string.play_services_not_installed), Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            case REQUEST_CODE_ALARM_ADD_SUCCESSFUL:
                if(resultCode == RESULT_OK)
                {
                    Alarm alarm = data.getParcelableExtra("EXTRA_ALARM");
                    alarm.setContext(this);
                    if(alarm.getEndDate().startsWith("0002"))
                        alarm.setEndDate(new GregorianCalendar(1970, 0, 1));
                    Alarm.addAlarm(alarm);
                    adapter.notifyDataSetChanged();
                }
                return;
            case REQUEST_CODE_ALARM_CHANGE_SUCCESSFUL:
                if(resultCode == RESULT_OK)
                {
                    Alarm.list.clear();
                    MysqlGetUseTask getUseTask = new MysqlGetUseTask(this);
                    getUseTask.execute("all", String.valueOf(patientID), String.valueOf(selectedAlarm.getId()));
                }
                return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
