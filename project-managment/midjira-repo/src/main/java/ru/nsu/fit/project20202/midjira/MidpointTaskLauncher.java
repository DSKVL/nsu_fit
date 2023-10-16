package ru.nsu.fit.project20202.midjira;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.RequestBodyEntity;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static ru.nsu.fit.project20202.midjira.JiraSurveyor.ACCEPT_HEADER_VALUE;

//api in midpoint /tasks/{oid}/run
//https://localhost:8080/ws/rest/reportData
public class MidpointTaskLauncher {
    @Value("${midpoint.oid}")
    String oid;
    String taskFilename;

    public void setUpReportTask() {
        try {
            HttpResponse<JsonNode> response = Unirest.get("https://localhost:8080/ws/rest/reports")
                    .basicAuth("Administrator", "5ecr3t")
                    .header("Accept", ACCEPT_HEADER_VALUE)
                    .asJson();



            RequestBodyEntity post = Unirest.post("https://localhost:8080/ws/rest/tasks")
                    .basicAuth("Administrator", "5ecr3t")
                    .header("Content-Type", "application/xml")
                    .body(new FileInputStream(taskFilename));


        } catch (FileNotFoundException e) {
            System.out.println("CannotFind");
        }
    }

    public void launchReportTask() {
        HttpResponse<JsonNode> response = Unirest.post("https://localhost:8080/ws/rest/tasks/" + oid + "/resume")
                .basicAuth("Administrator", "5ecr3t")
                .header("Accept", ACCEPT_HEADER_VALUE)
                .asJson();

    }
}
