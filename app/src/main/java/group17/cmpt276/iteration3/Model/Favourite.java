package group17.cmpt276.iteration3.Model;

/**
 * Favourite Class stores data to identify Restaurants that have been marked as Favourite
 */
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

    public Date getDateLastInspection() {
        return dateLastInspection;
    }

}
