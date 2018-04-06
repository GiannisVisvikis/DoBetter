package comp.examplef1.iovisvikis.f1story.MyAdapters;

/**
 * Created by iovisvikis on 17/5/2017.
 */

public class CurrentGridRows{


    private final String driverName, driverId, driverNationality, driverURL, constructorName, constructorId, constructorNationality, constructorURL;


    public String getDriverName() {
        return driverName;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getDriverNationality() {
        return driverNationality;
    }

    public String getDriverURL() {
        return driverURL;
    }

    public String getConstructorName() {
        return constructorName;
    }

    public String getConstructorId() {
        return constructorId;
    }

    public String getConstructorNationality() {
        return constructorNationality;
    }

    public String getConstructorURL() {
        return constructorURL;
    }

    public CurrentGridRows(String[] gridRowInfo) {

        this.driverName = gridRowInfo[0] + " " + gridRowInfo[1];
        this.driverId = gridRowInfo[2];
        this.driverNationality = gridRowInfo[3];
        this.driverURL = gridRowInfo[4];

        this.constructorName = gridRowInfo[5];
        this.constructorId = gridRowInfo[6];
        this.constructorNationality = gridRowInfo[7];
        this.constructorURL = gridRowInfo[8];


    }


}
