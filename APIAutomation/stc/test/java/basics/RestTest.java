package basics;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bsh.ParseException;
import static io.restassured.RestAssured.given;

public class RestTest {

    public static Response doGetRequest(String endpoint) {
        RestAssured.defaultParser = Parser.JSON;

        return
            given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
                when().get(endpoint).
                then().contentType(ContentType.JSON).extract().response();
    }
    
    public static void getStatus(Response resp) 
    {
    	if(resp.getStatusCode() == 200){
    		Log("Status Code -> Test Passed, Request Successful");
    	}
    }
    
    public static Response getResponse(){
    	Response response = doGetRequest("https://apiproxy.paytm.com/v2/movies/upcoming");
    	return response;
    }
    
    public static void checkPoster(Response resp){
    	int size = getSize(resp);
    	int count = 0;
    	for(int i=0;i<size;i++){
    		Map<String, String> movies = resp.jsonPath().getMap("upcomingMovieData["+i+"]");
        	String url = movies.get("moviePosterUrl");
        	String img_format = url.substring(url.lastIndexOf('-') + 1).split("\\.")[1];
        	if(!(img_format.equals("jpg"))){
        		Log("The movie "+ movies.get("provider_moviename")
        				+ " has the wrong image format");
        		count++;
        	}
    	}
    	if(count>0){
    		Log("Movie Poster URL -> Test Failed, The above movie has Wrong image format");
    	}
    	else{
    		Log("Movie Poster URL -> Test Passed. All the Movies have JPEG image format");
    	}
    }
    
    public static void checkUniqueMovieCode(Response resp){
    	int size = getSize(resp);
    	int count = 0;
    	for(int i=0;i<size;i++){
    		Map<String, String> movies = resp.jsonPath().getMap("upcomingMovieData["+i+"]");
        	String promo_code = movies.get("paytmMovieCode");
        	Set<String> code_set = new HashSet<String>();
        	if(!(code_set.add(promo_code))){
        		Log("The code "+promo_code+ " for the movie "
        	+ movies.get("provider_moviename")+ "is not unique");
        		count++;
        	}
    	}
    	if(count>0){
    		Log("Unique Promo Code Test -> Test Failed, Promo codes are not Unique");
    	}
    	else{
    		Log("Unique Promo Code Test -> Test Passed. All Promo codes are Unique");
    	}
    }
    
    public static void languageFormat(Response resp){
    	int size = getSize(resp);
    	int count = 0;
    	int j=0;
    	for(int i=0;i<size;i++){
    		Map<String, String> movies = resp.jsonPath().getMap("upcomingMovieData["+i+"]");
    		String lang_format = movies.get("language");
    		while(j<lang_format.length()){
    			if (lang_format.charAt(i) == ' ' || lang_format.charAt(i) == '\n' 
                        || lang_format.charAt(i) == '\t' || lang_format.charAt(i) == ','){
    				count++;
    				Log("Language Format Test -> Test Failed "
                    + movies.get("provider_moviename")+" Movie has multiple languages");
    			}
    			j++;
    		}
    	}
    	if(count>0){
    		Log("Language Format Test -> Test Failed , The above movies has multiple languages");
    	}
    	else{
    		Log("Language Format Test -> Test Passed, All Movies have Single language");
    	}
    }
    
    public static void movieReleaseDate(Response resp) throws java.text.ParseException{
    	int size = getSize(resp);
    	int count = 0;
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
    	Calendar calendar = Calendar.getInstance();
        long timeMilli2 = calendar.getTimeInMillis();
    	for(int i=0;i<size;i++){
    		Map<String, String> movies = resp.jsonPath().getMap("upcomingMovieData["+i+"]");
    		String movie_rel_date = movies.get("releaseDate");
    		if(movie_rel_date != null && !movie_rel_date.isEmpty()){
    			Date date = formatter.parse(movie_rel_date);
    			if(date.getTime()<timeMilli2){
    			}
    			else{
    				Log(movies.get("provider_moviename").toUpperCase()+" has Past Date");
    				count++;
    			}
    		}
    		else{
    			Log(movies.get("provider_moviename").toUpperCase()+
    					" Coming Soon..!!");
    		}
    	}
    	if(count>0){
    		Log("Movie Release Date Test -> Test Failed , The above movies has past dates or No dates");
    	}
    	else{
    		Log("Movie Release Date Test -> Test Passed, All Movies have correct Dates");
    	}
    }
    
    public static void checkContentAvailable(Response resp) throws IOException{
    	int size = getSize(resp);
    	String fname = "output"+"_"+java.time.LocalDate.now()+".xls";
		File fout = new File(fname);
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
    	for(int i=0;i<size;i++){
    		Map<String, String> movies = resp.jsonPath().getMap("upcomingMovieData["+i+"]");
    		String con_ava = String.valueOf((movies.get("isContentAvailable")));
    		if(con_ava.equals("1")){
    			bw.write(movies.get("provider_moviename"));
    			bw.newLine();
    		}
    	}
    	bw.close();
    	Log("We have Content Available set to 1 for certain Movies, Updating them in  XLS File => "+fname);
    }
    
    //wrapper for Logging
    public static void Log(String S){
    	System.out.println(S);
    }
    
    public static int getSize(Response response){
    	List<String> jsonResponse = response.jsonPath().getList("upcomingMovieData");
        return jsonResponse.size();
    }
    
    public static void main(String[] args) {
        Response response = getResponse();
        getStatus(response);
        checkPoster(response);
        checkUniqueMovieCode(response);
        languageFormat(response);
        try {
			movieReleaseDate(response);
		} catch (java.text.ParseException e) {
	    	e.printStackTrace();
		}
        try {
			checkContentAvailable(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}