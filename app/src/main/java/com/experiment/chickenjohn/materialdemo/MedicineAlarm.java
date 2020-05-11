package com.experiment.chickenjohn.materialdemo;
import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MedicineAlarm extends AppCompatActivity {

    private Alarm currentAlarm;
    private ToggleButton toggleButton1;
    private EditText editNotes1;
    private EditText dateText1;
    private ToggleButton toggleButton2;
    private EditText editNotes2;
    private EditText dateText2;
    private ToggleButton toggleButton3;
    private EditText editNotes3;
    private EditText dateText3;
    private ToggleButton toggleButton4;
    private EditText editNotes4;
    private EditText dateText4;
    private PendingIntent pi;
    private BroadcastReceiver br;
    private AlarmManager am;
    TimePickerDialog timePicker;
    private Alarm[] alarms = new Alarm[10];
    private FirebaseAuth firebaseAuth;
    Calendar c = Calendar.getInstance();
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState);
        setContentView( R.layout.alarmsettings );
        firebaseAuth = FirebaseAuth.getInstance();
        initializeApp();
    }

    private void initializeApp() {
        am = (AlarmManager)( this.getSystemService( Context.ALARM_SERVICE ) );
        toggleButton1 = (ToggleButton)findViewById( R.id.toggleButton1 );
        editNotes1 = (EditText)findViewById( R.id.editNotes1 );
        dateText1 = (EditText)findViewById( R.id.editText1 );
        toggleButton2 = (ToggleButton)findViewById( R.id.toggleButton2 );
        editNotes2 = (EditText)findViewById( R.id.editNotes2 );
        dateText2 = (EditText)findViewById( R.id.editText2 );
        toggleButton3 = (ToggleButton)findViewById( R.id.toggleButton3 );
        editNotes3 = (EditText)findViewById( R.id.editNotes3 );
        dateText3 = (EditText)findViewById( R.id.editText3 );
        toggleButton4 = (ToggleButton)findViewById( R.id.toggleButton4 );
        editNotes4 = (EditText)findViewById( R.id.editNotes4 );
        dateText4 = (EditText)findViewById( R.id.editText4 );

        // BROADCAST RECEIVER
        br = new BroadcastReceiver() {
            @Override
            public void onReceive( Context context, Intent intent ) {
                String notes = "";
                Bundle extras = intent.getExtras();
                if( extras != null ) {
                    notes = extras.getString("notes");
                }
                Toast.makeText(MedicineAlarm.this, "Wake UP", Toast.LENGTH_SHORT).show();

                // Reschedule a new alarm if this is recurring
                createNotification( notes );
            }
        };
        // Register the receiver and create the intents for passing information
        registerReceiver( br, new IntentFilter( "com.experiment.chickenjohn.materialdemo" ) );

        // Create all of my Alarm Objects
        // TODO: Set date for each calendar from file
        alarms[0] = new Alarm( this, editNotes1, dateText1, toggleButton1, 0, Calendar.getInstance() );
        alarms[0].setTags();

        alarms[1] = new Alarm( this, editNotes2, dateText2, toggleButton2, 1, Calendar.getInstance() );
        alarms[1].setTags();

        alarms[2] = new Alarm( this, editNotes3, dateText3, toggleButton3, 2, Calendar.getInstance() );
        alarms[2].setTags();

        alarms[3] = new Alarm( this, editNotes4, dateText4, toggleButton4, 3, Calendar.getInstance() );
        alarms[3].setTags();
    }

    public void toggleAlarm( View v ) {
        if( v.getId() == toggleButton1.getId() ) {
            if( toggleButton1.isChecked() ) {
                alarms[0].setNotes( editNotes1.getText().toString() );
                am.set( AlarmManager.RTC, c.getTimeInMillis(), alarms[0].pi );
                Toast.makeText(MedicineAlarm.this, "Alarm1 On" , Toast.LENGTH_SHORT).show();


            } else {
                Toast.makeText(MedicineAlarm.this, "Alarm1 Off", Toast.LENGTH_SHORT).show();
            }
        } else if( v.getId() == toggleButton2.getId() ) {
            if( toggleButton2.isChecked() ) {
                alarms[1].setNotes( editNotes2.getText().toString() );
                am.set( AlarmManager.RTC, c.getTimeInMillis(), alarms[1].pi );
                Toast.makeText(MedicineAlarm.this, "Alarm2 On", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(MedicineAlarm.this, "Alarm2 Off", Toast.LENGTH_SHORT).show();

            }
        }
        else if( v.getId() == toggleButton3.getId() ) {
            if( toggleButton3.isChecked() ) {
                alarms[2].setNotes( editNotes3.getText().toString() );
                am.set( AlarmManager.RTC, c.getTimeInMillis(), alarms[2].pi );
                Toast.makeText(MedicineAlarm.this, "Alarm3 On", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(MedicineAlarm.this, "Alarm3 Off", Toast.LENGTH_SHORT).show();

            }
        }
        else if( v.getId() == toggleButton4.getId() ) {
            if( toggleButton4.isChecked() ) {
                alarms[3].setNotes( editNotes2.getText().toString() );
                am.set( AlarmManager.RTC, c.getTimeInMillis(), alarms[3].pi );
                Toast.makeText(MedicineAlarm.this, "Alarm4 On", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(MedicineAlarm.this, "Alarm4 Off", Toast.LENGTH_SHORT).show();

            }
        }
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet( DatePicker view, int year, int monthOfYear, int dayOfMonth ) {
            Alarm am = (Alarm)currentAlarm;

            am.cal.set( Calendar.YEAR, year );
            am.cal.set( Calendar.MONTH, monthOfYear );
            am.cal.set( Calendar.DAY_OF_MONTH, dayOfMonth );

            timePicker.show();  // Launches the TimePicker right after the DatePicker closes
        }
    };

    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet( TimePicker view, int hour, int minute ) {
            Alarm am = (Alarm)currentAlarm;
            am.cal.set( Calendar.HOUR, hour );
            am.cal.set( Calendar.MINUTE, minute );
            am.updateDateTime();
        }
    };

    public void dateOnClick( View view ) {
        Alarm am = (Alarm)view.getTag();
        currentAlarm = am;
        timePicker = new TimePickerDialog( MedicineAlarm.this, time,
                am.cal.get( Calendar.HOUR ),
                am.cal.get( Calendar.MINUTE ), false );
        new DatePickerDialog( MedicineAlarm.this, date,
                am.cal.get( Calendar.YEAR ),
                am.cal.get( Calendar.MONTH ),
                am.cal.get( Calendar.DAY_OF_MONTH ) ).show();
    }

    private void createNotification( String notes ) {
        // prepare intent which is triggered if the notification is selected
        Intent intent = new Intent( this, MedicineAlarm.class );
        PendingIntent pIntent = PendingIntent.getActivity( this, 0, intent, 0 );
        Notification n = new Notification.Builder( this )
                .setContentTitle( "Medicine Alarm" )
                .setContentText( notes )
                .setSmallIcon( R.drawable.app_icon )
                .setContentIntent( pIntent )
                .setAutoCancel( true )
                .build();
        NotificationManager notificationManager =
                (NotificationManager)getSystemService( NOTIFICATION_SERVICE );
        notificationManager.notify( 0, n );
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.med_main, menu);
        return true;
    }
    private void Logout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(MedicineAlarm.this, LoginPage.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MedicineAlarm.this, MainActivity.class));
        }
        if (id == R.id.logoutMenu) {
            Logout();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
