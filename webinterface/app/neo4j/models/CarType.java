package neo4j.models;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by tuxburner on 5/24/14.
 */
public enum CarType {
    V1,
    V2,
    V3;

    private static List<String> typesForTemplate;

    public static List<String> getCarTypes() {
        if (typesForTemplate == null) {
            typesForTemplate = new ArrayList<>();
            for (CarType carType : CarType.values()) {
                typesForTemplate.add(carType.name());
            }
        }
        return typesForTemplate;
    }
}
