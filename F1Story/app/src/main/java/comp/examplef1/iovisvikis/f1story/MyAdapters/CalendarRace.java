package comp.examplef1.iovisvikis.f1story.MyAdapters;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by iovisvikis on 14/4/2017.
 */

public class CalendarRace{


    private final DateTime qualEvent;

    public DateTime getQualEvent() {
        return qualEvent;
    }

    public DateTime getRaceEvent() {
        return raceEvent;
    }

    private final DateTime raceEvent;

    private final String circuitUrl, circuitName, circuitId, countryName, locality, localDate, localTime;

    public String getCircuitUrl() {
        return circuitUrl;
    }

    public String getCircuitName() {
        return circuitName;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getLocality() {
        return locality;
    }

    public String getLocalDate() {
        return localDate;
    }

    public String getLocalTime() {
        return localTime;
    }

    public String getCircuitId() {
        return circuitId;
    }


    public CalendarRace(String[] eventInfo){

        DateTimeFormatter parser = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.UTC);

        //index 0 carries the qualifying date in GMT
        this.qualEvent = parser.parseDateTime(eventInfo[0]);
        //index 1 carries the race date in GMT
        this.raceEvent = parser.parseDateTime(eventInfo[1]);

        //get event local date and time
        DateTime localRaceDateTime = raceEvent.toDateTime(DateTimeZone.getDefault());

        this.localDate = localRaceDateTime.getYear() + "-" + localRaceDateTime.getMonthOfYear() + "-" + localRaceDateTime.getDayOfMonth();

        String localHour = Integer.toString(localRaceDateTime.getHourOfDay());
        String localMinutes = Integer.toString(localRaceDateTime.getMinuteOfHour());
        String localSeconds = Integer.toString(localRaceDateTime.getSecondOfMinute());

        this.localTime = localHour + ":" + localMinutes + ":" + localSeconds;

        this.circuitUrl = eventInfo[2];
        this.circuitName = eventInfo[3];
        this.circuitId = eventInfo[4];
        this.countryName = eventInfo[5];
        this.locality = eventInfo[6];


    }


}//CalendarRace



