# Extent Report Integration Using Karate DSL

## Installation :

Add the dependency to your pom.xml file:


        <dependencies>
        <dependency>
            <groupId>com.intuit.karate</groupId>
            <artifactId>karate-apache</artifactId>
            <version>${karate.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.intuit.karate</groupId>
            <artifactId>karate-junit4</artifactId>
            <version>${karate.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.aventstack</groupId>
            <artifactId>extentreports</artifactId>
            <version>${extentreport.version}</version>
        </dependency>
        <dependency>
            <groupId>net.masterthought</groupId>
            <artifactId>cucumber-reporting</artifactId>
            <version>3.8.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>



## Create a ExtentManager Class :
public class ExtentManager {

    private static ExtentReports extent;

    public static ExtentReports getInstance(){
        return extent;
    }

    public static void createInstance(String fileName){
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter(fileName);
        htmlReporter.config().setTheme(Theme.STANDARD);
        htmlReporter.config().setEncoding("UTF-8");
        htmlReporter.config().setProtocol(Protocol.HTTPS);
        htmlReporter.config().setDocumentTitle("Karate Extent Report");
        htmlReporter.config().setReportName("Karate Extent Demo");
        htmlReporter.viewConfigurer().viewOrder().as(
                new ViewName[] {
                        ViewName.DASHBOARD,
                        ViewName.TEST,
                        ViewName.AUTHOR,
                        ViewName.DEVICE,
                        ViewName.EXCEPTION,
                        ViewName.LOG
                }).apply();


        htmlReporter.config().setTimeStampFormat("MM/dd/yyyy hh:mm:ss a");
        extent = new ExtentReports();
        extent.setSystemInfo("Created By", "Jagannatha");
        extent.setSystemInfo("Autmation Type", "API");
        extent.attachReporter(htmlReporter);
    }

    public static void  createReport(){
        System.out.println("Initialize Extent report was called");
        if(ExtentManager.getInstance() == null){
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yy");
            String formattedDate = dateFormat.format(date);
            ExtentManager.createInstance("results/" + "Extent_Report_Demo_" + formattedDate + ".html");
        }
    }
}

# Create ExtentReport Hook

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
        test.assignCategory(Tags);
        test.info("<b>Method: </b>" + context.getPrevRequest().getMethod());
        test.info("<b>Status: </b>" + Status);

        if (Status == "Failed") {
            test.fail("<b>Error: </b>" + Error);
        }

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

# Create a Karate Runner Class :
    public class UsersRunner {
    @Test
    public void testParallel() throws Exception {
        Results result = Runner.path("classpath:apiServices")
                .hook(new ExtentReportHook())
                .parallel(10);
        generateReport(result.getReportDir());
        Assert.assertTrue(result.getErrorMessages(), result.getFailCount() == 0);
    }

    public static void generateReport(String karateOutputPath) throws Exception {
        Collection<File> jsonFiles = FileUtils.listFiles(new File(karateOutputPath), new String[]{"json"}, true);
        List<String> jsonPaths = new ArrayList<>(jsonFiles.size());
        jsonFiles.forEach(file -> jsonPaths.add(file.getAbsolutePath()));
        Configuration config = new Configuration(new File("target"), "qa");
        ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
        reportBuilder.generateReports();
    }
}


