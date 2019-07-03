package Neo4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;

import Constants.CONSTANTS;

public class Query8_9
{
   // private static final File databaseDirectory = new File( "target/neo4j-query-1-6" );

    private static final File databaseDirectory = new File( "target/smallTest" );

	int timeIndex=0;
    // tag::vars[]
	private static GraphDatabaseService graphDb =new GraphDatabaseFactory().newEmbeddedDatabase( databaseDirectory );
	Node Persons[] = new Node[CONSTANTS.nodes+1];
    Node Actions1[] = new Node[2];
    Node Actions2[] = new Node[2];
    // end::vars[]

    // tag::createReltype[]
    private enum RelTypes implements RelationshipType
    {
    	RELATIONSHIP1,
    	RELATIONSHIP2,
    	RELATIONSHIP3,
    	RELATIONSHIP4
    }
    // end::createReltype[]

    public static void main( final String[] args ) throws IOException
    {
    	registerShutdownHook( graphDb );
    	Query8_9 hello = new Query8_9();
        hello.Query1();
        //hello.Query();
        System.out.print("test");
        hello.write_data();
        hello.shutDown();
    }

    void Query() { 
        try ( Transaction tx = graphDb.beginTx() )
        {     	
        	Label labelPerson = Label.label( "Person" );

        	Node N = graphDb.findNode(labelPerson, "attribute", 250);
        			Traverser friendsTraverser = getFriends( N, Direction.BOTH );
        				for ( Path friendPath : friendsTraverser ) {
        					if((Integer)friendPath.endNode().getProperty("attribute") >= 15) {	
        						//System.out.println("Query 19:" +"The  user " + 250 + " is connected with " + friendPath.endNode().getProperty( "name" ));	
        					}
        				}
        }
    }
    
    void Query1() { 
    	Label labelPerson = Label.label( "Person" );
    	Label labelAction = Label.label( "Action" );
 		double end = System.currentTimeMillis();
 		double start = System.currentTimeMillis();
    	for (int i=0;i<CONSTANTS.iterations;i++) {
    		timeIndex=0;

     		//Query8
    		start = System.currentTimeMillis();
    	 	try ( 	Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("MATCH (X:Person {name:'Person1'})-[:RELATIONSHIP3]->(n1)<-[:RELATIONSHIP3]-(Y:Person{name:'Person2'}) RETURN COUNT(DISTINCT(n1))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	System.out.printf( "Query 8:" +"%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ;
     		timeIndex++;
     		
     		//Query9
    		start = System.currentTimeMillis();
    	 	try ( 	Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("MATCH (X:Person{name:'Person1'})-[:RELATIONSHIP3]->(n1) With COLLECT(n1) as n "
    	 					+ "MATCH (Y:Person {name:'Person2'})-[:RELATIONSHIP3]->(n1) WHERE n1 in n RETURN COUNT(DISTINCT(n1))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	System.out.printf( "Query 9:" +"%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ;
     		timeIndex++;
     		
	
     	     		//Query17
     	    		start = System.currentTimeMillis();
     	            try ( Transaction tx = graphDb.beginTx() )
     	            {
     	            	Set<Node> friends1 = new HashSet<Node>();
     	            	Set<Node> friends2 = new HashSet<Node>();
     	            	Node Person1= graphDb.findNode(labelPerson,"name","Person1");
     	            	Node Person2= graphDb.findNode(labelPerson,"name","Person2");
     	            	Traverser person1Friends = getFriends( Person1, Direction.OUTGOING);
     	            	Traverser person2Friends = getFriends( Person2, Direction.OUTGOING);
     	            		{
     	            			for ( Path friendPath : person1Friends ) {
     	            				friends1.add(friendPath.endNode());
     	            			}   
     	            			for ( Path friendPath : person2Friends ) {
     	            				friends2.add(friendPath.endNode());
     	            			} 
     	            			friends1.retainAll(friends2);
     	            		}     	     		
     	            	System.out.println("Query 17:" + "Found new user1 " + friends1.size() );	

     	            }
     	     		end = System.currentTimeMillis();
     	     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     	     		timeIndex++;
     	    	}
     	    
    	   	
    }

    private Traverser getFriends(final Node person, Direction dir )
    {
        TraversalDescription td = graphDb.traversalDescription()
                .breadthFirst()
                .relationships( RelTypes.RELATIONSHIP3, dir )
                .evaluator( Evaluators.atDepth(1) );
        return td.traverse( person );
    }
    
    void write_data() {
    		try {
    			FileWriter csvWriter = new FileWriter("Query8_9.csv");
    			for (int i=0;i<CONSTANTS.iterations;i++) {
    				for(int j=0; j<timeIndex;j++) { 
    						csvWriter.write( CONSTANTS.queries[j][i] + "");
    						csvWriter.append("\t");
    				}
    				csvWriter.append("\n");
    		} 
    			csvWriter.close();
 			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }


    void shutDown()
    {
        System.out.println();
        System.out.println( "Shutting down database ..." );
        // tag::shutdownServer[]
        graphDb.shutdown();
        // end::shutdownServer[]
    }

    // tag::shutdownHook[]
    private static void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }
    // end::shutdownHook[]
}
