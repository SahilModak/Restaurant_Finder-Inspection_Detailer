package group17.cmpt276.iteration3.UI;

import androidx.appcompat.app.AppCompatActivity;

import group17.cmpt276.iteration3.Model.SearchCriteria;
import group17.cmpt276.iteration3.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class OptionsScreen extends AppCompatActivity {

    Boolean searchFav = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_screen);


        Button btn1 = findViewById(R.id.cancelBtn);
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
                SearchCriteria thisSearch = SearchCriteria.getInstance();
                RadioButton fav = (RadioButton) findViewById(R.id.favouriteCheck);
                thisSearch.setSearchFavourites(fav.isChecked());
                RadioGroup hazardGroup = (RadioGroup) findViewById(R.id.hazardGroup);
                int radioId = hazardGroup.getCheckedRadioButtonId();
                if(radioId != -1){
                    RadioButton hazard = findViewById(radioId);
                    thisSearch.setSearchHazardLevel((String) hazard.getText());
                }
                EditText minViolations = (EditText) findViewById(R.id.minViolation);
                EditText maxViolations = (EditText) findViewById(R.id.maxViolation);
                int maxV;
                int minV;
                boolean flag = true;
                if(!(minViolations.getText().toString().equals(""))){
                    minV = Integer.parseInt(minViolations.getText().toString());
                    thisSearch.setMinViolations(minV);
                    if(!(maxViolations.getText().toString().equals(""))){
                        maxV = Integer.parseInt(maxViolations.getText().toString());
                        if(maxV < minV){
                            flag = false;
                            maxViolations.setError("The maximum number of violations cant be less than the minimum number of of violations");
                        }
                        else{
                            thisSearch.setMaxViolations(maxV);
                        }
                    }
                }
                else if (!(maxViolations.getText().toString().equals(""))){
                    maxV = Integer.parseInt(maxViolations.getText().toString());
                    thisSearch.setMaxViolations(maxV);
                }
                if(flag) {
                    finish();
                    //startActivity(new Intent(OptionsScreen.this, MapsActivity.class));
                }
            }
        });
    }
}