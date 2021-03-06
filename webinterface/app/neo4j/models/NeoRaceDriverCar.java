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
@TypeAlias(value = TypeAliasNames.RACE_DRIVER_CAR)
public class NeoRaceDriverCar extends AbstractNeoNode {

    @Fetch
    @RelatedTo(type = RelationNames.RACE_DRIVER)
    public NeoDriver driver;

    @Fetch
    @RelatedTo(type = RelationNames.RACE_CAR)
    public NeoCar car;

    @Fetch
    @RelatedTo(type = RelationNames.RACE_LAPS)
    public List<NeoRaceLap> laps;
}
