package apiServices;

import com.aventstack.extentreports.ExtentTest;
import com.intuit.karate.Results;
import com.intuit.karate.core.*;
import com.intuit.karate.http.HttpRequestBuilder;

public class ExtentReportHook implements ExecutionHook {
    private static ExtentTest test;
    String Status, Error, Tags;

    @Override
    public boolean beforeScenario(Scenario scenario, ScenarioContext context) {
        return true;
    }

    @Override
    public void afterScenario(ScenarioResult result, ScenarioContext context) {
        if (result.isFailed()) {
            Status = "Failed";

        } else {
            Status = "Passed";
        }

        if (result.getError() == null) {
            Error = "No Error";
        } else {
            Error = result.getError().toString();
        }

        Tags = "";
        if (result.getScenario().getTags() == null) {
            Tags = "No Tags";
        } else {
            for (int z = 0; z < result.getScenario().getTags().size(); z++) {

                Tags = Tags + result.getScenario().getTags().get(z) + ",";
            }
            Tags = Tags.substring(0, Tags.length() - 1);
        }
        test = ExtentManager.getInstance().createTest("<b>Scenario: </b>" + result.getScenario().getName());
        test.info("<b>Url: </b>" + context.getRequestBuilder().getUrlAndPath());
        test.info("<b>Feature: </b>" + result.getScenario().getFeature().getName());
        test.info("<b>Tag: </b>" + Tags);
        test.info("<b>Method: </b>" + context.getPrevRequest().getMethod());
        test.info("<b>Status: </b>" + Status);
        if (Status == "Failed") {
            test.fail("<b>Error: </b>" + Error);
        }
        String response = new String(context.getPrevResponse().getBody());
        test.info("<b>Response:</b>" + response);


    }

    @Override
    public boolean beforeFeature(Feature feature, ExecutionContext context) {


        return true;
    }

    @Override
    public void afterFeature(FeatureResult result, ExecutionContext context) {

    }

    @Override
    public void beforeAll(Results results) {

        ExtentManager.createReport();

    }

    @Override
    public void afterAll(Results results) {
        //  ExtentReporting.endTest();

        ExtentManager.getInstance().flush();
    }

    @Override
    public boolean beforeStep(Step step, ScenarioContext context) {

        return true;
    }

    @Override
    public void afterStep(StepResult result, ScenarioContext context) {

    }

    @Override
    public String getPerfEventName(HttpRequestBuilder req, ScenarioContext context) {

        return null;
    }

    @Override
    public void reportPerfEvent(PerfEvent event) {

    }

}
