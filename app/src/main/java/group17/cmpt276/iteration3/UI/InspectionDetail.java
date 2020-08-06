package group17.cmpt276.iteration3.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.List;

import group17.cmpt276.iteration3.Model.Inspection;
import group17.cmpt276.iteration3.Model.Restaurant;
import group17.cmpt276.iteration3.Model.RestaurantManager;
import group17.cmpt276.iteration3.Model.Violation;
import group17.cmpt276.iteration3.R;

/**
 * Displays details of selected inspection. Shows date of inspection, number of critical
 * and non-critical violations, type of inspection, along with a scrollable list of violations
 * with brief descriptions. User may select a violation to see the full description.
 */
public class InspectionDetail extends AppCompatActivity {

    private static final String EXTRA_RESTAURANT_INDEX = "jmt24.cmpt276.group17_iteration1.restaurant_index";
    private static final String EXTRA_INSPECTION_INDEX = "jmt24.cmpt276.group17_iteration1.inspection_index";
    private static final String TAG = "Inspection Detail";
    private int currentRestaurantIndex;
    private int currentInspectionIndex;
    private Restaurant restaurant;
    private List<Violation> violationList;

    public static Intent makeIntent(Context context, int restaurantIndex, int inspectionIndex) {
        Intent intent =  new Intent(context, InspectionDetail.class);
        intent.putExtra(EXTRA_RESTAURANT_INDEX, restaurantIndex);
        intent.putExtra(EXTRA_INSPECTION_INDEX, inspectionIndex);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_detail);

        setToolbar();
        getInspectionDetailsFromIntent();
        getViolationList();
        updateView();
        populateListView();
        registerClickCallback();
        setupHazardLevel();
    }

    private void setToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar_detail_inspection);
        setSupportActionBar(toolbar);
    }

    private void getInspectionDetailsFromIntent(){
        currentRestaurantIndex = getIntent().getIntExtra(EXTRA_RESTAURANT_INDEX, 0);
        currentInspectionIndex = getIntent().getIntExtra(EXTRA_INSPECTION_INDEX, 0);
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inspection_detail, menu);
        return true;
    }

    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.item_inspectionDetail_back){
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateView() {
        TextView txtDate = findViewById(R.id.txtDate);
        txtDate.setText(getString(R.string.date,
                   restaurant.getInspection(currentInspectionIndex).getDate().getFullDate()));

        TextView txtCriticalViolation = findViewById(R.id.txtCriticalViolation);
        txtCriticalViolation.setText(getString(R.string.num_crit,
                restaurant.getInspection(currentInspectionIndex).getNumCriticalViolations()));

        TextView txtNonCriticalViolation = findViewById(R.id.txtNonCriticalViolation);
           txtNonCriticalViolation.setText(getString(R.string.num_non_crit,
                restaurant.getInspection(currentInspectionIndex).getNumNonCriticalViolations()));

        TextView txtInspectionType = findViewById(R.id.txtInspectionType);
        txtInspectionType.setText(getString(R.string.type_inspection,
                restaurant.getInspection(currentInspectionIndex).getInspectionType()));
    }

    private void getViolationList(){
        RestaurantManager restaurantManager = RestaurantManager.getInstance();
        restaurant = restaurantManager.getRestaurant(currentRestaurantIndex);
        violationList = restaurant.getInspection(currentInspectionIndex).getAllViolations();
    }

    private void populateListView() {
        ArrayAdapter<Violation> adapter = new ViolationListAdapter();
        ListView list = (ListView) findViewById(R.id.violationListView);
        list.setAdapter(adapter);
    }

    /*
    Custom adapter to provide a view for the Violation ListView
    Returns a view for each Violation in a collection of Violation objects
     */
    private class ViolationListAdapter extends ArrayAdapter<Violation> {
        public ViolationListAdapter() {
            super(InspectionDetail.this, R.layout.violation_view, violationList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have view in case null
            View violationView = convertView;
            if (violationView == null) {
                violationView = getLayoutInflater().inflate(R.layout.violation_view, parent, false);
            }

            Violation currentViolation = violationList.get(position);
            setupViolationView(violationView, currentViolation);
            return violationView;
        }
    }
    // set up each violation in the list
    private void setupViolationView(View violationView, Violation currentViolation){
        // fill view
        ImageView imageViolation = (ImageView) violationView.findViewById(R.id.iconNatureofViolation);
        // will have to change according to violation
        setupNatureOfViolation(imageViolation,currentViolation);

        ImageView imageSeverity = (ImageView) violationView.findViewById(R.id.iconSeverity);
        // will have to change according to severity
        setupSeverity(imageSeverity,currentViolation);

        TextView txtDescription = (TextView) violationView.findViewById(R.id.txtBriefDescription);
        setupShortDescription(txtDescription,currentViolation);
    }

    //set up the nature of violation base on different nature that reads from CSV file
    private void setupNatureOfViolation(ImageView imageViolation, Violation current){
        switch (current.getViolationNature()){
            case "Licensing":
                imageViolation.setImageResource(R.drawable.ic_licensing);break;
            case "Pest":
                imageViolation.setImageResource(R.drawable.ic_pest);break;
            case "Food":
                imageViolation.setImageResource(R.drawable.ic_food);break;
            case "Employees":
                imageViolation.setImageResource(R.drawable.ic_employee);break;
            case "Equipment":
                imageViolation.setImageResource(R.drawable.ic_equipment);break;
        }
    }
    // set up the icon base on whether the violation is critical or not
    private void setupSeverity(ImageView imageSeverity, Violation current){
        if(current.isCritical()){
            imageSeverity.setImageResource(R.drawable.ic_ciritcal);
        }
        else{
            imageSeverity.setImageResource(R.drawable.ic_noncritical);
        }
    }

    // set up a short description for each violation
    private void setupShortDescription(TextView txtDescription,Violation current){
        String violationDetails = current.getDescription();
        if(violationDetails == null){
            violationDetails = getString(R.string.unknown_violation);
        }
        txtDescription.setText(violationDetails);
    }

    // toast the long description of the violation, when user click on that violation
    private void registerClickCallback() {
        final ListView listView = (ListView) findViewById(R.id.violationListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(InspectionDetail.this, violationList.get(position).getViolationString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //setup the hazard level icon, and shows the word description of the hazard level
    private void setupHazardLevel() {
        Inspection currentInspection =restaurant.getInspection(currentInspectionIndex);
        TextView textView = findViewById(R.id.txtHazard);
        ImageView imageHazard = findViewById(R.id.hazardIcon2);
        switch (currentInspection.getHazardLevel()){
            case "Low":
                imageHazard.setImageResource(R.drawable.ic_warning_low);
                textView.setText(R.string.Hazard_Level_Low);
                break;
            case "Moderate":
                imageHazard.setImageResource(R.drawable.ic_warning_moderate);
                textView.setText(R.string.Hazard_Level_Moderate);
                break;
            case "High":
                imageHazard.setImageResource(R.drawable.ic_warning_critical);
                textView.setText(R.string.Hazard_Level_High);
        }
    }
}