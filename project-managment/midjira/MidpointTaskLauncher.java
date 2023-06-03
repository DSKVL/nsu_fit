package ru.nsu.fit.project20202.midjira;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;


public class MidpointTaskLauncher {
    @Autowired
    private Environment env;

    String taskFilename;
    private final static String ACCEPT_HEADER_VALUE = "application/xml";

    public void setUpReportTask() {
//        try {
//            HttpResponse<JsonNode> response = Unirest.get("https://localhost:"
//                    + env.g
//                    + "/ws/rest/reports")
//                    .basicAuth("Administrator", "5ecr3t")
//                    .header("Accept", ACCEPT_HEADER_VALUE)
//                    .asJson();
//
//            RequestBodyEntity post = Unirest.post("https://localhost:8080/ws/rest/tasks")
//                    .basicAuth("Administrator", "5ecr3t")
//                    .header("Content-Type", ACCEPT_HEADER_VALUE)
//                    .body(new FileInputStream(taskFilename));
//        } catch (FileNotFoundException e) {
//            System.out.println("CannotFind");
//        }
    }

    public void launchReportTask() {
        HttpResponse<JsonNode> response = Unirest.post("https://localhost:8080/ws/rest/tasks/" + "adasda" + "/resume")
                .basicAuth("Administrator", "5ecr3t")
                .header("Accept", ACCEPT_HEADER_VALUE)
                .asJson();

    }
}
