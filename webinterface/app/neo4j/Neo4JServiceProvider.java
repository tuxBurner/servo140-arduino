package neo4j;


import neo4j.repositories.DriverRepo;
import neo4j.repositories.TrackRepo;
import neo4jplugin.Neo4JPlugin;
import neo4jplugin.ServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Neo4JServiceProvider extends ServiceProvider {


    @Autowired
    public TrackRepo trackRepo;

    @Autowired
    public DriverRepo driverRepo;

    public static Neo4JServiceProvider get() {
        return Neo4JPlugin.get();
    }



}
