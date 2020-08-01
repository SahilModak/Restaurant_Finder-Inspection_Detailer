package group17.cmpt276.iteration3.UI;

import androidx.appcompat.app.AppCompatActivity;

import group17.cmpt276.iteration3.Model.RestaurantManager;
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
    String searchResName;
    int searchMaxVio = -1;
    int searchMinVio = -1;
    String searchHazard;
    RestaurantManager restaurantManager;
    boolean flagValidInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_screen);

        restaurantManager = RestaurantManager.getInstance();
        restaurantManager.clearSearch(); //clear search before starting a new search

        setCancelButton();
        setSaveSearchButton();
    }

    private void setSaveSearchButton() {
        Button btn2 = findViewById(R.id.saveBtn);
        btn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                getUserSearchFavorite();
                getUserSearchHazardLevel();
                getUserSearchName();
                getUserMinMaxViolations();
                if(flagValidInput){
                    restaurantManager.setSearchedRestaurants(searchResName,searchFav,searchMaxVio,searchMinVio,searchHazard);
                    finish();
                }
            }
        });
    }

    private void getUserMinMaxViolations(){
        EditText minViolations = findViewById(R.id.minViolation);
        EditText maxViolations = findViewById(R.id.maxViolation);
        String maxV;
        String minV;

        maxV = maxViolations.getText().toString();
        minV = minViolations.getText().toString();

        if(!maxV.equals("")){
            searchMaxVio = Integer.parseInt(maxV);
        }

        if(!minV.equals("")){
            searchMinVio = Integer.parseInt(minV);
        }

        if(searchMinVio != -1 && searchMaxVio != -1){
            if(searchMinVio > searchMaxVio) {
                maxViolations.setError("The maximum number of violations cant be less than the minimum number of of violations");
                flagValidInput = false;
            }
        }
    }

    private void getUserSearchFavorite() {
        RadioButton fav = (RadioButton) findViewById(R.id.favouriteCheck);
        searchFav = fav.isChecked();
    }

    private void getUserSearchHazardLevel() {
        RadioGroup hazardGroup = findViewById(R.id.hazardGroup);
        int radioId = hazardGroup.getCheckedRadioButtonId();
        if(radioId != -1){
            RadioButton hazard = findViewById(radioId);
            this.searchHazard = (String) hazard.getText();
        }
        else{
            searchHazard = "";
        }
    }
    private void getUserSearchName(){
        EditText editTextName = findViewById(R.id.editText_search_name);
        searchResName = editTextName.getText().toString();
    }


    private void setCancelButton() {
        Button btn1 = findViewById(R.id.cancelBtn);
        btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(OptionsScreen.this, MainActivity.class));
            }
        });
    }
}