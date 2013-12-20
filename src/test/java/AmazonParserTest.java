import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.parser.AmazonParser;
import com.ir.util.StringUtil;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Test(suiteName = "Amazon", description = "Test for Amazon Product Pages")
public class AmazonParserTest {

    private final AmazonParser amazonParser = new AmazonParser();

    private static final String TEST_DATA_LOCATION =  (AmazonParserTest.class.getProtectionDomain().getCodeSource().getLocation()
                                                        + "amazon/data/").replaceAll("file:", "");

    @DataProvider(name = "amazonData")
    public Object[][] amazonTestProducts() throws IOException {
        Reporter.log("FILE LOCATION: " + TEST_DATA_LOCATION);
        File file = new File(TEST_DATA_LOCATION);
        File[] files = file.listFiles();

        Object[][] data = new Object[files.length][2];
        for(int i=0; i<files.length; i++){
            data[i][0]=files[i].getName().split("-")[0];
            data[i][1]=files[i].getName().split("-")[1];
        }

        return data;
    }

    @Test(groups = { "ProductDetails" }, dataProvider = "amazonData")
    public void testProduct(String id, String category) throws IOException {
        String data = Files.toString(new File(TEST_DATA_LOCATION+id+"-"+category), Charsets.UTF_8);
        Map<Field, Object> dataMap = amazonParser.parseAll(data);
        Reporter.log(StringUtil.prettifyMapForDebug(dataMap));

        for(Field field : amazonParser.getFields()){
            if(!field.isValid(amazonParser, dataMap)){
                    Assert.fail("Validation failed for field: " + field + " !");
            }
        }
        LogUtil.afterTestMarker();;
    }

}
