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
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.io.fs.FileUtils;

import Constants.CONSTANTS;

public class Init_Neo4j2
{
    private static final File databaseDirectory = new File( "target/neo4j-query-1-6" );

    public String greeting;

    // tag::vars[]
    GraphDatabaseService graphDb;
    List<Relationship> relationships = new ArrayList<>();
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
        //hello.createDb();
        //hello.createRE();
        hello.Query1();
        hello.write_data();
        //hello.removeData();
        hello.Query();
        hello.shutDown();
    }
    
    void init() throws IOException {
    	FileUtils.deleteRecursively( databaseDirectory );
    	// tag::startDb[]
    	graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( databaseDirectory );
    	registerShutdownHook( graphDb );
    	for(int i=0;i<=CONSTANTS.nodes-2000;i+=2000) {
    		createP(i);    		
    	}
    	createA();
    	for(int i=0;i<=CONSTANTS.nodes-2000;i+=2000) {
    		createR(i);    		
    	}	
    }
    
    void createA() throws IOException
    {
        try ( Transaction tx = graphDb.beginTx() )
        {
            Label labelAction = Label.label( "Action" );
            for (int i=0;i<=1;i++) {
        		Actions1[i] = graphDb.createNode(labelAction);
        		Actions1[i].setProperty( "name", "Action1_" + i );
        		Actions1[i].setProperty( "attribute",  i*5 );
        		Actions2[i] = graphDb.createNode(labelAction);
        		Actions2[i].setProperty( "name", "Action2_" + i );
        		Actions2[i].setProperty( "attribute",  i*5 );
            }
        	tx.success();
        }
    }
    
    void createP(int k) throws IOException
    {
    	int h=k+2000;
        try ( Transaction tx = graphDb.beginTx() )
        {
            Label label = Label.label( "Person" );
        	for (int i=k; i<h;i++) {
        		Persons[i] = graphDb.createNode(label);
        		Persons[i].setProperty( "name", "Person" + i );
        		Persons[i].setProperty( "attribute",  i );
        	}
        	tx.success();
        }
    }
    
     void createR(int k) throws IOException
     {
        int h=k+2000;
        try ( Transaction tx = graphDb.beginTx() )
        {
        	for (int i=k; i<h;i++) {
        		relationships.add(Persons[i].createRelationshipTo( Actions1[ i%2], RelTypes.RELATIONSHIP1));
        		relationships.add(Persons[i].createRelationshipTo( Actions2[ (i+1)%2], RelTypes.RELATIONSHIP2));
        		for(int j=0;j<=CONSTANTS.edges;j++) {
            		relationships.add(Persons[i].createRelationshipTo( Persons[(int)(Math.random() * CONSTANTS.nodes-1)], RelTypes.RELATIONSHIP3));
            		relationships.add(Persons[i].createRelationshipTo( Persons[(int)(Math.random() * CONSTANTS.nodes-1)], RelTypes.RELATIONSHIP4));
        		}
        	}           
        	tx.success();
        }
    	
    }

    void createDb() throws IOException
    {
    	FileUtils.deleteRecursively( databaseDirectory );
    	// tag::startDb[]
    	graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( databaseDirectory );
    	registerShutdownHook( graphDb );

        try ( Transaction tx = graphDb.beginTx() )
        {
            Label label = Label.label( "Person" );
        	for (int i=0; i<2000;i++) {
        		Persons[i] = graphDb.createNode(label);
        		Persons[i].setProperty( "name", "Person" + i );
        		Persons[i].setProperty( "attribute",  i );
        	}
        	tx.success();
        }

        try ( Transaction tx = graphDb.beginTx() )
        {
            Label labelAction = Label.label( "Action" );
            for (int i=0;i<=1;i++) {
        		Actions1[i] = graphDb.createNode(labelAction);
        		Actions1[i].setProperty( "name", "Action1_" + i );
        		Actions1[i].setProperty( "attribute",  i*5 );
        		Actions2[i] = graphDb.createNode(labelAction);
        		Actions2[i].setProperty( "name", "Action2_" + i );
        		Actions2[i].setProperty( "attribute",  i*5 );
            }
        	tx.success();
        }
    }
    void createRE() throws IOException{
        try ( Transaction tx = graphDb.beginTx() )
        {
        	for (int i=0; i<2000;i++) {
        		relationships.add(Persons[i].createRelationshipTo( Actions1[ i%2], RelTypes.RELATIONSHIP1));
        		relationships.add(Persons[i].createRelationshipTo( Actions2[ (i+1)%2], RelTypes.RELATIONSHIP2));
        		for(int j=0;j<=CONSTANTS.edges;j++) {
            		relationships.add(Persons[i].createRelationshipTo( Persons[(int)(Math.random() * CONSTANTS.nodes-1)], RelTypes.RELATIONSHIP3));
            		relationships.add(Persons[i].createRelationshipTo( Persons[(int)(Math.random() * CONSTANTS.nodes-1)], RelTypes.RELATIONSHIP4));
        		}
        	}           
        	tx.success();
        }

    }
    
    void Query() { 
    	for (int i=0;i<=CONSTANTS.iterations;i++) {
    		double start = System.currentTimeMillis();
    	 	try ( Result result = graphDb.execute("MATCH (X:Person)- return COUNT(X)") )
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	System.out.printf( "%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
    	}
    }
    
    void Query1() { 
    	for (int i=0;i<=CONSTANTS.iterations;i++) {
    		double start = System.currentTimeMillis();
    	 	try ( Result result = graphDb.execute("MATCH ()-[Y:RELATIONSHIP3]->() return COUNT(Y)") )
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		double end = System.currentTimeMillis();
     		CONSTANTS.queries[0][i] = end - start ; 
     		
    		start = System.currentTimeMillis();
    	 	try ( Result result = graphDb.execute("MATCH (X:Person)<-[:Relationship4]-(Y) WHERE X.attribute>=250 AND Y.attribute>=15  RETURN COUNT(DISTINCT(Y))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[1][i] = end - start ;
     		//System.out.println( CONSTANTS.times[i]);
     		
    		start = System.currentTimeMillis();
    	 	try ( Result result = graphDb.execute("MATCH (X:Person)-[:Relationship4]->(Y) WHERE X.attribute>=250 AND Y.attribute>=15  RETURN COUNT(DISTINCT(Y))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[2][i] = end - start ;
     		
    		start = System.currentTimeMillis();
    	 	try ( Result result = graphDb.execute("MATCH (X:Person)-[:Relationship4]-(Y) WHERE X.attribute>=250 AND Y.attribute>=15  RETURN COUNT(DISTINCT(Y))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[3][i] = end - start ;
     		
     		start = System.currentTimeMillis();
    	 	try ( Result result = graphDb.execute("MATCH (X:Person)<-[:Relationship2]-(Y) WHERE X.attribute>=250 AND Y.attribute>=15  RETURN COUNT(DISTINCT(Y))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[4][i] = end - start ;
     		//System.out.println( CONSTANTS.times[i]);
     		
    		start = System.currentTimeMillis();
    	 	try ( Result result = graphDb.execute("MATCH (X:Person)-[:Relationship2]->(Y) WHERE X.attribute>=250 AND Y.attribute>=15  RETURN COUNT(DISTINCT(Y))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[5][i] = end - start ;
     		
    		start = System.currentTimeMillis();
    	 	try ( Result result = graphDb.execute("MATCH (X:Person)-[:Relationship2]-(Y) WHERE X.attribute>=250 AND Y.attribute>=15  RETURN COUNT(DISTINCT(Y))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[6][i] = end - start ;
     		
    		start = System.currentTimeMillis();
    	 	try ( Result result = graphDb.execute("MATCH (X:Person)-[:Relationship3]->(n1)<-[:Relationship3]-(Y:Person) RETURN COUNT(DISTINCT(n1))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[7][i] = end - start ;
     		
    		start = System.currentTimeMillis();
    	 	try ( Result result = graphDb.execute("MATCH (X:Person)-[:Relationship3]->(n1) With COLLECT(n1) as n MATCH (Y:Person)-[:Relationship3]->(n1) WHERE n1 in n RETURN COUNT(DISTINCT(n1))"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	//System.out.printf( "%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[8][i] = end - start ;
     		
    		/**start = System.currentTimeMillis();
    	 	try ( Result result = graphDb.execute("start p1=node:Person(\"name:Person1\") match (p1)-[:RELATIONSHIP3]-(f) where f.attribute < 1000 Return f"))
    	 	{
    	     	while ( result.hasNext() )
    	     	{
    	         	Map<String, Object> row = result.next();
    	         	for ( String key : result.columns() )
    	         	{
    	             	System.out.printf( "%s = %s%n", key, row.get( key ) );
    	         	}
    	     	}
    	 	}
     		end = System.currentTimeMillis();
     		CONSTANTS.queries[9][i] = end - start ;	
    		 **/
    	}
    	
    	
    }

    void write_data() {
    		try {
    			FileWriter csvWriter = new FileWriter("Queries.csv");
    			for (int i=0;i<=CONSTANTS.iterations;i++) {
    				for(int j=0; j<CONSTANTS.queries.length;j++) { 
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
