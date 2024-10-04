package util;

import java.time.LocalDateTime;
import org.json.simple.JSONObject;

public class WeatherData {
    
    public JSONObject json; // the actual data
    public LocalDateTime date; // when this data was updated

    public WeatherData(JSONObject json, LocalDateTime date) {
        this.json = json;
        this.date = date;
    }

}
