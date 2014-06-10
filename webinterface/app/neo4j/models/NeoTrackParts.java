package neo4j.models;

import neo4j.TypeAliasNames;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by tuxburner on 6/8/14.
 */
@NodeEntity
@TypeAlias(value = TypeAliasNames.TRACK_PARTS)
public class NeoTrackParts extends AbstractNeoNode {

    public Integer straight = 0;

    public Integer dblStraight = 0;

    public Integer fourthStraight = 0;

    public Integer thirdStraight = 0;

    public Integer curve90 = 0;

    public Integer curve45 = 0;

    public Integer curve452 = 0;

    public Integer connectStraight = 0;

}
