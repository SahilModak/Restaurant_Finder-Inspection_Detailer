package group17.cmpt276.iteration3.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import group17.cmpt276.iteration3.Model.CSV.CSVUpdater;
import group17.cmpt276.iteration3.Model.CSV.DatabaseInfo;
import group17.cmpt276.iteration3.R;

/*
  Display a spinning progressbar to indicate update is being completed
  User may cancel update to return to restaurant view with last updated data
 */
public class UpdateActivity extends AppCompatActivity {
    private static final String TAG = "Update";
    private DatabaseInfo databaseInfo;

    SharedPreferences sharedPreferences;

    private TextView updateTextView;
    private ProgressBar updateProgressBar;
    private Button continueButton;
    private Button cancelButton;
    private volatile boolean stopThread = false;


    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, UpdateActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseInfo = DatabaseInfo.getInstance();
        sharedPreferences = this.getSharedPreferences("shared preferences",MODE_PRIVATE);
        updateProgressBar = (ProgressBar) findViewById(R.id.progressBarUpdate);
        updateTextView = (TextView) findViewById(R.id.txtUpdating);

        setupContinueButton();
        setupCancelButton();

        // launch new thread for update in background
        UpdateRunnable runnable = new UpdateRunnable(3);
        new Thread(runnable).start();
    }

    private void setupContinueButton() {
        continueButton = (Button) findViewById(R.id.btnContinue);
        continueButton.setClickable(false);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseInfo.setDataLastUpdate(sharedPreferences);
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    private void setupCancelButton() {
        cancelButton = (Button) findViewById(R.id.btnCancel);
        cancelButton.setEnabled(true); // change to true once cancel is handled correctly
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopThread();
                databaseInfo.updateCanceled(sharedPreferences);
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    private void stopThread() {
        stopThread = true;
    }

    /**
     * Runnable to complete update of data in background. Updates UI on completion.
     *
     * UpdateRunnable adapted from
     * https://www.youtube.com/watch?v=QfQE1ayCzf8&list=PLrnPJCHvNZuD52mtV8NvazNYIyIVPVZRa
     */
    class UpdateRunnable implements Runnable {
        int seconds;

        UpdateRunnable(int seconds) {
            this.seconds = seconds;
        }

        @Override
        public void run() {

            for (int i = 0; i < seconds; i++) {
                if (stopThread) {
                    Thread.currentThread().interrupt();
                }
                Log.d(TAG, "startThread: " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            CSVUpdater csvUpdater = new CSVUpdater(UpdateActivity.this);
            csvUpdater.update(UpdateActivity.this, sharedPreferences);
            runOnUiThread(new Runnable() {
                @Override
                public void run() { updateProgressBar.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                continueButton.setClickable(true);
                continueButton.setVisibility(View.VISIBLE);
                updateTextView.setText(R.string.update_complete);
                }
            });
        }
    }


}
