package org.example;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class ConexionMongo {
    private static ConexionMongo instance;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private ConexionMongo() {
        String connectionString = "mongodb+srv://chamo:chamo3133@clasesaed.4vyw6ij.mongodb.net/?appName=clasesAED";

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        this.mongoClient = MongoClients.create(settings);

        // Seleccionamos la base de datos (CAMBIA EL NOMBRE SI ES NECESARIO)
        this.database = mongoClient.getDatabase("Barrios");
    }

    public static ConexionMongo getInstance() {
        if (instance == null) {
            instance = new ConexionMongo();
        }
        return instance;
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}