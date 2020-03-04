package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

public class RpcHelper {
	// Writes a JSONArray to http response.
	public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException{
		response.setContentType("application/json");
		response.getWriter().print(array);

	}

              // Writes a JSONObject to http response.
	public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException {	
		response.setContentType("application/json");
		response.getWriter().print(obj);
	}
	
	// Parse a JSONObject from http request
	public static JSONObject readJSONObject(HttpServletRequest request) throws IOException {
		BufferedReader reader = new BufferedReader(request.getReader());
		StringBuilder requestBody = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			requestBody.append(line);
		}
		
		return new JSONObject(requestBody.toString());
	}
	
	 // Convert a JSON object to Item object
	public static Item parseFavoriteItem(JSONObject favoriteItem) {
		ItemBuilder builder = new ItemBuilder();
		builder.setItemId(favoriteItem.getString("item_id"));
		builder.setName(favoriteItem.optString("name"));
		builder.setAddress(favoriteItem.optString("address"));
		builder.setUrl(favoriteItem.optString("url"));
		builder.setImageUrl(favoriteItem.optString("image_url"));
		
		Set<String> keywords = new HashSet<>();
		JSONArray array = favoriteItem.optJSONArray("keywords");
		if (array != null) {
			for (int i = 0; i < array.length(); ++i) {
				keywords.add(array.getString(i));
			}
		}
		builder.setKeywords(keywords);
		return builder.build();
	}


}
