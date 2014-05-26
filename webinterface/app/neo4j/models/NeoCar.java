package neo4j.models;

import neo4j.TypeAliasNames;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by tuxburner on 5/24/14.
 */
@NodeEntity
@TypeAlias(value = TypeAliasNames.CAR)
public class NeoCar extends ImageNeoNode {

    @Indexed(unique = true)
    public String name;

    public CarType carType;
}
