
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.neo4j.driver.v1.StatementResult;
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
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;


public class New_Queries
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
    	New_Queries hello = new New_Queries();
    	System.out.print("test");
    	//hello.createIndex();
        hello.Query1();
        //hello.Query();
        hello.write_data();
        hello.shutDown();
    }

    void Query() { 
        try ( Transaction tx = graphDb.beginTx() )
        {     	
        	Label labelPerson = Label.label( "Person" );

        	Node N = graphDb.findNode(labelPerson, "attribute", 250);
        			Traverser friendsTraverser = getFriends( N, Direction.BOTH,1 );
        				for ( Path friendPath : friendsTraverser ) {
        					if((Integer)friendPath.endNode().getProperty("attribute") >= 15) {	
        						System.out.println("Query 19:" +"The  user " + 250 + " is connected with " + friendPath.endNode().getProperty( "name" ));	
        					}
        				}
        }
    }
    
   /** private StatementResult createIndex( final Transaction tx, final String name)
    {
        return tx.run( "CALL db.index.fulltext.createNodeIndex(\"titlesAndDescriptions\",[\"Person\"],[\"name\"])");
    }**/
    
    void Query1() { 
    	Label labelPerson = Label.label( "Person" );
    	Label labelAction = Label.label( "Action" );
    	for (int i=0;i<CONSTANTS.iterations;i++) {
    		timeIndex=0;
    		
    		//Query1
    		double start = System.currentTimeMillis();
    	 	try (  Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("MATCH (Y:Person)-[:RELATIONSHIP4]->(X) WITH AVG(X.attribute) as A MATCH (Y:Person)-[:RELATIONSHIP4]->(X) WHERE  A<=250 return Count(DISTINCT(Y))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	System.out.printf( "Query 1:" + " %s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
    	 	 // end::execute[]
     		double end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
     		
     		//Query2
    		start = System.currentTimeMillis();
            try ( Transaction tx = graphDb.beginTx() )
            {
            	int count =0;
            	int avg=0;
            	Iterable<Node>	allNodes = graphDb.getAllNodes();
            	for(Node n : allNodes) {	
            			avg = 0;
            			Traverser friendsTraverser = getFriends( n, Direction.OUTGOING,1 );
            			{
            				for ( Path friendPath : friendsTraverser ) {
            					avg += (Integer)friendPath.endNode().getProperty("attribute");
            				}    
            				if( friendsTraverser.metadata().getNumberOfPathsReturned()!=0 &&  avg/friendsTraverser.metadata().getNumberOfPathsReturned() <=250) {
            					count++;
            				}
            			}   	
            	}
            	System.out.println("Query 2:" +"The result of user  is " + count);	
            }
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
     		
    		//Query3
    		start = System.currentTimeMillis();
    	 	try (  Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("match t=(p:Person{name :'Person1'})-[:RELATIONSHIP3*1..3]->(p1:Person)-[:RELATIONSHIP3*1..2]->(p2) where not (p)-[:RELATIONSHIP3]->(p2) return COUNT(p2)"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	System.out.printf( "Query 3:" + " %s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
    	 	 // end::execute[]
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
     		
    		//Query4
    		start = System.currentTimeMillis();
    	 	try (  Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("match t=(p:Person{name :'Person1'})-[:RELATIONSHIP2*1..2]->(p1:Action)<-[:RELATIONSHIP2*1..3]-(p2) where not (p)-[:RELATIONSHIP3]->(p2) return COUNT(p2)"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	System.out.printf( "Query 4:" + " %s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
    	 	 // end::execute[]
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
     		
     		//Query5
    		start = System.currentTimeMillis();
            try ( Transaction tx = graphDb.beginTx() )
            {
            	int count=0;
            	Node startPoints= graphDb.findNode(labelPerson,"name","Person1");
            			Traverser friendsTraverser = getFriends( startPoints, Direction.OUTGOING,2 );
            			Traverser friendTraverser = getFriends( startPoints, Direction.OUTGOING,1 );
            			{
            				for ( Path friendPath : friendsTraverser ) {
            					Traverser NoN =getFriends(friendPath.endNode(), Direction.OUTGOING,3);
            					for ( Path N : NoN ) {
                    				for ( Path Path : friendTraverser ) {
                    					if(N.endNode() != Path.endNode()) {
                    						count++;
            							}
                    				}
            					}
            				}    
            			}   	 	
            	System.out.println("Query 5:" +"Found new user " + count );		
            }
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
     		
     		//Query6
    		start = System.currentTimeMillis();
            try ( Transaction tx = graphDb.beginTx() )
            {
            	int count=0;
            	Node startPoints= graphDb.findNode(labelPerson,"name","Person1");
            			Traverser actionsTraverser = getActions( startPoints, Direction.OUTGOING,2 );
            			Traverser friendsTraverser = getFriends( startPoints, Direction.OUTGOING,1 );
            			{
            				for ( Path actionPath : actionsTraverser ) {
            					Traverser NoN =getActions(actionPath.endNode(), Direction.INCOMING,2);
            					for ( Path N : NoN ) {
                    				for ( Path Path : friendsTraverser ) {
                    					if(N.endNode() != Path.endNode()) {
                    						count++;
            							}
                    				}
            					}
            				}    
            			}   	 	
            	System.out.println("Query 6:" +"Found new user " + count );		
            }
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
     		
    		//Query7
    		start = System.currentTimeMillis();
    	 	try (  Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("match t=(p:Person{name :'Person1'})-[:Relationship3*1..4]->(p1:Person)-[:Relationship3*1..6]->(p2) where not (p)-[:Relationship3*1..4]->(p2) return p2.name"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	System.out.printf( "Query 7:" + " %s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
    	 	 // end::execute[]
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
     		
    		//Query8
    		start = System.currentTimeMillis();
    	 	try (  Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("match t=(p:Person{name :'Person1'})-[:Relationship2*1..4]->(p1:Action)<-[:Relationship2*1..6]-(p2) where not (p)-[:Relationship3*1..4]->(p2) return p2.name"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	System.out.printf( "Query 8:" + " %s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
    	 	 // end::execute[]
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
     		
    		//Query9
    		/**start = System.currentTimeMillis();
    	 	try (  Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("CALL db.index.fulltext.queryNodes(\"titlesAndDescriptions\", \"Person1\") YIELD node, score\n RETURN node.name, score"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	System.out.printf( "Query 4:" + " %s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
    	 	 // end::execute[]
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++; **/
    	
    	}    
    }

    private Traverser getFriends(final Node person, Direction dir, int i )
    {
        TraversalDescription td = graphDb.traversalDescription()
                .breadthFirst()
                .relationships( RelTypes.RELATIONSHIP4, dir )
                .evaluator( Evaluators.atDepth(i) );
        return td.traverse( person );
    }
    
    private Traverser getActions(final Node person, Direction dir, int i )
    {
        TraversalDescription td = graphDb.traversalDescription()
                .breadthFirst()
                .relationships( RelTypes.RELATIONSHIP2, dir )
                .evaluator( Evaluators.atDepth(i) );
        return td.traverse( person );
    }
    
    void write_data() {
    		try {
    			FileWriter csvWriter = new FileWriter("newQueries.csv");
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
