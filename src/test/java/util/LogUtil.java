package util;

import org.testng.Reporter;

public class LogUtil {

    public static void beforeTestMarker(){
        Reporter.log("\n\n********************************************************************************");
    }

    public static void afterTestMarker(){
        Reporter.log("********************************************************************************");
    }
}
