package neo4j.models;

import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelatedTo;

/**
 * Created by tuxburner on 5/24/14.
 */
public class RaceDriverCar extends AbstractNeoNode {

    @Fetch
    @RelatedTo(type = RelationNames.RACE_DRIVER)
    public Driver driver;

    @Fetch
    @RelatedTo(type = RelationNames.RACE_CAR)
    public Car car;
}
