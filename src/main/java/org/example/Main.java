package org.example;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        // 1. Obtenemos la instancia de la base de datos usando tu clase ConexionMongo
        MongoDatabase database = ConexionMongo.getInstance().getDatabase();

        // 2. Obtenemos la colección.
        // ¡IMPORTANTE!: Asegúrate de que en Atlas la colección se llame "barrios"
        MongoCollection<Document> collection = database.getCollection("barrios");

        System.out.println("Conexión establecida a la base de datos: " + database.getName());

        System.out.println("\n--- 1. Máximo, mínimo y media de población ---");
        ejercicio1(collection);

        System.out.println("\n--- 2. Población total por barrio ---");
        ejercicio2(collection);

        System.out.println("\n--- 3. Los 5 barrios más pequeños por área ---");
        ejercicio3(collection);

        System.out.println("\n--- 4. Cuántos barrios hay por distrito (Count con Sum) ---");
        ejercicio4(collection);

        System.out.println("\n--- 5. Barrio de Ofra con más mujeres ---");
        ejercicio5(collection);
    }

    private static void ejercicio1(MongoCollection<Document> collection) {
        Bson groupStage = Aggregates.group(null,
                Accumulators.max("maximo_poblacion", "$TOTAL"),
                Accumulators.min("minimo_poblacion", "$TOTAL"),
                Accumulators.avg("media_poblacion", "$TOTAL")
        );

        collection.aggregate(Arrays.asList(groupStage))
                .forEach(doc -> System.out.println(doc.toJson()));
    }
    private static void ejercicio2(MongoCollection<Document> collection) {
        Bson projectStage = Aggregates.project(
                Projections.fields(
                        Projections.excludeId(),
                        Projections.include("BARRIO", "TOTAL")
                )
        );

        collection.aggregate(Arrays.asList(projectStage))
                .forEach(doc -> System.out.println(doc.toJson()));
    }

    private static void ejercicio3(MongoCollection<Document> collection) {
        Bson sortStage = Aggregates.sort(Sorts.ascending("AREA"));
        Bson limitStage = Aggregates.limit(5);
        Bson projectStage = Aggregates.project(
                Projections.fields(
                        Projections.excludeId(),
                        Projections.include("BARRIO", "AREA")
                )
        );

        // Se pasan las etapas en orden dentro de una lista
        collection.aggregate(Arrays.asList(sortStage, limitStage, projectStage))
                .forEach(doc -> System.out.println(doc.toJson()));
    }

    private static void ejercicio4(MongoCollection<Document> collection) {
        Bson groupStage = Aggregates.group("$DISTRITO",
                Accumulators.sum("cantidad_barrios", 1)
        );

        collection.aggregate(Arrays.asList(groupStage))
                .forEach(doc -> System.out.println(doc.toJson()));
    }

    private static void ejercicio5(MongoCollection<Document> collection) {
        Bson matchStage = Aggregates.match(Filters.eq("DISTRITO", "OFRA - COSTA SUR"));
        Bson sortStage = Aggregates.sort(Sorts.descending("MUJERES"));
        Bson limitStage = Aggregates.limit(1);

        // Etapa extra para limpiar la salida y que se vea solo el barrio y el nº de mujeres
        Bson projectStage = Aggregates.project(
                Projections.fields(
                        Projections.excludeId(),
                        Projections.include("BARRIO", "MUJERES")
                )
        );

        collection.aggregate(Arrays.asList(matchStage, sortStage, limitStage, projectStage))
                .forEach(doc -> System.out.println(doc.toJson()));
    }
}
