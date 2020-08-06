package group17.cmpt276.iteration3.Model;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.util.Hashtable;
import group17.cmpt276.iteration3.R;

/**
 * Violation Dictionary Class (Singleton)
 * Contains resources to organize the different types of violations
 */
public class ViolationDictionary {
    private static final String TAG = "Violation Dictionary";
    private static Hashtable<Integer, String> violationDescriptionDictionary;
    private static ViolationDictionary violationDictionary;
    int[] employee = {402,403,404};
    int[] pest = {304,305,313};
    int[] licensing = {101,102,103,104,212,311,314,501,502};
    int[] food = {201,202,203,204,205,206,207,208,209,210,211,306,310};
    int[] equipment = {301,302,303,307,308,309,312,315,401};

    public static ViolationDictionary getInstance(Context context){
        if (violationDictionary == null){
            violationDictionary = new ViolationDictionary();
            addViolationDesciptions(context);
        }
        return violationDictionary;
    }

    public String getTypeViolationFromNum(int violationNum) {
        if(inViolationArray(employee,violationNum)){
            return "Employee";
        }
        else if(inViolationArray(equipment,violationNum)){
            return "Equipment";
        }
        else if(inViolationArray(food,violationNum)){
            return "Food";
        }
        else if(inViolationArray(licensing,violationNum)){
            return "Licensing";
        }
        else if(inViolationArray(pest,violationNum)){
            return "Pest";
        }
        else{
            return "Unknown";
        }
    }

    boolean inViolationArray(int[] arr, int i){
        boolean isIn = false;
        for (int value : arr) {
            if (value == i) {
                isIn = true;
                break;
            }
        }
        return isIn;
    }

    //todo: extract to strings.xml
    private static void addViolationDesciptions(Context context) {
        Resources res = context.getResources();
        violationDescriptionDictionary = new Hashtable<>();
        Log.i(TAG, "addViolationDesciptions: attempt to access sys Resources" + res.getString(R.string.violation_description101));
        violationDescriptionDictionary.put(101, res.getString(R.string.violation_description101));
        violationDescriptionDictionary.put(102, res.getString(R.string.violation_description102));
        violationDescriptionDictionary.put(103, res.getString(R.string.violation_description103));
        violationDescriptionDictionary.put(104, res.getString(R.string.violation_description104));
        violationDescriptionDictionary.put(201, res.getString(R.string.violation_description201));
        violationDescriptionDictionary.put(202, res.getString(R.string.violation_description202));
        violationDescriptionDictionary.put(203, res.getString(R.string.violation_description203));
        violationDescriptionDictionary.put(204, res.getString(R.string.violation_description204));
        violationDescriptionDictionary.put(205, res.getString(R.string.violation_description205));
        violationDescriptionDictionary.put(206, res.getString(R.string.violation_description206));
        violationDescriptionDictionary.put(208, res.getString(R.string.violation_description208));
        violationDescriptionDictionary.put(209, res.getString(R.string.violation_description209));
        violationDescriptionDictionary.put(210, res.getString(R.string.violation_description210));
        violationDescriptionDictionary.put(211, res.getString(R.string.violation_description211));
        violationDescriptionDictionary.put(212, res.getString(R.string.violation_description212));
        violationDescriptionDictionary.put(301, res.getString(R.string.violation_description301));
        violationDescriptionDictionary.put(302, res.getString(R.string.violation_description302));
        violationDescriptionDictionary.put(303, res.getString(R.string.violation_description303));
        violationDescriptionDictionary.put(304, res.getString(R.string.violation_description304));
        violationDescriptionDictionary.put(305, res.getString(R.string.violation_description305));
        violationDescriptionDictionary.put(306, res.getString(R.string.violation_description306));
        violationDescriptionDictionary.put(307, res.getString(R.string.violation_description307));
        violationDescriptionDictionary.put(308, res.getString(R.string.violation_description308));
        violationDescriptionDictionary.put(309, res.getString(R.string.violation_description309));
        violationDescriptionDictionary.put(310, res.getString(R.string.violation_description310));
        violationDescriptionDictionary.put(311, res.getString(R.string.violation_description311));
        violationDescriptionDictionary.put(312, res.getString(R.string.violation_description312));
        violationDescriptionDictionary.put(313, res.getString(R.string.violation_description313));
        violationDescriptionDictionary.put(314, res.getString(R.string.violation_description314));
        violationDescriptionDictionary.put(315, res.getString(R.string.violation_description315));
        violationDescriptionDictionary.put(401, res.getString(R.string.violation_description401));
        violationDescriptionDictionary.put(402, res.getString(R.string.violation_description402));
        violationDescriptionDictionary.put(403, res.getString(R.string.violation_description403));
        violationDescriptionDictionary.put(404, res.getString(R.string.violation_description404));
        violationDescriptionDictionary.put(501, res.getString(R.string.violation_description501));
        violationDescriptionDictionary.put(502, res.getString(R.string.violation_description502));
    }

    public String getDescription(int violationNum){
        if(!violationDescriptionDictionary.containsKey(violationNum)){
            return null;
        }
        else{
            return violationDescriptionDictionary.get(violationNum);
        }
    }
}
