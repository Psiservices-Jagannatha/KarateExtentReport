package apiServices;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
