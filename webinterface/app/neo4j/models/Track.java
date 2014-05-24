package neo4j.models;

import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by tuxburner on 5/24/14.
 */
@NodeEntity
public class Track extends AbstractNeoNode {

    /**
     * The name of the track
     */
    @Indexed(unique = true)
    public String name;
}
