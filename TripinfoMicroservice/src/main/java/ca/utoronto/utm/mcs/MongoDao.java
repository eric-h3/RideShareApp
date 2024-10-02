package ca.utoronto.utm.mcs;

import com.mongodb.client.*;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.bson.types.ObjectId;

import io.github.cdimascio.dotenv.Dotenv;

public class MongoDao {
	
	public MongoCollection<Document> collection;

	private final String dbName = "trip";

	public MongoDao() {
        // TODO: 
        // Connect to the mongodb database and create the database and collection. 
        // Use Dotenv like in the DAOs of the other microservices.
		Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("MONGODB_ADDR");
		String uriDb = "mongodb://root:123456@" + addr + ":27017";
		MongoClient mongoClient = MongoClients.create(uriDb);
        MongoDatabase database = mongoClient.getDatabase(this.dbName);
		this.collection = database.getCollection(this.dbName);
	}

	// *** implement database operations here *** //
	public boolean postConfirm(String driverUid, String passengerUid, int startTime) {
		Document doc = new Document();
		doc.put("driver", driverUid);
		doc.put("passenger", passengerUid);
		doc.put("startTime", startTime);

		try {
			this.collection.insertOne(doc);
			return true;
		} catch (Exception e) {

			return false;
		}
	}

	public boolean patchTrip(ObjectId id, int distance, int endTime, int timeElapsed, String totalCost) {

		Document doc = new Document();
		doc.put("distance", distance);
		doc.put("endTime", endTime);
		doc.put("timeElapsed", timeElapsed);
		doc.put("totalCost", totalCost);
		this.collection.updateOne(eq("_id", id), new Document("$set", doc));
		
		return true;
	}

	public Document findbyId(String id) {
        try {
			ObjectId _id = new ObjectId(id);
            return this.collection.find(eq("_id", _id)).first();
        } catch (Exception e) {
            System.out.println("Error occurred");
        }
        return null;
    }

	public Document getDriver(String driverUid) {
        try {
            return this.collection.find(eq("driver", driverUid)).first();
        } catch (Exception e) {
            System.out.println("Error occurred");
        }
        return null;
    }


	public FindIterable<Document> getPassengerTrips(String passengerUid) {
		try {
            return this.collection.find(eq("passenger", passengerUid));
        } catch (Exception e) {
            System.out.println("Error occurred");
        }
        return null;
	}

	public Document getPassenger(String passengerUid) {
        try {
            return this.collection.find(eq("passenger", passengerUid)).first();
        } catch (Exception e) {
            System.out.println("Error occurred");
        }
        return null;
    }

	public FindIterable<Document> getDriverTrips(String driverUid) {
		try {
            return this.collection.find(eq("driver", driverUid));
        } catch (Exception e) {
            System.out.println("Error occurred");
        }
        return null;
	}

}
