package nl.rijnijssel.medicijnenapp.gcm;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

import nl.rijnijssel.medicijnenapp.R;

/**
 * Created by Rik on 21-10-2015.
 */
public class InstanceIDListener extends InstanceIDListenerService
{
    /**
     * Called when the device receives a new GCM registration key
     */
    @Override
    public void onTokenRefresh()
    {
        getSharedPreferences(getString(R.string.preference_name), Context.MODE_PRIVATE).edit().putBoolean(getString(R.string.preference_token_sent), false).apply();
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
