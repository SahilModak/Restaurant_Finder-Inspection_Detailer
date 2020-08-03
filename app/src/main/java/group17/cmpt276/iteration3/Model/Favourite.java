package group17.cmpt276.iteration3.Model;

public class Favourite {
    private String ID;
    private Date dateLastInspection;

    public Favourite(String ID, Date dateLastInspection) {
        this.ID = ID;
        this.dateLastInspection = dateLastInspection;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Date getDateLastInspection() {
        return dateLastInspection;
    }

    public void setDateLastInspection(Date dateLastInspection) {
        this.dateLastInspection = dateLastInspection;
    }
}
