package ru.nsu.fit.project20202.midjira;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.io.File;
import java.util.Optional;
import java.util.Timer;

public class JiraReportAttacher {
    @Autowired
    private Environment env;
    @Autowired
    private JiraSurveyor jiraSurveyor;

    public static final String ACCEPT_HEADER_VALUE = "application/json";


    public void init() {
        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                var report = getFinishedReport();
//                report.ifPresent(file -> {
//                    attachFinishedReport(file);
//                    markTaskFinished();
//                });
//
//            }
//        }, 0L, 1000L * 60L * 30L);
    }


    private String getTaskReportPath(JsonObject task) {
        var extension = task.get("extension").getAsJsonObject();
        var reportDataParam = extension.get("reportDataParam").getAsJsonObject();
        var oid = reportDataParam.get("oid").getAsString();

        HttpResponse<JsonNode> response = Unirest.get("https:/localhost:"
                        + env.getProperty("midpoint.port")
                        + "/midpoint/ws/rest/reportData/"
                        + oid)
                .basicAuth(env.getProperty("midpoint.username"), env.getProperty("midpoint.password"))
                .header("Accept", ACCEPT_HEADER_VALUE)
                .asJson();
        var body = response.getBody().getObject();
        var reportData = (JsonObject) body.get("reportData");
        return reportData.get("filePath").getAsString();
    }

    private Optional<File> getFinishedReport() {
        HttpResponse<JsonNode> response = Unirest.get("https:/localhost:"
                        + env.getProperty("midpoint.port")
                        + "/midpoint/ws/rest/tasks/"
                        + env.getProperty("midpoint.oid"))
                .basicAuth(env.getProperty("midpoint.username"), env.getProperty("midpoint.password"))
                .header("Accept", ACCEPT_HEADER_VALUE)
                .asJson();
        var body = response.getBody().getObject();
        var task = (JsonObject) body.get("task");
        var operationExecution = (JsonArray) task.get("operationExecution");
        var lastStatusChange = operationExecution.get(operationExecution.size()-1).getAsJsonObject();
        var status = lastStatusChange.get("status").getAsString();
        if (status.equals("success"))
            return Optional.of(new File(getTaskReportPath(task)));
        return Optional.empty();
    }
    private void attachFinishedReport(File report) {
        HttpResponse<JsonNode> response = Unirest.post("https:/localhost:"
                        + env.getProperty("jira.port")
                        + "/rest/api/3/issue/"
                        + jiraSurveyor.getIssueId()
                        + "/attachments")
                .basicAuth(env.getProperty("jira.username"), env.getProperty("jira.password"))
                .header("Accept", ACCEPT_HEADER_VALUE)
                .header("X-Atlassian-Token", "no-check")
                .field("file", report)
                .asJson();
    }
    private void markTaskFinished() {
        JsonNodeFactory jnf = JsonNodeFactory.instance;
        ObjectNode payload = jnf.objectNode();

        ObjectNode fields = payload.putObject("fields");
        ObjectNode assignee = fields.putObject("assignee");

        assignee.put("name", env.getProperty("jira.username"));

        ObjectNode resolution = fields.putObject("resolution");
        resolution.put("name", "Finished");

        ObjectNode transition = payload.putObject("transition");
        transition.put("id", "2");

        HttpResponse<JsonNode> response = Unirest.post("https://localhost:"
                        + env.getProperty("jira.port")
                        +"/rest/api/3/issue/"
                        + jiraSurveyor.getIssueId()
                        + "/transitions")
                .basicAuth(env.getProperty("jira.username"), env.getProperty("jira.password"))
                .header("Accept", ACCEPT_HEADER_VALUE)
                .header("Content-Type", ACCEPT_HEADER_VALUE)
                .body(payload)
                .asJson();
        //TODO check response
    }
}
