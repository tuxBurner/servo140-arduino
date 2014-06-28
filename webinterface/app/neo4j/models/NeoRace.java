package neo4j.models;

import neo4j.RelationNames;
import neo4j.TypeAliasNames;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.List;


/**
 * Created by tuxburner on 5/24/14.
 */
@NodeEntity
@TypeAlias(value = TypeAliasNames.RACE)
public class NeoRace extends AbstractNeoNode {

    ERaceType raceType;

    Boolean raceFinished;

    Integer laps;


    @Fetch
    @RelatedTo(type = RelationNames.RACE_TO_TRACK)
    NeoTrack track;

    @Fetch
    @RelatedTo(type = RelationNames.RACE_DRIVER_CAR)
    List<NeoRaceDriverCar> raceDriverCars;
}
