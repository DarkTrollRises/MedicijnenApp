package nl.rijnijssel.medicijnenapp.gcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import nl.rijnijssel.medicijnenapp.R;
import nl.rijnijssel.medicijnenapp.mysql.MysqlGcmRegisterTask;

/**
 * Created by Rik on 21-10-2015.
 */
public class RegistrationIntentService extends IntentService
{
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    private SharedPreferences preferences;

    /**
     * Default constructor
     */
    public RegistrationIntentService() {
        super(TAG);
    }

    /**
     * Called when this activity gets activated via an intent
     *
     * @param intent intent which was called
     */
    @Override
    protected void onHandleIntent(Intent intent)
    {
        preferences = getSharedPreferences(getString(R.string.preference_name), Context.MODE_PRIVATE);

        try
        {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.sender_id), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(TAG, "GCM Registration Token: " + token);

            sendRegistrationToServers(token);

            subscribeTopics(token);

            preferences.edit().putBoolean(getString(R.string.preference_token_sent), true).apply();
        }
        catch(Exception e)
        {
            Log.d(TAG, "Failed to complete token refresh", e);

            preferences.edit().putBoolean(getString(R.string.preference_token_sent), false).apply();
        }

        Intent registrationComplete = new Intent(getString(R.string.broadcast_registration_complete));
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Sends the registration key to be saved for the current user
     *
     * @param token registration key
     */
    private void sendRegistrationToServers(String token)
    {
        MysqlGcmRegisterTask registerTask = new MysqlGcmRegisterTask(this);
        registerTask.execute(String.valueOf(preferences.getInt(getString(R.string.preference_patient_id), 0)), token);
    }

    /**
     * Subscribe the key to certain specific topics
     *
     * @param token registration key
     * @throws IOException
     */
    private void subscribeTopics(String token) throws IOException
    {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
}
