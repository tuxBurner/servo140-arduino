package neo4j.models;

import neo4j.TypeAliasNames;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by tuxburner on 6/28/14.
 */
@NodeEntity
@TypeAlias(value = TypeAliasNames.RACE_LAP)
public class NeoRaceLap extends AbstractNeoNode {

    Integer lapNr;

    Double lapTime;
}
