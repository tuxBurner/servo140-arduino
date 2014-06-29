package neo4j.repositories;

import neo4j.models.NeoRace;
import org.springframework.data.neo4j.repository.GraphRepository;

/**
 * Created by tuxburner on 6/29/14.
 */
public interface NeoRaceRepo extends GraphRepository<NeoRace> {
}
