package group17.cmpt276.iteration3.UI;

import androidx.appcompat.app.AppCompatActivity;
import group17.cmpt276.iteration3.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class OptionsScreen extends AppCompatActivity {

    Boolean searchFav = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_screen);
        Button btn1 = findViewById(R.id.saveBtn);
        btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(OptionsScreen.this, MainActivity.class));
            }
        });

        Button btn2 = findViewById(R.id.saveBtn);
        btn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(OptionsScreen.this, MainActivity.class));
            }
        });
    }

}