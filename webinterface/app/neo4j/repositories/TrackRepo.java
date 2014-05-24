package neo4j.repositories;

import neo4j.models.Track;
import org.springframework.data.neo4j.repository.GraphRepository;

/**
 * Created by tuxburner on 5/24/14.
 */
public interface TrackRepo extends GraphRepository<Track> {

}
