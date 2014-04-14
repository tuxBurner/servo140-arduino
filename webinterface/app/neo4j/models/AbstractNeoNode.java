package neo4j.models;


import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.neo4j.annotation.GraphId;

/**
 * This is the AbstractNode from which all other node entities should extend from
 */
public class AbstractNeoNode {

    @GraphId
    public Long id;

    @CreatedDate
    public long created;

    @LastModifiedDate
    public long lastModified;

}
