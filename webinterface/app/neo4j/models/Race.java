package neo4j.models;

import neo4j.RelationNames;
import neo4j.TypeAliasNames;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;


/**
 * Created by tuxburner on 5/24/14.
 */
@NodeEntity
@TypeAlias(value = TypeAliasNames.RACE)
public class Race extends AbstractNeoNode {

    @Fetch
    @RelatedTo(type = RelationNames.RACE_TO_TRACK)
    Track track;

    @Fetch
    @RelatedTo(type = RelationNames.RACE_DRIVER_CAR)
    RaceDriverCar raceDriverCar;

}
