package neo4j.models;

import org.springframework.data.neo4j.annotation.Indexed;

/**
 * Created by tuxburner on 5/24/14.
 */
public class Car extends AbstractNeoNode {

    @Indexed(unique = true)
    public String name;

    public CarType type;
}
