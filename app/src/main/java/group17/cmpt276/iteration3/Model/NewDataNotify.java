package group17.cmpt276.iteration3.Model;

/**
 * UI classes will check with this class if there is new data to display
 * or tell this class that the new data has been read/used
 */


public class NewDataNotify {
    private boolean isNewData = false;
    private static NewDataNotify newDataNotify;

    private NewDataNotify() {
    }

    public static NewDataNotify getInstance(){
        if(newDataNotify == null){
            newDataNotify = new NewDataNotify();
        }
        return newDataNotify;
    }

    public boolean isNewData() {
        return isNewData;
    }

    public void setNewData(boolean newData) {
        isNewData = newData;
    }
}
