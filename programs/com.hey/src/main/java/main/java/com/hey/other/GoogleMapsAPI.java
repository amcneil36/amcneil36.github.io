package main.java.com.hey.other;

import java.util.Scanner;

import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.RankBy;

public class GoogleMapsAPI {

	static class NearbySearchRequestSub extends NearbySearchRequest {

		public NearbySearchRequestSub(GeoApiContext context) {
			super(context);
		}
		@Override
		public NearbySearchRequest radius(int distance) {
			return param("radius", String.valueOf(distance));
		}

	}

	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in); // System.in is a standard input stream
		System.out.print("Enter API Key: ");
		String key = sc.nextLine(); // reads string
		GeoApiContext.Builder builder = new GeoApiContext.Builder();
		builder.apiKey(key);
		GeoApiContext context = builder.build();
		builder.queryRateLimit(10);
		LatLng weston = new LatLng(26.10061, -80.4053835);
	    NearbySearchRequest request = new NearbySearchRequestSub(context);
	    request.location(weston);
		NearbySearchRequest nearbySearchRequest = request.radius(225000).type(PlaceType.RESTAURANT);
		PlacesSearchResponse req = nearbySearchRequest.await();
		PlacesSearchResult[] foo = req.results;
		for (PlacesSearchResult st : foo) {
			System.out.println(st.name);
		}
	}

}
