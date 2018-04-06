
package comp.examplef1.iovisvikis.f1story.MyAdapters;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by iovisvikis on 30/6/2017.
 */

public class ConstructorChampion{

    private ArrayList<SeasonEndAllConstructors> championshipYears;



    public ArrayList<SeasonEndAllConstructors> getChampionshipYears() {
        return championshipYears;
    }



    public ConstructorChampion(JSONObject standingsListObject)
    {
        this.championshipYears = new ArrayList<>();

        SeasonEndAllConstructors seasonChampionship = new SeasonEndAllConstructors(standingsListObject);
        this.championshipYears.add(seasonChampionship);

    }


}
