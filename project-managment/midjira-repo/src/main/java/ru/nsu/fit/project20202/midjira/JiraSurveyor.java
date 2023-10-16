package ru.nsu.fit.project20202.midjira;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;


/*
* По задумке этот класс будет раз в 30 минут проверять список ишьюсов в жиру
 */
public class JiraSurveyor {
    private final MidpointTaskLauncher taskLauncher;
    public static final String ACCEPT_HEADER_VALUE = "application/json";

    JiraSurveyor(MidpointTaskLauncher taskLauncher) {
        this.taskLauncher = taskLauncher;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                doJiraSurvey();
            }
        }, 0L, 1000L * 60L * 30L);
    }

    //Возможно необходимо через ресурсы спринга параметризовать
    // пользователя, его токен, юрл жиры, имя проекта, тип ишью
    private JsonArray requestIssuesList() {
        HttpResponse<JsonNode> response = Unirest.get("https://project20202.atlassian.net/rest/api/3/search")
                .basicAuth("email@example.com", "<api_token>")
                .header("Accept", ACCEPT_HEADER_VALUE)
                .queryString("jql", "issuetype = Report")
                .queryString("expand", "transitions")
                .asJson();

        return (JsonArray) response.getBody().getObject().get("issues");
    }

    enum IssueStatus {
        OPEN, IN_PROGRESS, CLOSED
    }

    private IssueStatus getIssueStatus(JsonObject issue) {
        JsonArray transitions = (JsonArray)  issue.get("transitions");
        boolean isInProgress = false;
        boolean isClosed = false;
        for (JsonElement transitionsElement : transitions) {
            JsonObject transition = (JsonObject) transitionsElement;
            var status = transition.get("name").getAsString();
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

    private Optional<JsonObject> findOpenIssue(JsonArray issues) {
        for (var issueElement : issues)
            if (IssueStatus.OPEN == getIssueStatus((JsonObject) issueElement))
                return Optional.of((JsonObject) issueElement);

        return Optional.empty();
    }

    private void markIssueInProgress(JsonObject issue) {
        JsonNodeFactory jnf = JsonNodeFactory.instance;
        ObjectNode payload = jnf.objectNode();

        ObjectNode fields = payload.putObject("fields");
        ObjectNode assignee = fields.putObject("assignee");
        //TODO parametrize with user
        assignee.put("Midjira", "Reporter");

        ObjectNode resolution = fields.putObject("resolution");
        resolution.put("name", "in Progress");


        ObjectNode historyMetadata = payload.putObject("historyMetadata");

        historyMetadata.put("activityDescription", "Complete order processing");
        ObjectNode actor = historyMetadata.putObject("actor");
        actor.put("displayName", "Tony");
        actor.put("id", "tony");
        actor.put("type", "mysystem-user");

        ObjectNode cause = historyMetadata.putObject("cause");
        cause.put("id", "myevent");
        cause.put("type", "mysystem-event");

        historyMetadata.put("description", "From the order testing process");
        ObjectNode extraData = historyMetadata.putObject("extraData");
        extraData.put("Iteration", "10a");
        extraData.put("Step", "4");

        ObjectNode generator = historyMetadata.putObject("generator");
        generator.put("id", "mysystem-1");
        generator.put("type", "mysystem-application");

        historyMetadata.put("type", "myplugin:type");

        ObjectNode transition = payload.putObject("transition");
        transition.put("id", "5");

        ObjectNode update = payload.putObject("update");
        ArrayNode comment = update.putArray("comment");
        ObjectNode comment0 = comment.addObject();

        ObjectNode add = comment0.putObject("add");
        ObjectNode body = add.putObject("body");

        ArrayNode content = body.putArray("content");
        ObjectNode content0 = content.addObject();

        ArrayNode content1 = content0.putArray("content");
        ObjectNode content2 = content1.addObject();

        content2.put("text", "Bug has been fixed");
        content2.put("type", "text");

        content0.put("type", "paragraph");

        body.put("type", "doc");
        body.put("version", 1);

        //TODO remake sample

        // This code sample uses the  'Unirest' library:
        // http://unirest.io/java.html
        HttpResponse<JsonNode> response = Unirest.post("https://project20202.atlassian.net/rest/api/3/issue/" + issue.get("id").getAsString() + "/transitions")
                .basicAuth("email@example.com", "<api_token>")
                .header("Accept", ACCEPT_HEADER_VALUE)
                .header("Content-Type", ACCEPT_HEADER_VALUE)
                .body(payload)
                .asJson();

        System.out.println(response.getStatusText());
    }

    private void doJiraSurvey() {
        var issuesList = requestIssuesList();
        var openReportIssue = findOpenIssue(issuesList);
        openReportIssue.ifPresent(issue -> {
            taskLauncher.launchReportTask();
            markIssueInProgress(issue);
        });
    }
}
