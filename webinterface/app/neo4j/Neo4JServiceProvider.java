package neo4j;


import neo4jplugin.Neo4JPlugin;
import neo4jplugin.ServiceProvider;
import org.springframework.stereotype.Component;

@Component
public class Neo4JServiceProvider extends ServiceProvider {


    public static Neo4JServiceProvider get() {
        return Neo4JPlugin.get();
    }



}
