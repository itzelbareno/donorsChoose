import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class Main {

	public static void main(String[] args){
		if (args.length>0 && (args[0].equals("-h") || args[0].equals("--help"))){
			help(args);
		}
		else{
			try {
				JSONParser parse = new JSONParser();
				JSONObject jobj = (JSONObject)parse.parse(getResponse(args));
				JSONArray jarray = (JSONArray)jobj.get("proposals");
				
				float percentFunded = 0f, numDonors = 0f,costToComplete = 0f, numStudents = 0f, totalPrice  = 0f;
				int resultsSize = jarray.size();
				
				for(int i=0;i<resultsSize;i++)
				{
					JSONObject jsonobj_1 = (JSONObject)jarray.get(i);
					System.out.println("Title: " +jsonobj_1.get("title"));
					System.out.println("Short Description: " +jsonobj_1.get("shortDescription"));
					System.out.println("Proposal Url: " +jsonobj_1.get("proposalURL"));
					System.out.println("Cost to Complete: $" +jsonobj_1.get("costToComplete"));
					System.out.println();
					
					percentFunded += Float.parseFloat(jsonobj_1.get("percentFunded").toString());
					numDonors += Float.parseFloat(jsonobj_1.get("numDonors").toString());
					costToComplete += Float.parseFloat(jsonobj_1.get("costToComplete").toString());
					numStudents += Float.parseFloat(jsonobj_1.get("numStudents").toString());
					totalPrice += Float.parseFloat(jsonobj_1.get("totalPrice").toString());
				}
				
				if(resultsSize==0){
					System.out.println("No results found. Please review your query for any conflicts.");
				}
				else{
					System.out.println("AVERAGED TOTALS:");
					System.out.println("Average % Funded:\t"+(percentFunded/resultsSize));
					System.out.println("Average # of Donors:\t"+(numDonors/resultsSize));
					System.out.println("Average Cost To Complete ($):\t"+(costToComplete/resultsSize));
					System.out.println("Average # Students:\t"+(numStudents/resultsSize));
					System.out.println("Average Total Price ($):\t"+(totalPrice/resultsSize));
				}
			}catch (ClientProtocolException e){
				System.out.println("Error Message: "+e.getMessage());
				System.out.println("Error Cause: "+e.getCause());
			} catch (IOException e) {
				System.out.println("Error Message: "+e.getMessage());
				System.out.println("Error Cause: "+e.getCause());
			} catch (ParseException e) {
				System.out.println("Error Message: "+e.getMessage());
				System.out.println("Error Cause: "+e.getCause());
			} catch (URISyntaxException e) {
				System.out.println("Error Message: "+e.getMessage());
				System.out.println("Error Cause: "+e.getCause());
			} finally {
				System.exit(1);
			}
		}
		
	}
	
	public static String getResponse(String[] args) throws URISyntaxException, UnsupportedOperationException, IOException{
		CloseableHttpClient httpclient = HttpClients.createDefault();
		URI uri = getURI(args);
		HttpGet httpget = new HttpGet(uri);
		CloseableHttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		String inline = "";
	    
		if (entity != null) {
	    	Scanner sc = new Scanner(entity.getContent());
	    	while(sc.hasNext())
				inline+=sc.nextLine();
			sc.close();
	    }
	    
		try {
			response.close();
		} catch (IOException e) {
			System.exit(1);
		} catch (NullPointerException e){
			System.exit(1);
		}
		
		return inline;
		
	}
	
	public static URI getURI(String[] args) throws URISyntaxException, ClientProtocolException{
		int i = 0;

		URIBuilder uriBuilder =  new URIBuilder()
		.setScheme("https")
		.setHost("api.donorschoose.org")
		.setPath("/common/json_feed.html")
		.setParameter("state", "CA")
        .setParameter("costToCompleteRange", "0 TO 2000")
        .setParameter("index","0")
        .setParameter("max", "5")
        .setParameter("sortBy","0");

		if(args.length>0 && (args[0].equals("-q") || args[0].equals("--query")))
			args = args[1].split("(&)|(=)");
		else if(args.length>0 && (args[0].equals("-s") || args[0].equals("--search")))
			i = 1;
		else if(args.length>0){
			System.out.println("Unknown argument "+args[0]+". Please consult --help.");
			System.exit(1);
			return null;
		}
		
		while(i<args.length){
			if(i+1 < args.length){
				uriBuilder.setParameter(args[i],args[++i]); 
				i++;
			}
			else {
				System.out.println("Not enough arguments. Please consult --help.");
				System.exit(1);
				return null;
			}
		}

		uriBuilder.setParameter("APIKey", "DONORSCHOOSE");

		return uriBuilder.build();
	}
	
	public static void help(String[] args){
		switch(args.length){
		case 1:
			printSearch();
			printQuery();
			break;
		
		case 2:
			if(args[1].equals("-q") || args[1].equals("--query"))
				printQuery();
			else if (args[1].equals("-s") || args[1].equals("--search")){
				printSearch();
			}
			else{
				System.out.println("Unknown command "+args[1]+".");
				printHelp();
			}
			break;
		}
		
	}
	
	public static void printHelp(){
		System.out.println("-h, --help Use -h, --help alone to get all commands or use it followed by the following arguments to get related information:");
		System.out.println("\t-s, --search Get definition and syntax to perform a search by specific parameters.");
		System.out.println("\t-q, --query Get definition and syntax to perform a search by using a full url query.");
	}
	
	public static void printSearch(){
		System.out.println("-s, --search");	
		System.out.println("\tDefinition:\tSearches depending on the parameter.");
		System.out.println("\tExample:\t-s paramName1 paramVal1 paramName2 paramVal2 ...");
		System.out.println("Go to https://data.donorschoose.org/docs/overview/ to get the parameters' names and syntax.");
	}
	
	public static void printQuery(){
		System.out.println("-q, --query");	
		System.out.println("\tDefinition:\tAdds the query to the URL. For multiple parameters, enclose in \"\"");
		System.out.println("\tExample:\t-q paramName=\"paramValue&paramName2=paramVal2&...\"");
		System.out.println("Go to https://data.donorschoose.org/docs/overview/ to get the parameters' names and syntax.");
	}

}
