package ru.nsu.fit.project20202.midjira;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

@PropertySource("classpath:parameters.properties")
public class JiraSurveyor {
    @Autowired
    private Environment env;
    private String issueId;

    public String getIssueId() {
        return issueId;
    }

    @Autowired
    private MidpointTaskLauncher taskLauncher;
    public static final String ACCEPT_HEADER_VALUE = "application/json";

    public void init() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                doJiraSurvey();
            }
        }, 0L, 1000L * 60L * 30L);
    }

    private JSONArray requestIssuesList() {
        String url = "https://" + env.getProperty("jira.url") + "/rest/api/3/search";
        HttpResponse<JsonNode> response = Unirest.get(url)
                .basicAuth( env.getProperty("jira.username"),  env.getProperty("jira.password"))
                .header("Accept", ACCEPT_HEADER_VALUE)
                .queryString("jql", "issuetype = " + env.getProperty("jira.issuetype"))
                .queryString("expand", "transitions")
                .asJson();

        return (JSONArray) response.getBody().getObject().get("issues");
    }

    enum IssueStatus {
        OPEN, IN_PROGRESS, CLOSED
    }

    private IssueStatus getIssueStatus(JSONObject issue) {
        JSONArray transitions =  issue.getJSONArray("transitions");
        boolean isInProgress = false;
        boolean isClosed = false;
        for (var transition : transitions) {
            var status = ((JSONObject) transition).getString("name");
            isInProgress |= status.equals("In Progress");
            isClosed |= status.equals("InProgress");
        }
        if (isClosed)
            return IssueStatus.CLOSED;
        else if (isInProgress) {
            return IssueStatus.IN_PROGRESS;
        } else {
            return IssueStatus.OPEN;
        }
    }

    private Optional<JSONObject> findOpenIssue(JSONArray issues) {
        for (var issueElement : issues)
            if (IssueStatus.OPEN == getIssueStatus((JSONObject) issueElement))
                return Optional.of((JSONObject) issueElement);

        return Optional.empty();
    }

    private void markIssueInProgress(JSONObject issue) {
        JsonNodeFactory jnf = JsonNodeFactory.instance;
        ObjectNode payload = jnf.objectNode();

        ObjectNode fields = payload.putObject("fields");
        ObjectNode assignee = fields.putObject("assignee");

        assignee.put("name", env.getProperty("jira.username"));

        ObjectNode resolution = fields.putObject("resolution");
        resolution.put("name", "in Progress");

        ObjectNode transition = payload.putObject("transition");
        transition.put("id", "1");

        HttpResponse<JsonNode> response = Unirest.post("https://localhost:"
                        + env.getProperty("jira.port")
                        +"/rest/api/3/issue/"
                        + issue.getString("id")
                        + "/transitions")
                .basicAuth(env.getProperty("jira.username"), env.getProperty("jira.password"))
                .header("Accept", ACCEPT_HEADER_VALUE)
                .header("Content-Type", ACCEPT_HEADER_VALUE)
                .body(payload)
                .asJson();
        //TODO check responses
    }

    private void doJiraSurvey() {
        var issuesList = requestIssuesList();
        var openReportIssue = findOpenIssue(issuesList);
        openReportIssue.ifPresent(issue -> {
            issueId = issue.getString("id");
            taskLauncher.launchReportTask();
            markIssueInProgress(issue);
        });
    }
}
