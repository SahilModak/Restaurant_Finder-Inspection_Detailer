package group17.cmpt276.iteration3.UI;

import androidx.appcompat.app.AppCompatActivity;
import group17.cmpt276.iteration3.R;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class OptionsScreen extends AppCompatActivity {

    Boolean searchFav = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_screen);
    }

}