package group17.cmpt276.iteration3.UI;

import androidx.appcompat.app.AppCompatActivity;

import group17.cmpt276.iteration3.Model.NewDataNotify;
import group17.cmpt276.iteration3.Model.RestaurantManager;
import group17.cmpt276.iteration3.R;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Search screen class is an activity to used to take input for the search functions
 * within the the restaurant manager singleton.
 */
public class SearchScreen extends AppCompatActivity {

    Boolean searchFav = false;
    String searchResName;
    //default min, max for violations
    int searchMaxVio = -1;
    int searchMinVio = -1;
    String searchHazard;
    RestaurantManager restaurantManager;
    boolean flagValidInput = true;
    NewDataNotify newDataNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_screen);

        restaurantManager = RestaurantManager.getInstance();
        newDataNotify = NewDataNotify.getInstance();

        //setting OnClickListeners for all buttons on screen
        setCancelButton();
        setSaveSearchButton();
        setClearSearchButton();
    }

    private void setClearSearchButton() {
        Button clear = findViewById(R.id.clearBtn);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if search active
                if(restaurantManager.isCalledSearch()){
                    restaurantManager.clearSearch();
                    newDataNotify.setNewData(true);
                    restaurantManager.clearSearch();
                }
                finish();
            }
        });
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
            //making sure value is entered
            searchMaxVio = Integer.parseInt(maxV);
        }

        if(!minV.equals("")){
            //making sure value is entered
            searchMinVio = Integer.parseInt(minV);
        }

        if(searchMinVio != -1 && searchMaxVio != -1){
            if(searchMinVio > searchMaxVio) {
                //setting error messages for invalid input
                maxViolations.setError(getString(R.string.inputError));
                minViolations.setError(getString(R.string.inputError));
                flagValidInput = false;
            }
        }
    }

    //check if favorites need to be used as search criteria
    private void getUserSearchFavorite() {
        CheckBox fav = (CheckBox) findViewById(R.id.favCheckBox);
        searchFav = fav.isChecked();
    }

    //get hazard level for user entry if entered
    private void getUserSearchHazardLevel() {
        RadioGroup hazardGroup = findViewById(R.id.hazardGroup);
        int radioId = hazardGroup.getCheckedRadioButtonId();
        if(radioId != -1){
            //check to make sure something from the RadioGroup is selected
            RadioButton hazard = findViewById(radioId);
            this.searchHazard = (String) hazard.getText();
        }
        else{
            searchHazard = "";
        }
    }

    // get the search string if entered
    private void getUserSearchName(){
        EditText editTextName = findViewById(R.id.editText_search_name);
        searchResName = editTextName.getText().toString();
    }

    //returns to previous screen with search results intact
    private void setCancelButton() {
        Button btn1 = findViewById(R.id.cancelBtn);
        btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                newDataNotify.setNewData(false);
                finish();
            }
        });
    }
}
