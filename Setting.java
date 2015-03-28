import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Properties;



public class Setting {
    public static int    numIterations				= 30; // number to repeat the whole calculation process, meant to get the average runtime
    public static double epsilon                    = 1E-8; // threshold to discard value in P matrix
    public static double clip                       = 1E-4;
    public static double delta		                = 1E-4;
    public static int    eta		                = 2; // number of times to expand the hubs
    public static double C						= 0.75; // damping factor

    public static String nodeFile					= "node";
    public static String edgeFile					= "edge";
    public static String indexDir				    = "";
    public static String outputDir		            = ""; 
    public static String queryFile      		    = "query";
    
    public static int resultTop						= 50;
    public static int hubTop						= 100000;
    public static int iterations                    =10;
    public static int progressiveTopK				=100;
    public static int depth							=8; // maximum path length
    
    public static int[][] INAdjacency;
    public static int[][] OUTAdjacency;
    public static int[] inSize;
    public static int[] nodesIndex;
    static {
//        String filePath = System.getProperty("config").trim();
        File f = new File("config.properties");
        if (!f.exists()) {
            System.out.println("Please set the system properties first.");
            System.exit(0);
        }
        
        //System.out.println("*** config file used: " + f.getAbsolutePath() + " ***");
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(f));
            for (Field field : Setting.class.getFields()) {
                if (field.getType().getName().equals("int"))
                    setInt(prop, field);
                else if (field.getType().getName().equals("double")) 
                    setDouble(prop, field);
                else if (field.getType().equals(String.class))
                    setString(prop, field);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static boolean hasValidProp(Properties prop, Field field) {
    	return prop.getProperty(field.getName()) != null
        	&& !prop.getProperty(field.getName()).trim().isEmpty();
    }
    
    private static String getProp(Properties prop, Field field) {
    	return prop.getProperty(field.getName()).trim();
    }

    private static void setInt(Properties prop, Field field) throws Exception {
        if (hasValidProp(prop, field))
            field.set(null, Integer.valueOf(getProp(prop, field)));
    }

    private static void setDouble(Properties prop, Field field) throws Exception {
        if (hasValidProp(prop, field)) {
            field.set(null, Double.valueOf(getProp(prop, field)));
        }
    }

    private static void setString(Properties prop, Field field) throws Exception {
        if (hasValidProp(prop, field)) {
            field.set(null, getProp(prop, field));
        }
    }

	public static void initializeExample() throws Exception {
		
		 TextReader inN = new TextReader(nodeFile);
	        TextReader inE = new TextReader(edgeFile);
	        String line;

	        System.out.print("Loading graph");
	        int count = 0;
	        while ((line = inN.readln()) != null) {
	            int id = Integer.parseInt(line);
	// for exact
	            nodesIndex[count]=id;
//	            System.out.println("Test node index----Graph:Line103-----Node id: "+ id+" index: "+count);

	            count++;
	            if (count % 1000000 == 0)
	                System.out.print(".");
	        }

	        while ((line = inE.readln()) != null) {
	            String[] split = line.split("\t");
	            int from = Integer.parseInt(split[0]);
	            int to = Integer.parseInt(split[1]);
	            
//	            count++;
//	            if (count % 1000000 == 0)
//	                System.out.print(".");
	        }
	        System.out.println();

	        inN.close();
	        inE.close();		
	}
    
  

    
}
