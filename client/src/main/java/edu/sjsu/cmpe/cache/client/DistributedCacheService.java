package edu.sjsu.cmpe.cache.client;

import java.util.concurrent.Future;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Distributed cache service
 * 
 */
public class DistributedCacheService implements CacheServiceInterface {

	
    public final String cacheSerUrl;
    public  CrdtClient CrdtClient;
    public DistributedCacheService(String serverUrl,CrdtClient client) {
        this.cacheSerUrl = serverUrl;
        this.CrdtClient=client;
    }
    
	public String get(long key) {
		// TODO Auto-generated method stub
		Future<HttpResponse<JsonNode>> future = Unirest.get(this.cacheSerUrl + "/cache/{key}")
                .header("accept", "application/json")
                .routeParam("key", Long.toString(key))
                .asJsonAsync(new Callback<JsonNode>() {

                    public void failed(UnirestException e) {
                    	CrdtClient.getFailed(cacheSerUrl);
                    	
                    }

                    public void completed(HttpResponse<JsonNode> response) {
                    	CrdtClient.getResultsForServers(response, cacheSerUrl);
                    }

                    public void cancelled() {
                        System.out.println("Request cancelled");
                    }

                });

        return null;
	}

	public void put(long key, String value) {
		// TODO Auto-generated method stub
		Future<HttpResponse<JsonNode>> future = Unirest.put(this.cacheSerUrl + "/cache/{key}/{value}")
                .header("accept", "application/json")
                .routeParam("key", Long.toString(key))
                .routeParam("value", value)
                .asJsonAsync(new Callback<JsonNode>(){

					public void completed(HttpResponse<JsonNode> response) 
					{
						
						System.out.println("value put on Server"+cacheSerUrl);
						CrdtClient.putForSuccess(cacheSerUrl);
										
					}

					public void failed(
							UnirestException e) {
						// TODO Auto-generated method stub
						CrdtClient.failed(cacheSerUrl);

						
					}

					public void cancelled() {
						// TODO Auto-generated method stub
						
					}
                	
                });


		
	}

	public void delete(long key) {
		// TODO Auto-generated method stub
		 HttpResponse<JsonNode> response = null;
    	 try{
    		 
    		 response = Unirest
                     .delete(this.cacheSerUrl + "/cache/{key}")
                     .header("accept", "application/json")
                     .routeParam("key", Long.toString(key))
                     .asJson();
         } catch (UnirestException e) {
             System.err.println(e);
         }
    	 
    	 System.out.println("response is " + response);

         if (response == null || response.getCode() != 204) {
             System.out.println("Failed to delete from cache.");
         } else {
             System.out.println("Deleted " + key + " from " + this.cacheSerUrl);
         }
		
	}

}
