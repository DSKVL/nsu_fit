package ru.nsu.fit.dskvl.places;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.Scanner;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.Function2;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Iterator;
import io.vavr.collection.Seq;
import io.vavr.concurrent.Future;
import io.vavr.collection.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import io.vavr.jackson.datatype.VavrModule;

record Point (double lng, double lat) {}

public class Main {
    static ObjectMapper mapper = new ObjectMapper().registerModule(new VavrModule());
    static HttpClient client = HttpClient.newHttpClient();

    static List<Tuple2<String, Point>> parseGeoCode(HttpResponse<String> response) throws IOException {
        return List.of(mapper.readTree(response.body()))
                .flatMap(jsonNode -> jsonNode.get("hits"))
                .map(x -> Tuple.of(x.get("name").toString(),
                        new Point(x.get("point").get("lng").asDouble(), x.geXt("point").get("lat").asDouble())));
    }

    static String ppWether(JsonNode owmResponse) {
        var coord = new Point(owmResponse.get("coord").get("lon").asDouble(),
                owmResponse.get("coord").get("lat").asDouble());

        return new StringBuilder("The weather at ")
                .append(coord.lat()).append(" ")
                .append(coord.lng()).append(" ")
                .append("is ").append(owmResponse.get("weather").get(0).get("description").asText()).append(".\n")
                .append("Temperature is ").append(owmResponse.get("main").get("temp").asText()).append("\n").toString();
    }

    static String parseWhether(HttpResponse<String> response) throws IOException {
        return ppWether(mapper.readTree(response.body()));
    }

    static List<String> parseSights(HttpResponse<String> response) throws IOException {
        var json = mapper.readTree(response.body()).get("features");
        return Iterator.ofAll(json.elements()).toList()
                .map(x->x.get("properties").get("xid").asText());
    }

    static String parseDescription(HttpResponse<String> response) throws IOException {
        var json = mapper.readTree(response.body());
        if (200 == response.statusCode()) {
            var name = json.get("name").asText();
            var desc = (json.has("info")) ? json.get("info").get("descr").asText() : "";
            return new StringBuilder(name).append("\n").append(desc).append("\n\n").toString();
        } else {
            return json.get("error").asText();
        }
    }

    static Future<List<Tuple2<String, Point>>> getCandidatePlaces(String place) {
        var httpRequestTry = Try.of(() -> HttpRequest.newBuilder()
                .uri(new URI("https://graphhopper.com/api/1/geocode?q="
                        + place.toLowerCase()
                        + "&locale=en&key=af036deb-6a1e-48e4-8746-f0362481cb05"))
                .GET()
                .build());

        return Future.fromTry(
                        httpRequestTry.map(request -> Future.fromCompletableFuture(
                                client.sendAsync(request, HttpResponse.BodyHandlers.ofString()))))
                .flatMap(Function.identity())
                .mapTry(Main::parseGeoCode);
    }

    static Future<String> getWhetherAt(Point point) {
        var httpRequestTry = Try.of(() -> HttpRequest.newBuilder()
                .uri(new URI("https://api.openweathermap.org/data/2.5/weather?lat=" + point.lat()
                        + "&lon=" + point.lng() + "&appid=1ffc3e8eb47bd4a2f021cc30df229575"))
                .GET()
                .build());

        return Future.fromTry(
                        httpRequestTry.map(request -> Future.fromCompletableFuture(
                                client.sendAsync(request, HttpResponse.BodyHandlers.ofString()))))
                .flatMap(Function.identity())
                .mapTry(Main::parseWhether);
    }

    static Function2 <Integer, Point, Future<List<String>>> getSightsAt = (Integer radius, Point point) -> {
        var httpRequestTry = Try.of(() -> HttpRequest.newBuilder()
                .uri(new URI("https://api.opentripmap.com/0.1/ru/places/radius?radius="
                        + radius
                        + "&lon=" + point.lng()
                        + "&lat=" + point.lat()
                        + "&apikey=5ae2e3f221c38a28845f05b612ae789d904bb594934537f58e3fb003"))
                .GET()
                .build());

        return Future.fromTry(
                        httpRequestTry.map(request -> Future.fromCompletableFuture(
                                client.sendAsync(request, HttpResponse.BodyHandlers.ofString()))))
                .flatMap(Function.identity())
                .mapTry(Main::parseSights);
    };

    static Future<Seq<String>> getSightsDescriptions(List<String> sightsList) {
        return Future.sequence(sightsList.map(Main::getDescription));
    }

    static Future<String> getDescription(String xid) {
        var httpRequestTry = Try.of(() -> HttpRequest.newBuilder()
                .uri(new URI("https://api.opentripmap.com/0.1/ru/places/xid/"
                        + xid
                        + "?apikey=5ae2e3f221c38a28845f05b612ae789d904bb594934537f58e3fb003"))
                .GET()
                .build());

        return Future.fromTry(
                        httpRequestTry.map(request -> Future.fromCompletableFuture(
                                client.sendAsync(request, HttpResponse.BodyHandlers.ofString()))))
                .flatMap(Function.identity())
                .mapTry(Main::parseDescription);
    }

    public static void main(String[] args) {
        var places = getCandidatePlaces(String.join("_", args));

        places.onComplete(listTry -> {
            System.out.println("Specify place:");
            listTry.onSuccess(list -> list.forEachWithIndex((y, index) -> {
                System.out.println(index + " " + y._1() + " at");
                System.out.println(y._2().lng() + " " + y._2.lat() + "\n");
            })).onFailure(e -> System.out.println("Unable to get response from geocode: " + e));
        }).onFailure(e -> System.out.println("Failed to get info about place " + e));

        var coordinates = places.map(placesList -> {
            var sc = new Scanner(System.in);
            var index = sc.nextInt();

            while (index >= placesList.length()) {
                System.out.println("Please select one place from the list.");
                index = sc.nextInt();
            }
            return placesList.get(index)._2();
        });

        var weather = coordinates.flatMap(Main::getWhetherAt)
                .onSuccess(System.out::println)
                .onFailure(e -> System.out.println("Unable to get weather " + e));

        coordinates.flatMap(getSightsAt.apply(500))
                .onFailure(e -> System.out.println("Unable to get information about sights " + e))
                .flatMap(Main::getSightsDescriptions)
                .onFailure(e -> System.out.println("Unable to get sights descriptions " + e))
                .get()
                .forEach(System.out::println);

        System.out.println(weather.get());
    }
}