package Neo4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
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
import org.neo4j.io.fs.FileUtils;

import Constants.CONSTANTS;

public class Init_Neo4j2
{
   // private static final File databaseDirectory = new File( "target/neo4j-query-1-6" );

    private static final File databaseDirectory = new File( "target/smallTest" );

    public String greeting;

	int timeIndex=0;
    // tag::vars[]
    GraphDatabaseService graphDb;
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
    	Init_Neo4j2 hello = new Init_Neo4j2();
    	hello.init();
        hello.Query1();
        hello.Query();
        hello.write_data();
        //hello.removeData();
        hello.deleteIndex();
        hello.shutDown();
    }
    
    void init() throws IOException {
    	boolean test=false;
    	FileUtils.deleteRecursively( databaseDirectory );
    	// tag::startDb[]
    	graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( databaseDirectory );
    	registerShutdownHook( graphDb );
    	createIndex();
    	createActions();
    	for(int i=0;i<CONSTANTS.nodes;i++) {
    		createPersons(i);    
    		createRelationships1_2(i);  
    		test= i==CONSTANTS.nodes-1;
    	} 	
    	if(test) {
    		for(int j=0;j<CONSTANTS.nodes;j++) {
    			createRelationships3_4(j); 		
    		}
    	}
    }
    
    void createIndex() throws IOException
    {
        IndexDefinition indexDefinition;
        try ( Transaction tx = graphDb.beginTx() )
        {
            Schema schema = graphDb.schema();
            indexDefinition = schema.indexFor( Label.label( "Person" ) )
                    .on( "name" )
                    .create();
            tx.success();
        }
        
        try ( Transaction tx = graphDb.beginTx() )
        {
            Schema schema = graphDb.schema();
            indexDefinition = schema.indexFor( Label.label( "Person" ) )
                    .on( "attribute" )
                    .create();
            tx.success();
        }
    }
    
    void deleteIndex()throws IOException
    {
        try ( Transaction tx = graphDb.beginTx() )
        {
            Label label = Label.label( "Person" );
            for ( IndexDefinition indexDefinition : graphDb.schema()
                    .getIndexes( label ) )
            {
                // There is only one index
                indexDefinition.drop();
            }

            tx.success();
        }      
    }
    
    
    void createActions() throws IOException
    {
        try ( Transaction tx = graphDb.beginTx() )
        {
            Label labelAction = Label.label( "Action" );
            for (int i=0;i<=1;i++) {
        		Actions1[i] = graphDb.createNode(labelAction);
        		Actions1[i].setProperty( "name", "Action1_" + i );
        		Actions1[i].setProperty( "attribute",  i*20 );
        		Actions2[i] = graphDb.createNode(labelAction);
        		Actions2[i].setProperty( "name", "Action2_" + i );
        		Actions2[i].setProperty( "attribute",  i*20 );
            }
        	tx.success();
        }
    }
    
    void createPersons(int i) throws IOException
    {
        try ( Transaction tx = graphDb.beginTx() )
        {
        	Label label = Label.label( "Person" );
        	Persons[i] = graphDb.createNode(label);
        	Persons[i].setProperty( "name", "Person" + i );
        	Persons[i].setProperty( "attribute",  i );    		
        	tx.success();
        }
    }
    
    void createRelationships1_2(int i) throws IOException
     {
        try ( Transaction tx = graphDb.beginTx() )
        {
        	Persons[i].createRelationshipTo( Actions1[i%2], RelTypes.RELATIONSHIP1);
        	Persons[i].createRelationshipTo( Actions2[ (i+1)%2], RelTypes.RELATIONSHIP2);
        	tx.success();
        }   	
    }
     
    void createRelationships3_4(int k) throws IOException
     {
        try ( Transaction tx = graphDb.beginTx() )
        {
        	for (int j=0;j<CONSTANTS.edges/2;j++) {
        		Persons[k].createRelationshipTo( Persons[(int)(Math.random() * CONSTANTS.nodes-1)], RelTypes.RELATIONSHIP3);        
        		Persons[k].createRelationshipTo( Persons[(int)(Math.random() * CONSTANTS.nodes-1)], RelTypes.RELATIONSHIP4);
        	}
        	tx.success();
        }   	
        try ( Transaction tx = graphDb.beginTx() )
        {
        	for (int j=CONSTANTS.edges/2;j<CONSTANTS.edges;j++) {
        		Persons[k].createRelationshipTo( Persons[(int)(Math.random() * CONSTANTS.nodes-1)], RelTypes.RELATIONSHIP3);        
        		Persons[k].createRelationshipTo( Persons[(int)(Math.random() * CONSTANTS.nodes-1)], RelTypes.RELATIONSHIP4);
        	}
        	tx.success();
        }  
    }
    
    void Query() { 
        try ( Transaction tx = graphDb.beginTx() )
        {     	
        	Label labelPerson = Label.label( "Person" );

        	Node N = graphDb.findNode(labelPerson, "attribute", 250);
        			Traverser friendsTraverser = getFriends( N, Direction.BOTH );
        				for ( Path friendPath : friendsTraverser ) {
        					if((Integer)friendPath.endNode().getProperty("attribute") >= 15) {	
        						System.out.println("Query 19:" +"The  user " + 613 + " is connected with " + friendPath.endNode().getProperty( "name" ));	
        					}
        				}
        }
    }
    
    void Query1() { 
    	Label labelPerson = Label.label( "Person" );
    	Label labelAction = Label.label( "Action" );
    	for (int i=0;i<CONSTANTS.iterations;i++) {
    		timeIndex=0;
    		
    		//Query1
    		double start = System.currentTimeMillis();
    	 	try (  Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("MATCH ()-[Y:RELATIONSHIP3]->() return COUNT(Y)") )
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "Query 1:" + " %s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
    	 	 // end::execute[]
     		double end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
    		//Query2
    		start = System.currentTimeMillis();
    	 	try ( 	Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("MATCH (X:Person)-[:RELATIONSHIP4]-(Y) WHERE X.attribute>=250 AND Y.attribute>=15  RETURN COUNT(Y)"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "Query 2:"  + "%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ;
     		timeIndex++;
    		
     		//Query3
    		start = System.currentTimeMillis();
    	 	try ( 	Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("MATCH (X:Person)-[:RELATIONSHIP4]->(Y) WHERE X.attribute>=250 AND Y.attribute>=15  RETURN COUNT(DISTINCT(Y))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "Query 3:" + "%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ;
     		timeIndex++;
     		
    		//Query4
    		start = System.currentTimeMillis();
    	 	try ( 	Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("MATCH (X:Person)-[:RELATIONSHIP4]-(Y) WHERE X.attribute>=250 AND Y.attribute>=15  RETURN COUNT(DISTINCT(Y))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "Query 4:" + "%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ;
     		timeIndex++;
     		
     		//Query5
     		start = System.currentTimeMillis();
    	 	try ( 	Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("MATCH (X:Person)<-[:RELATIONSHIP2]-(Y) WHERE X.attribute>=250 AND Y.attribute>=15  RETURN COUNT(DISTINCT(Y))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	System.out.printf( "Query 5:" + "%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ;
     		timeIndex++;
     		
     		//Query6
    		start = System.currentTimeMillis();
    	 	try ( 	Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("MATCH (X:Person)-[:RELATIONSHIP2]->(Y) WHERE X.attribute>=250 AND Y.attribute>=15  RETURN COUNT(DISTINCT(Y))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "Query 6:" + "%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ;
     		timeIndex++;
     		
     		//Query7
    		start = System.currentTimeMillis();
    	 	try ( 	Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("MATCH (X:Person)-[:RELATIONSHIP2]-(Y) WHERE X.attribute>=250 AND Y.attribute>=15  RETURN COUNT(DISTINCT(Y))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "Query 7:" +"%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ;
     		timeIndex++;
     		
     		//Query8
    		start = System.currentTimeMillis();
    	 	try ( 	Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("MATCH (X:Person {name:'Person1'})-[:RELATIONSHIP3]->(n1)<-[:RELATIONSHIP3]-(Y:Person) RETURN COUNT(DISTINCT(n1))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "Query 8:" +"%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ;
     		timeIndex++;
     		
     		//Query9
    		start = System.currentTimeMillis();
    	 	try ( 	Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("MATCH (X:Person)-[:RELATIONSHIP3]->(n1) With COLLECT(n1) as n MATCH (Y:Person)-[:RELATIONSHIP3]->(n1) WHERE n1 in n RETURN COUNT(DISTINCT(n1))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "Query 9:" +"%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ;
     		timeIndex++;
     		
     		//Query10
    		start = System.currentTimeMillis();
    	 	try ( 	Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("MATCH (p:Person {name:'Person213'}) return p"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "Query 10:" +"%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ;	
     		timeIndex++;
     		
     		//Query11
    		start = System.currentTimeMillis();
    	 	try ( 	Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("MATCH (p:Person) WHERE p.name='Person213' return p"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "Query 11:" +"%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ;	
     		timeIndex++;
     		
     		//Query12
    		start = System.currentTimeMillis();
            try ( Transaction tx = graphDb.beginTx() )
            {
            	try ( ResourceIterator<Node> users =
                        graphDb.findNodes( labelPerson, "name", "Person213" ) )
            	{
            		ArrayList<Node> userNodes = new ArrayList<>();
                    while ( users.hasNext() )
                    {
                        userNodes.add( users.next() );
                    }
                    for ( Node node : userNodes )
                    {
                        //System.out.println("Query 12:" +"The username of user " + 213 + " is " + node.getProperty( "name" ));
                    }
            	}
            }
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
     		
     		//Query13
    		start = System.currentTimeMillis();
    	 	try ( 	Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("MATCH (X:Person)-[:RELATIONSHIP4]->(Y) WHERE X.name='Person250' AND Y.name='Person15' RETURN COUNT(Y)"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "Query 13:" +"%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ;
     		timeIndex++;
     		
     		//Query14
    		start = System.currentTimeMillis();
    	 	try ( 	Transaction ignored = graphDb.beginTx();
    	 			Result result = graphDb.execute("MATCH (X:Person{attribute: 250})-[:RELATIONSHIP4]->(Y:Person{attribute:15}) RETURN COUNT(Y)"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "Query 14:" +"%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ;
     		timeIndex++;
     		
     		//Query15
    		start = System.currentTimeMillis();
            try ( Transaction tx = graphDb.beginTx() )
            {
            	ResourceIterable<Node> startPoints= graphDb.getAllNodes();
            	for(Node n1 :startPoints) {
            		if ((Integer) n1.getProperty("attribute") >=250) {
            			Traverser friendsTraverser = getFriends( n1, Direction.INCOMING );
            			{
            				for ( Path friendPath : friendsTraverser ) {
            					if((Integer)friendPath.endNode().getProperty("attribute") >= 15) {	
            						//System.out.println("Query 15:" +"The username of user " + 613 + " is " + friendPath.endNode().getProperty( "name" ));	
            					}
            				}            				
            			}   	
            		}
            	}
            }
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
     		
     		//Query16
    		start = System.currentTimeMillis();
            try ( Transaction tx = graphDb.beginTx() )
            {
            	ResourceIterable<Node> startPoints= graphDb.getAllNodes();
            	for(Node n1 :startPoints) {
            		if ((Integer) n1.getProperty("attribute") >=250) {
            			Traverser friendsTraverser = getFriends( n1, Direction.OUTGOING );
            			{
            				for ( Path friendPath : friendsTraverser ) {
            					if((Integer)friendPath.endNode().getProperty("attribute") >= 15) {	
            						//System.out.println("Query 16:" +"The username of user " + 613 + " is " + friendPath.endNode().getProperty( "name" ));	
            					}
            				}            				
            			}   	
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
            	ResourceIterable<Node> startPoints= graphDb.getAllNodes();
            	for(Node n1 :startPoints) {
            		if ((Integer) n1.getProperty("attribute") >=250) {
            			Traverser friendsTraverser = getFriends( n1, Direction.BOTH );
            			{
            				for ( Path friendPath : friendsTraverser ) {
            					if((Integer)friendPath.endNode().getProperty("attribute") >= 15) {	
            						//System.out.println("Query 17:" +"The username of user " + 613 + " is " + friendPath.endNode().getProperty( "name" ));	
            					}
            				}            				
            			}   	
            		}
            	}
            }
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[timeIndex][i] = end - start ; 
     		timeIndex++;
     		
     		//Query18
    		start = System.currentTimeMillis();
            try ( Transaction tx = graphDb.beginTx() )
            {
            	Node N = graphDb.findNode(labelPerson, "attribute", 250);
            			Traverser friendsTraverser = getFriends( N, Direction.BOTH );
            				for ( Path friendPath : friendsTraverser ) {
            					if((Integer)friendPath.endNode().getProperty("attribute") >= 15) {	
            						//System.out.println("Query 19:" +"The username of user " + 613 + " is " + friendPath.endNode().getProperty( "name" ));	
            					}
            				}            				          	
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
                .relationships( RelTypes.RELATIONSHIP4, dir )
                .evaluator( Evaluators.atDepth(1) );
        return td.traverse( person );
    }
    
    void write_data() {
    		try {
    			FileWriter csvWriter = new FileWriter("Queries.csv");
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
    
    void removeData()
    {
        try ( Transaction tx = graphDb.beginTx() )
        {
            // tag::removingData[]
            // let's remove the data
        	for (int i=0; i>CONSTANTS.nodes;i++) {
                Persons[i].delete();
                Persons[i].getSingleRelationship( RelTypes.RELATIONSHIP1, Direction.BOTH ).delete();
                Persons[i].getSingleRelationship( RelTypes.RELATIONSHIP2, Direction.BOTH ).delete();
                Persons[i].getSingleRelationship( RelTypes.RELATIONSHIP3, Direction.BOTH ).delete();
                Persons[i].getSingleRelationship( RelTypes.RELATIONSHIP4, Direction.BOTH ).delete();
        	}
            // end::removingData[]

            tx.success();
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
