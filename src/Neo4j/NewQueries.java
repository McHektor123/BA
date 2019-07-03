
package Neo4j;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import org.neo4j.graphdb.traversal.Uniqueness;

import Constants.CONSTANTS;

public class NewQueries
{
   // private static final File databaseDirectory = new File( "target/neo4j-query-1-6" );

    private static final File databaseDirectory = new File( "target/smallTest" );

	int timeIndex=0;
    // tag::vars[]
	private static GraphDatabaseService graphDb =new GraphDatabaseFactory().newEmbeddedDatabase( databaseDirectory );
	Node Persons[] = new Node[CONSTANTS.nodes+1];
    Node Actions1[] = new Node[2];
    Node Actions2[] = new Node[2];
    Label labelPerson = Label.label( "Person" );
	Label labelAction = Label.label( "Action" );
	double start=0;
	double end=0;
	boolean output=true;
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
    	NewQueries hello = new NewQueries();
    	//hello.Index1();
    	//hello.Index2();
    	//hello.Index3();
    	//hello.createIndex();
    	//hello.Query1();
    	//hello.write_data("Query1.csv");
    	//hello.Query2();
    	//hello.write_data("Query2.csv");
    	hello.Query18();
    	hello.write_data("Query18.csv");
        hello.Query3();
        hello.write_data("Query3.csv");
        //hello.Query4();
        //hello.write_data("Query4.csv");
        hello.Query5();
        hello.write_data("Query5.csv");
        //hello.Query6();
        //hello.write_data("Query6.csv");
        //hello.Query7();
        //hello.write_data("Query7.csv");
        //hello.Query8();
        //hello.write_data("Query8.csv");
        //hello.Query9();
        //hello.write_data("Query9.csv");
        //hello.Query10();
        //hello.write_data("Query10.csv");
        //hello.Query11();
        //hello.write_data("Query11.csv");
        //
        hello.Query12();
        //hello.write_data("Query12.csv");
        //hello.Query13();
        //hello.write_data("Query13.csv");
        //hello.Query14();
        //hello.write_data("Query14.csv");
        //hello.Query15();
        //hello.write_data("Query15.csv");
        //hello.Query16();
        //hello.write_data("Query16.csv");
        //hello.Query16_1();
        //hello.write_data("Query16_1.csv");
        //hello.Query17();
        //hello.write_data("Query17.csv");
        //hello.Query17_1();
       // hello.write_data("Query17_1.csv");
        hello.shutDown();
    }
    void Index1() { 
    		//Query1
	 	try ( 	Transaction ignored = graphDb.beginTx();
	 			Result result = graphDb.execute("CREATE INDEX ON :Person(name)"))
	 	{

	 	}
	
    }
    void Index2() { 
		//Query1
 	try ( 	Transaction ignored = graphDb.beginTx();
 			Result result = graphDb.execute("CREATE INDEX ON :Person(attribute)"))
 	{

 	}

}
    void Index3() { 
		//Query1
 	try ( 	Transaction ignored = graphDb.beginTx();
 			Result result = graphDb.execute("CREATE INDEX ON :Action(attribute)"))
 	{

 	}

}
   
    void Query1() { 
    	for (int i=0;i<CONSTANTS.iterations;i++) {
    		timeIndex=0;
    		//Query1
    		start = System.currentTimeMillis();
    	 	try (  Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute(""
    	 					+ "MATCH (Y1:Person{name :'Person1'})-[:RELATIONSHIP4]->(X)   WITH AVG(X.attribute) as A1 " + 
    	 					"MATCH (Y1:Person{name :'Person1'})-[:RELATIONSHIP4]->(X) WHERE A1>=25000 WITH Y1 as Y1 " + 
    	 					"MATCH (Y2:Person{name :'Person2'})-[:RELATIONSHIP4]->(X)   WITH AVG(X.attribute) as A2, Y1 " + 
    	 					"MATCH (Y2:Person{name :'Person2'})-[:RELATIONSHIP4]->(X) WHERE A2>=25000  WITH Y2 as Y2, Y1 " + 
    	 					"MATCH (Y3:Person{name :'Person3'})-[:RELATIONSHIP4]->(X)   WITH AVG(X.attribute) as A3, Y1,Y2 " + 
    	 					"MATCH (Y3:Person{name :'Person3'})-[:RELATIONSHIP4]->(X) WHERE A3>=25000 WITH Y3 as Y3,Y1,Y2 " + 
    	 					"RETURN Y1.name,Y2.name,Y3.name"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	         		if(output) {
    	         			System.out.printf( "Query 1:" + " %s = %s%n", key, row.get( key ) );
    	         		}
    	         	}
    	     	}
    	 	}
    	 	// end::execute[]
    	 	end = System.currentTimeMillis();
    	 	CONSTANTS.queries[timeIndex][i] = end - start ; 
    	 	timeIndex++;
    	}
    }
    void Query2() { 
    	Set<String> friendsResult = new HashSet<String>();
    	for (int i=0;i<CONSTANTS.iterations;i++) {
    		timeIndex=0;	
     		//Query2
    		start = System.currentTimeMillis();
            try ( Transaction tx = graphDb.beginTx() )
            {
            	int avg=0;
            	Set<Node> friends = new HashSet<Node>();
            	Node Person1 = graphDb.findNode(labelPerson, "name", "Person1");
            	Node Person2 = graphDb.findNode(labelPerson, "name", "Person2");
            	Node Person3 = graphDb.findNode(labelPerson, "name", "Person3");
            	friends.add(Person1);
            	friends.add(Person2);
            	friends.add(Person3);
            	for(Node n : friends) {	
            			avg = 0;
            			Traverser friendsTraverser = getFriendsBreathFirst( n, Direction.OUTGOING,1 );
            			{
            				for ( Path friendPath : friendsTraverser ) {
            					avg += (Integer)friendPath.endNode().getProperty("attribute");
            				}    
            				if( friendsTraverser.metadata().getNumberOfPathsReturned()!=0 &&  avg/friendsTraverser.metadata().getNumberOfPathsReturned() >=25000) {
            					friendsResult.add((String)n.getProperty("name"));
            				}
            			}   	
            	}
            	for(String n : friendsResult) {	
            		if(output) {
            			System.out.println("Query 2:" +"The result of user  is " + n);		
            		}
            	}
            }
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
    	}
    }
    void Query3() { 
    	for (int i=0;i<CONSTANTS.iterations;i++) {
    		timeIndex=0;	
    		//Query3
    		start = System.currentTimeMillis();
    	 	try (  Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("match (p:Person{name :'Person1'})-[:RELATIONSHIP3]->(p1:Person)-[:RELATIONSHIP3]->(p2) "
    	 					+ "where not (p)-[:RELATIONSHIP3]->(p2) return COUNT(DISTINCT(p2))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	         		if(output) {
    	         			System.out.printf( "Query 3:" + " %s = %s%n", key, row.get( key ) );
    	         		}
    	         	}    	     		
    	     	}
    	 	}
    	 	 // end::execute[]
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
    	}
    }

    void Query5() { 
    	for (int i=0;i<CONSTANTS.iterations;i++) {
    		timeIndex=0;	
     		//Query5
    		start = System.currentTimeMillis();
            try ( Transaction tx = graphDb.beginTx() )
            {
            	Set<Node> personSet = new HashSet<Node>();
            	Set<Node> friendsSet = new HashSet<Node>();
            	Node startPoints= graphDb.findNode(labelPerson,"name","Person1");
            	Traverser friendsTraverser = getFriendsBreathFirst( startPoints, Direction.OUTGOING,2 );
            	for ( Path friendsPath : friendsTraverser ) {
            		personSet.add(friendsPath.endNode());
            		Traverser friendsOfFriendsTraverser = getFriendsBreathFirst( friendsPath.endNode(), Direction.OUTGOING,1 );           		
            		for ( Path friendOfFriendsPath : friendsOfFriendsTraverser ) {
            			friendsSet.add(friendOfFriendsPath.endNode());
            		}
            	} 
            	friendsSet.removeAll(personSet);
            	if(output) {
            		System.out.println("Query 5:" + "Found new user " + friendsSet.size() );	
            	}
            }
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
    	}
    }
  
    /**void Query11() { 
    	for (int i=0;i<CONSTANTS.iterations;i++) {
    		timeIndex=0;	
    		//Query11
    		start = System.currentTimeMillis();
    	 	try (  Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("CALL db.index.fulltext.createNodeIndex(\"titlesAndDescriptions\",[\"Person\"],[\"name\"])\n" + 
    	 					"CALL db.index.fulltext.queryNodes(\"titlesAndDescriptions\", \"Person1\") YIELD node, score\n" + 
    	 					"RETURN node.name, score\n" + 
    	 					""))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	         		if(output) {
    	         			System.out.printf( "Query 11:" + " %s = %s%n", key, row.get( key ) );
    	         		}
    	         	}
    	     	}
    	 	}
    	 	 // end::execute[]
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;   	
    	}    
    }**/
    void Query11() { 
    	for (int i=0;i<CONSTANTS.iterations;i++) {
    		timeIndex=0;	
    		//Query12
    		start = System.currentTimeMillis();
    	 	try (  Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("match (p:Person{name :'Person1'})-[:RELATIONSHIP3*1..3]->(p1:Person) return COUNT(DISTINCT(p1))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	         		if(output) {
    	         			System.out.printf( "Query 12:" + " %s = %s%n", key, row.get( key ) );
    	         		}
    	         	}    	     		
    	     	}
    	 	}
    	 	 // end::execute[]
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
    	}
    }
    void Query12() { 
    	for (int i=0;i<CONSTANTS.iterations;i++) {
    		timeIndex=0;	
    		//Query12
    		start = System.currentTimeMillis();
    	 	try (  Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("match (p:Person{name :'Person1'})-[:RELATIONSHIP3*3]->(p1:Person) return COUNT(DISTINCT(p1))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	         		if(output) {
    	         			System.out.printf( "Query 12:" + " %s = %s%n", key, row.get( key ) );
    	         		}
    	         	}    	     		
    	     	}
    	 	}
    	 	 // end::execute[]
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
    	}
    }
    void Query13() { 
    	for (int i=0;i<CONSTANTS.iterations;i++) {
    		timeIndex=0;	
    		//Query13
    		start = System.currentTimeMillis();
    	 	try (  Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("match (p:Person{name :'Person1'})-[:RELATIONSHIP3*3]->(p1:Person) return COUNT(DISTINCT(p1))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	         		if(output) {
    	         			System.out.printf( "Query 13:" + " %s = %s%n", key, row.get( key ) );
    	         		}
    	         	}    	     		
    	     	}
    	 	}
    	 	 // end::execute[]
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
    	}
    }
    void Query15() { 
    	for (int i=0;i<CONSTANTS.iterations;i++) {
    		timeIndex=0;
    		//Query15
    		start = System.currentTimeMillis();
    	 	try (  Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute(""
    	 					+ "MATCH (Y1:Person{name :'Person1'})-[:RELATIONSHIP4]->(X) WITH AVG(X.attribute) as A1\n" + 
    	 					"RETURN A1"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	         		if(output) {
    	         			System.out.printf( "Query 15:" + " %s = %s%n", key, row.get( key ) );
    	         		}
    	         	}
    	     	}
    	 	}
    	 	// end::execute[]
    	 	end = System.currentTimeMillis();
    	 	CONSTANTS.queries[timeIndex][i] = end - start ; 
    	 	timeIndex++;
    	}
    }
    void Query16() { 
    	for (int i=0;i<CONSTANTS.iterations;i++) {
    		timeIndex=0;	
     		//Query16
    		start = System.currentTimeMillis();
            try ( Transaction tx = graphDb.beginTx() )
            {
            	Set<Node> friends = new HashSet<Node>();
            	Node startPoints= graphDb.findNode(labelPerson,"name","Person1");
            	Traverser friendsTraverser = getFriendsBreathFirst( startPoints, Direction.OUTGOING,2 );
            		{
            			for ( Path friendPath : friendsTraverser ) {
            				friends.add(friendPath.endNode());
            			}   
            		}  
            			if(output) {
            				System.out.println("Query 16:" + "Found new user " + friends.size() );	
            			}
            }
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
    	}
    }  
    void Query16_1() { 
    	for (int i=0;i<CONSTANTS.iterations;i++) {
    		timeIndex=0;	
     		//Query16
    		start = System.currentTimeMillis();
            try ( Transaction tx = graphDb.beginTx() )
            {
            	Set<Node> friends = new HashSet<Node>();
            	Node startPoints= graphDb.findNode(labelPerson,"name","Person1");
            	Traverser friendsTraverser = getFriendsFirstDeepFirst( startPoints, Direction.OUTGOING,2 );
            		{
            			for ( Path friendPath : friendsTraverser ) {
            				friends.add(friendPath.endNode());
            			}   
            		}  
            			if(output) {
            				System.out.println("Query 16_1:" + "Found new user " + friends.size() );	
            			}
            }
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
    	}
    }
    void Query17() { 
    	for (int i=0;i<CONSTANTS.iterations;i++) {
    		timeIndex=0;	
     		//Query17
    		start = System.currentTimeMillis();
            try ( Transaction tx = graphDb.beginTx() )
            {
            	Set<Node> friends = new HashSet<Node>();
            	Node startPoints= graphDb.findNode(labelPerson,"name","Person1");
            	Traverser friendsTraverser = getFriendsBreathFirst( startPoints, Direction.OUTGOING,3 );
            		{
            			for ( Path friendPath : friendsTraverser ) {
            				friends.add(friendPath.endNode());
            			}   
            		}  
            			if(output) {
            				System.out.println("Query 17:" + "Found new user " + friends.size() );	
            			}
            }
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
    	}
    }
    void Query17_1() { 
    	for (int i=0;i<CONSTANTS.iterations;i++) {
    		timeIndex=0;	
     		//Query17
    		start = System.currentTimeMillis();
            try ( Transaction tx = graphDb.beginTx() )
            {
            	Set<Node> friends = new HashSet<Node>();
            	Node startPoints= graphDb.findNode(labelPerson,"name","Person1");
            	Traverser friendsTraverser = getFriendsFirstDeepFirst( startPoints, Direction.OUTGOING,3 );
            		{
            			for ( Path friendPath : friendsTraverser ) {
            				friends.add(friendPath.endNode());
            			}   
            		}  
            			if(output) {
            				System.out.println("Query 17_1:" + "Found new user " + friends.size() );	
            			}
            }
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
    	}
    }
    void Query18() { 
    	for (int i=0;i<CONSTANTS.iterations;i++) {
    		timeIndex=0;	
    		//Query18
    		start = System.currentTimeMillis();
    	 	try (  Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("match t=(p:Person{name :'Person1'})-[:RELATIONSHIP3*2]->(p1:Person)"
    	 					+ "where not (p)-[:RELATIONSHIP3]->(p1) return COUNT(Distinct(p1))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	         		if(output) {
    	         			System.out.printf( "Query 18:" + " %s = %s%n", key, row.get( key ) );
    	         		}
    	         	}
    	     	}
    	 	}
    	 	 // end::execute[]
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
    	}
    }
    private Traverser getFriendsBreathFirst(final Node person, Direction dir, int i )
    {
        TraversalDescription td = graphDb.traversalDescription()
                .breadthFirst()
                .relationships( RelTypes.RELATIONSHIP3, dir )
                .evaluator( Evaluators.atDepth(i) )
                .uniqueness(Uniqueness.NODE_GLOBAL);
        return td.traverse( person );
    }
    private Traverser getFriendsFirstDeepFirst(final Node person, Direction dir, int i )
    {
        TraversalDescription td = graphDb.traversalDescription()
                .depthFirst()
                .relationships( RelTypes.RELATIONSHIP3, dir )
                .evaluator( Evaluators.atDepth(i) )
                .uniqueness(Uniqueness.NODE_GLOBAL);
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
    
    void write_data(String path) {
    		try {
    			FileWriter csvWriter = new FileWriter(path);
    			for (int i=0;i<CONSTANTS.iterations;i++) {
    				for(int j=0; j<=timeIndex;j++) { 
    						csvWriter.write( CONSTANTS.queries[j][i] + "");
    				}
    				csvWriter.append("\n");
    		} 
    			csvWriter.close();
 			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FileWriter txtWriter;
			try {
				txtWriter = new FileWriter("Log.txt");
				txtWriter.write( path + " completed");
				txtWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print(path + "completed");
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