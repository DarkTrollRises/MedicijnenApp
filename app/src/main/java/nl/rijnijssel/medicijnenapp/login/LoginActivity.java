package nl.rijnijssel.medicijnenapp.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import nl.rijnijssel.medicijnenapp.AuthActivity;
import nl.rijnijssel.medicijnenapp.R;
import nl.rijnijssel.medicijnenapp.mysql.MysqlLoginTask;

/**
 * Created by Rik on 8-12-2015.
 */
public class LoginActivity extends AppCompatActivity
{
    private EditText etUsername, etPassword;
    private Button buttonLogin;

    /**
     * Method called when this activity gets created
     */
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try
        {
            SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_name), Context.MODE_PRIVATE);
            if(preferences != null && preferences.getInt(getString(R.string.preference_patient_id), 0) != 0 && !preferences.getString(getString(R.string.preference_patient_name), "").equals(""))
            {
                Intent intent = new Intent(this, AuthActivity.class);
                intent.putExtra("EXTRA_PATIENT_ID", preferences.getInt(getString(R.string.preference_patient_id), 0));
                intent.putExtra("EXTRA_PATIENT_NAME", preferences.getString(getString(R.string.preference_patient_name), ""));
                startActivity(intent);
                finish();
            }
            else
                setLogin();
        }
        catch(Exception e)
        {
            setLogin();
        }
    }

    /**
     * Show the "login failed" prompt
     */
    public void login()
    {
        new AlertDialog.Builder(this).setTitle(getString(R.string.alert_title)).setMessage(getString(R.string.error_invalid_username_password))
                .create().show();
    }

    /**
     * Log the user into the device
     *
     * @param patientID id of the logged in user
     * @param name name of the logged in user
     */
    public void login(int patientID, String name)
    {
        Intent intent = new Intent(this, AuthActivity.class);
        intent.putExtra("EXTRA_PATIENT_ID", patientID);
        intent.putExtra("EXTRA_PATIENT_NAME", name);
        startActivity(intent);
        finish();
    }

    /**
     * Set all the properties for the login fields and button
     */
    private void setLogin()
    {
        class TextChangedListener implements TextWatcher
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        }

        etUsername = ((EditText) findViewById(R.id.etUserName));
        etPassword = ((EditText) findViewById(R.id.etPassword));

        etUsername.addTextChangedListener(new TextChangedListener());
        etPassword.addTextChangedListener(new TextChangedListener());

        final Context context = this;
        buttonLogin = ((Button) findViewById(R.id.buttonLogin));
        buttonLogin.setEnabled(false);
        buttonLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MysqlLoginTask loginTask = new MysqlLoginTask(context);
                loginTask.execute(etUsername.getText().toString(), etPassword.getText().toString());
            }
        });
    }

    /**
     * Check if all inputs are filled in
     */
    private void checkInputs()
    {
        if(!etUsername.getText().toString().equals("") && !etPassword.getText().toString().equals(""))
        {
            buttonLogin.setBackground(getResources().getDrawable(R.drawable.button_login));
            buttonLogin.setEnabled(true);
        }
        else
        {
            buttonLogin.setBackground(getResources().getDrawable(R.drawable.button_login_dim));
            buttonLogin.setEnabled(false);
        }
    }
}

