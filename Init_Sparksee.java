import com.sparsity.sparksee.gdb.*;
import java.io.FileNotFoundException;

public class Init_Sparksee {

	static double times[]=new double[101];
	static int nodes=1000;
	static int edges=100;
	static long Persons[]= new long[1001];
	static long Actions1[] = new long[2];
	static long Actions2[] = new long[2];
	static int iterations=100;
	static double time=0;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException
    {
        // Create a sample database
        //
        SparkseeConfig cfg = new SparkseeConfig();
        Sparksee sparksee = new Sparksee(cfg);
        cfg.setCacheMaxSize(1); // 2 GB
        Database db = sparksee.create("HelloSparksee1.gdb", "HelloSparksee");
        Session sess = db.newSession();
        Graph g = sess.getGraph();
        
        // Add a node type for the actions, with a unique identifier and two indexed attributes
        int actionType = g.newNodeType("ACTION");
        int actionNameType = g.newAttribute(actionType, "NAME", DataType.String, AttributeKind.Indexed);
        int actionAttributeType = g.newAttribute(actionType, "ATTRIBUTE", DataType.Integer, AttributeKind.Indexed);
        
        // Add a node type for the actions, with a unique identifier and two indexed attributes
        int personType = g.newNodeType("PERSON");
        int personNameType = g.newAttribute(personType, "NAME", DataType.String, AttributeKind.Indexed);
        int personAttributeType = g.newAttribute(personType, "ATTRIBUTE", DataType.Integer, AttributeKind.Indexed);

        // Add an undirected edge type with an attribute for the cast of a action
        int realationShip1Type = g.newRestrictedEdgeType("RELATIONSHIP1",personType,actionType, false);
        int realationShip2Type = g.newRestrictedEdgeType("RELATIONSHIP2",personType,actionType, false);
        int realationShip3Type = g.newRestrictedEdgeType("RELATIONSHIP3",personType,personType, false);
        int realationShip4Type = g.newRestrictedEdgeType("RELATIONSHIP4",personType,personType, false);

	// Add some action nodes
	Value value = new Value();

	long Action1_0 = g.newNode(actionType);
	g.setAttribute(Action1_0, actionNameType, value.setString("Action1_0"));
	g.setAttribute(Action1_0, actionAttributeType, value.setInteger(5));

	long Action1_1 = g.newNode(actionType);
	g.setAttribute(Action1_1, actionNameType, value.setString("Action1_1"));
	g.setAttribute(Action1_1, actionAttributeType, value.setInteger(10));

	long Action2_0 = g.newNode(actionType);
	g.setAttribute(Action2_0, actionNameType, value.setString("Action2_0"));
	g.setAttribute(Action2_0, actionAttributeType, value.setInteger(15));
	
	long Action2_1 = g.newNode(actionType);
	g.setAttribute(Action2_1, actionNameType, value.setString("Action2_1"));
	g.setAttribute(Action2_1, actionAttributeType, value.setInteger(20));

	Actions1[0] = Action1_0;
	Actions1[1] = Action1_1;
	Actions2[0] = Action2_0;
	Actions2[1] = Action2_1;
	// Add some PEOPLE nodes
	for (int i=0; i<=nodes;i++) {
		Persons[i] = g.newNode(personType);
		g.setAttribute(Persons[i], personNameType, value.setString("Person" + i));
		g.setAttribute(Persons[i], personAttributeType, value.setInteger(i));
	}
	
	// Add some CAST edges
	long anEdge;
	for (int i=0; i<=nodes;i++) {
		anEdge = g.newEdge(realationShip1Type, Persons[i], Actions1[i%2]);
		anEdge = g.newEdge(realationShip2Type, Persons[i], Actions2[(i+1)%2]);
		for(int j=0;j<=edges;j++) {
			anEdge = g.newEdge(realationShip3Type, Persons[i], Persons[(int)(Math.random() * 999)]);
			anEdge = g.newEdge(realationShip4Type, Persons[i], Persons[(int)(Math.random() * 999)]);
		}
	}
		for(int i=0;i<=iterations;i++) {
			double start = System.currentTimeMillis();
			//Objects LessThan = g.select( personAttributeType, Condition.LessEqual, value.setInteger(15));
			//Objects greaterEqual = g.select( personAttributeType, Condition.GreaterEqual, value.setInteger(250));
			Objects peopleObjs = g.select(personType);
			Objects neighbor1 = g.neighbors( Persons[1], realationShip3Type, EdgesDirection.Outgoing);
			Objects neighbors = g.neighbors( peopleObjs, realationShip3Type, EdgesDirection.Outgoing);
			Objects union = Objects.combineIntersection(neighbors, neighbor1);
			ObjectsIterator it = union.iterator();
			while (it.hasNext())
			{
				long peopleOid = it.next();
				long count=union.count();
				g.getAttribute(peopleOid,personNameType, value);
				//System.out.println(value.getString());
			}
			//System.out.println(union.count());
			double stop = System.currentTimeMillis();
			times[i]=stop-start;
			time +=stop-start;
			it.close();	
			neighbors.close();
			neighbor1.close();
			peopleObjs.close();
			union.close();
	        System.out.println(times[i]);
		}
        //System.out.println("Sparksee: " + time/iterations + " ms passed" );
        // The ObjectsIterator must be closed
        // The Objects must be closed
        //castFromBoth.close();


	//
        // Close the database
	//
        sess.close();
        db.close();
        sparksee.close();
    }
}
