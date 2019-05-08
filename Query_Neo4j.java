import org.neo4j.driver.v1.AccessMode;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import static org.neo4j.driver.v1.Values.parameters;

import java.util.ArrayList;
import java.util.List;


public class Query_Neo4j implements AutoCloseable
{
    private final Driver driver;
	private int i=0;
	static double times[]=new double[101];
	double time =0;	

    public Query_Neo4j( String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }
    StatementResult sr;
 // Match and display all friendships.
    private StatementResult printFriends( final Transaction tx )
    {	
    	for (int i=0;i<=Init_Sparksee.iterations;i++) {
    		double start = System.currentTimeMillis();
    		StatementResult result = tx.run( "MATCH (X:Person{name: 'Person0'})-[:Relationship3]->(n1) WITH COLLECT(n1) as n MATCH (n1)<-[:Relationship3]-(Y:Person) WHERE n1 IN n  RETURN COUNT(DISTINCT(n1))");
        	while ( result.hasNext())
        	{
            	Record record = result.next();
            	//System.out.println( String.format( "%s knows %s", record.get( "Y.name" ).asString(), record.get( "X.name" ).toString() ) );
            	//System.out.println(record.get( "Y.name" ));

        	}
        	double end = System.currentTimeMillis();
        	times[i] = end - start;
        	sr=result;
            System.out.println(times[i]);
    	}
    	return sr;
    }


    public void addRelationshipAndMakeFriends()
    {
        // To collect the session bookmarks
        List<String> savedBookmarks = new ArrayList<>();

        try ( Session session1 = driver.session( AccessMode.WRITE, savedBookmarks ) )
        {
        	session1.readTransaction( this::printFriends );
        }
    }

    public static void main( String... args ) throws Exception
    {
        try ( Query_Neo4j greeter = new Query_Neo4j( "bolt://localhost:7687", "neo4j", "strgalt4" ) )
        {
            greeter.addRelationshipAndMakeFriends();
        }
    }
}