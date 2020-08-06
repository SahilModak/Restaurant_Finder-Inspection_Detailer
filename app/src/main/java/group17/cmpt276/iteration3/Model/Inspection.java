package group17.cmpt276.iteration3.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
Inspection Class, contains metadata about a restaurant inspection including date and violations found
Contains a list of violations observed during the given inspection
 */

public class Inspection implements Iterable<Violation>{
    private Date date;
    private String inspectionType;
    private int numCriticalViolations;
    private int numNonCriticalViolations;
    private String hazardLevel;
    private List<Violation> allViolations;

    public Inspection(Date date, String inspectionType, int numCriticalViolations, int numNonCriticalViolations, String hazardLevel) {
        this.date = date;
        this.inspectionType = inspectionType;
        this.numCriticalViolations = numCriticalViolations;
        this.numNonCriticalViolations = numNonCriticalViolations;
        this.hazardLevel = hazardLevel;
        allViolations = new ArrayList<>();
    }

    public Date getDate() {
        return date;
    }

    public List<Violation> getAllViolations(){
        return allViolations;
    }


    public String getInspectionType() {
        return inspectionType;
    }

    public int getNumCriticalViolations() {
        return numCriticalViolations;
    }

    public int getNumNonCriticalViolations() {
        return numNonCriticalViolations;
    }

    public String getHazardLevel() {
        return hazardLevel;
    }

    @Override
    public Iterator<Violation> iterator() {
        //override to make the class iterable
        return allViolations.iterator();
    }

    public void addViolation(Violation x){
        //add violation to list of all violations
        allViolations.add(x);
    }

    public int numOfViolations(){
        //function to get total number of violations for the given inspection
        return allViolations.size();
    }

}
