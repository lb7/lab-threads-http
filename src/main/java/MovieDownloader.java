import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * A class for downloading movie data from the internet.
 * Code adapted from Google.
 *
 * YOUR TASK: Add comments explaining how this code works!
 * 
 * @author Joel Ross & Kyungmin Lee
 */
public class MovieDownloader {

	public static String[] downloadMovieData(String movie) {

		//construct the url for the omdbapi API
		String urlString = "";
		try {
			urlString = "http://www.omdbapi.com/?s=" + URLEncoder.encode(movie, "UTF-8") + "&type=movie";
		}catch(UnsupportedEncodingException uee){
			return null;
		}

		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;

		String[] movies = null;

		try {
			//Make a new URL with the API rul
			URL url = new URL(urlString);

			//Open a connection and connect with the GET HTTP method
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();

			//Get the stream from the connection so we can read the input
			InputStream inputStream = urlConnection.getInputStream();
			StringBuffer buffer = new StringBuffer(); //Create buffer to read lines into
			if (inputStream == null) { //Stop if there is no input stream
				return null;
			}
			//Set up reader so we can read from the connection
			reader = new BufferedReader(new InputStreamReader(inputStream));

			String line = reader.readLine(); //Read a line received from the API
            while (line != null) { //Keep reading until there are no more lines
				buffer.append(line + "\n"); //Stick the line to the end of the buffer
				line = reader.readLine(); //Read the next line
			}

			if (buffer.length() == 0) { //Stop if there is nothing in the buffer
				return null;
			}
			String results = buffer.toString(); //Get the full string from the buffer

            //Edit the result so it's more readable
			results = results.replace("{\"Search\":[","");
			results = results.replace("]}","");
			results = results.replace("},", "},\n");

			//Split the result into one string for each line
			movies = results.split("\n");
		} 
		catch (IOException e) { //Stop if there is an IOException
			return null;
		} 
		finally {
			if (urlConnection != null) { //Disconnect the connection if it exists
				urlConnection.disconnect();
			}
			if (reader != null) { //Close the reader if it exists
				try {
					reader.close();
				} 
				catch (IOException e) {
				}
			}
		}

		return movies;
	}


	public static void main(String[] args) 
	{
		Scanner sc = new Scanner(System.in);

		boolean searching = true;

		while(searching) {					
			System.out.print("Enter a movie name to search for or type 'q' to quit: ");
			String searchTerm = sc.nextLine().trim();
			if(searchTerm.toLowerCase().equals("q")){
				searching = false;
			}
			else {
				String[] movies = downloadMovieData(searchTerm);
				for(String movie : movies) {
					System.out.println(movie);
				}
			}
		}
		sc.close();
	}
}
