package util;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;


public class AdHocTestUtil {

    private static final Logger logger = LogManager.getLogger(AdHocTestUtil.class);

    public static void main(String[] args) throws Exception{
        testFieldParsing();
    }

    private static void testFieldParsing() throws IOException {
        File file = new File("/Users/sathiya/Work/Git/ir/Crawl/src/test/resources/amazon/data");
        for (File f : file.listFiles()){
            String data = Files.toString(f, Charsets.ISO_8859_1);
            Document doc = Jsoup.parse(data, "http://www.amazon.com/");

            // if List has multiple price '-' assign smallest to actual, if actual is null. List can be null.
            //if(doc.select("div[class=buying] > b").size() > 0){
            logger.info(f.getName() + "     LIST-1: " + doc.select("div[class=detailBreadcrumb]").text());
            logger.info(f.getName() + "     LIST-1: " + doc.select("div[class=detailBreadcrumb]").text());
            //}
            logger.info(f.getName() + "---------------------------------------------------------------------------");
        }
    }
}
