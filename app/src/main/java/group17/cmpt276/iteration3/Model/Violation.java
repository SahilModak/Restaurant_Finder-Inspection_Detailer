package group17.cmpt276.iteration3.Model;
import android.content.Context;

/*
Violation Class, contains metadata about a single violation within an inspection
Data members include: the criticality of the violation, a description and the violation number
 */

public class Violation {
    private static final String TAG = "Violation Class";
    private boolean isCritical;
    private String violationString;
    private String description;
    private String typeViolation;
    private int violationNum;

    public Violation(boolean isCritical, String description, int violationNum, Context context) {
        this.isCritical = isCritical;
        this.violationString = description;
        this.violationNum = violationNum;
        ViolationDictionary violationDictionary = ViolationDictionary.getInstance(context);
        this.typeViolation = violationDictionary.getTypeViolationFromNum(this.violationNum);
        this.description = violationDictionary.getDescription(this.violationNum);
    }

    public String getViolationNature(){
        return typeViolation;
    }

    public boolean isCritical() {
        return isCritical;
    }

    public String getDescription() {
        return description;
    }

    public String getViolationString() {
        return violationString;
    }

    @Override
    public String toString() {
        return "Violation{" +
                "isCritical=" + isCritical +
                ", violationString='" + violationString + '\'' +
                ", description='" + description + '\'' +
                ", typeViolation='" + typeViolation + '\'' +
                ", violationNum=" + violationNum +
                '}';
    }
}
