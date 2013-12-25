import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.ir.config.retailer.amazon.ItemParser;
import com.ir.core.error.Error;
import com.ir.core.error.ErrorUtil;
import com.ir.crawl.parse.bean.ParseResponse;
import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.parser.Parser;
import com.ir.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Test(suiteName = "Amazon", description = "Test for Amazon Product Pages")
public class AmazonParserTest {

    protected static final Logger logger = LogManager.getLogger(AmazonParserTest.class.getName());

    private final Parser parser = new ItemParser();

    private static final String TEST_DATA_LOCATION =  AmazonParserTest.class.getResource("test-data/amazon/").getPath();

    @DataProvider(name = "amazonData")
    public Object[][] amazonTestProducts() throws IOException {
        logger.info("FILE LOCATION: " + TEST_DATA_LOCATION);

        File file = new File(TEST_DATA_LOCATION);
        File[] files = file.listFiles();
        int filesLength = files.length;
        Object[][] data = new Object[filesLength][2];
        System.out.println("FILES LENGTH:    " + filesLength);
        for(int i=0; i<filesLength; i++){
            data[i][0]=String.valueOf(i);
            data[i][1]=files[i].getName();
        }
        return data;
    }

    @Test(groups = { "ProductDetails" }, dataProvider = "amazonData")
    public void testProduct(String count, String id) throws IOException {
        String data = Files.toString(new File(TEST_DATA_LOCATION + id), Charsets.ISO_8859_1);
        Document htmlDocument = Jsoup.parse(data, "http://www.amazon.com");
        ParseResponse parseResponse = parser.parseAll(htmlDocument);
        if(parseResponse.isEligibleForProcessing()){
            Map<Field, Object> dataMap = parseResponse.getDataMap();
            Reporter.log(StringUtil.prettifyMapForDebug(dataMap));

            for(Field field : parser.getFields()){
                if(!field.isValid(parser, dataMap)){
                    Assert.fail("Validation failed for field: " + field + " !");
                }
            }
            List<Error> errors = ErrorUtil.getErrorCodes(parser.getErrorField(), dataMap);
            if(errors != null){
                for(Error error : errors){
                    Assert.fail(error.toString());
                }
            }
        }
        LogUtil.afterTestMarker();
    }
}
