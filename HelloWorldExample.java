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


public class HelloWorldExample implements AutoCloseable
{
    private final Driver driver;
    
	private int i=0;

	long time =0;

    public HelloWorldExample( String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }
    // Create a Action node
    private StatementResult deleteAll( final Transaction tx)
    {
        return tx.run( " MATCH (n) DETACH DELETE n");
    }
    
 // Create a Action node
    private StatementResult addAction( final Transaction tx, final String name , final int i)
    {
        return tx.run( "CREATE (:Action {name: $name, attribute: $attribute})", parameters( "name", name, "attribute", i) );
    }

 // Create a person node
    private StatementResult addPerson( final Transaction tx, final String name, final int i )
    {
        return tx.run( "CREATE (:Person {name: $name, attribute: $attribute})", parameters( "name", name, "attribute", i ) );
    }

    // Create an Relationship1ment relationship to a pre-existing Action node.
    // This relies on the person first having been created.
    private StatementResult Relationship1( final Transaction tx, final String person, final String Action)
    {
        return tx.run( "MATCH (person:Person {name: $person_name}) " +
                        "MATCH (action:Action {name: $Action_name}) " +
                        "CREATE (person)-[:Relationship1]->(action)",
                parameters( "person_name", person, "Action_name", Action) );
    }
    
    // Create an Relationship1ment relationship to a pre-existing Action node.
    // This relies on the person first having been created.
    private StatementResult Relationship2( final Transaction tx, final String person, final String Action)
    {
        return tx.run( "MATCH (person:Person {name: $person_name}) " +
                        "MATCH (action:Action {name: $Action_name}) " +
                        "CREATE (person)-[:Relationship2]->(action)",
                parameters( "person_name", person, "Action_name", Action) );
    }
    
    // Create an Relationship1ment relationship to a pre-existing Action node.
    // This relies on the person first having been created.
    private StatementResult Relationship3( final Transaction tx, final String person1, final String person2 )
    {
        return tx.run( "MATCH (person1:Person {name: $person1_name}) " +
                        "MATCH (person2:Person {name: $person2_name}) " +
                        "CREATE (person1)-[:Relationship3]->(person2)",
                parameters( "person1_name", person1, "person2_name", person2) );

    }
    
    // Create an Relationship1ment relationship to a pre-existing Action node.
    // This relies on the person first having been created.
    private StatementResult Relationship4( final Transaction tx, final String person1, final String person2 )
    {
        return tx.run( "MATCH (person1:Person {name: $person1_name}) " +
                        "MATCH (person2:Person {name: $person2_name}) " +
                        "CREATE (person1)-[:Relationship4]->(person2)",
                parameters( "person1_name", person1, "person2_name", person2) );

    }

 // Match and display all friendships.
    private StatementResult printFriends( final Transaction tx )
    {	
    	long start = System.currentTimeMillis();
        StatementResult result = tx.run( "MATCH (Y)-[:Relationship3]->(X) RETURN Y.name,X.name");
        while ( result.hasNext() )
        {
            Record record = result.next();
            System.out.println( String.format( "%s knows %s", record.get( "Y.name" ).asString(), record.get( "X.name" ).toString() ) );
        }
        long end = System.currentTimeMillis();
        time += end - start;
        return result;
    }

    public void addRelationshipAndMakeFriends()
    {
        // To collect the session bookmarks
        List<String> savedBookmarks = new ArrayList<>();

        // Create the first person and Relationship1ment relationship.
        try ( Session session1 = driver.session( AccessMode.WRITE ) )
        {
            session1.writeTransaction( tx -> deleteAll( tx) );
            session1.writeTransaction( tx -> addAction( tx, "Action1_0", 5 ) );
            session1.writeTransaction( tx -> addAction( tx, "Action1_1", 10 ) );
            session1.writeTransaction( tx -> addAction( tx, "Action2_0", 15 ) );
            session1.writeTransaction( tx -> addAction( tx, "Action2_1", 20 ) );
            for(i=0; i<=100; i++) {
                session1.writeTransaction( tx -> addPerson( tx, "Person" + i,i));	
            }
            i=0;
            savedBookmarks.add( session1.lastBookmark() );
        }

        // Create the second person and Relationship1ment relationship.
        try ( Session session2 = driver.session( AccessMode.WRITE ) )
        {
            for(i=0; i<=100; i++) {
                session2.writeTransaction( tx -> Relationship1( tx, "Person" + i, "Action1_" + i%2) );
                session2.writeTransaction( tx -> Relationship2( tx, "Person" + i, "Action2_" + (i+1)%2) );
                for(int j=0;j <10;j++) {
                	session2.writeTransaction( tx -> Relationship3( tx, "Person" + i, "Person" + (int)(Math.random() * 99)));
                	session2.writeTransaction( tx -> Relationship4( tx, "Person" + i, "Person" + (int)(Math.random() * 99)));
                }
            }
            i=0;
            savedBookmarks.add( session2.lastBookmark() );
        }

        // Create a friendship between the two people created above.
        try ( Session session3 = driver.session( AccessMode.WRITE, savedBookmarks ) )
        {
        	for(int j=0; j<10;j++) {
        		session3.readTransaction( this::printFriends );
        	}
            System.out.println(time/10 + " passed");
        }
    }

    public static void main( String... args ) throws Exception
    {
        try ( HelloWorldExample greeter = new HelloWorldExample( "bolt://localhost:7687", "neo4j", "strgalt4" ) )
        {
            greeter.addRelationshipAndMakeFriends();
        }
    }
}