import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.ir.config.retailer.amazon.AmazonFieldNames;
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

    private static final AmazonParser PARSER = new AmazonParser();

    private static final String TEST_DATA_LOCATION =  (AmazonParserTest.class.getProtectionDomain().getCodeSource().getLocation()
                                                        + "amazon/data/").replaceAll("file:", "");

    @DataProvider(name = "amazonData")
    public Object[][] amazonTestProducts() throws IOException {
        Reporter.log("FILE LOCATION: " + TEST_DATA_LOCATION);
        File file = new File(TEST_DATA_LOCATION);
        File[] files = file.listFiles();

        Object[][] data = new Object[files.length][2];
        for(int i=0; i<files.length; i++){
            data[i][0]=files[i].getName();
            data[i][1]=files[i].getName();
        }

        return data;
    }

    @Test(groups = { "ProductDetails" }, dataProvider = "amazonData")
    public void testProduct(String name, String fileName) throws IOException {
        String data = Files.toString(new File(TEST_DATA_LOCATION+fileName), Charsets.UTF_8);
        Map<Field, Object> dataMap = PARSER.parseAll(data);
        Reporter.log(StringUtil.prettifyMapForDebug(dataMap));

        for(Field field : PARSER.getFields()){
            if(!field.validate(dataMap)){
                    Assert.fail("Validation failed for field: " + name + " !");
            }
        }
//        if(response.get(AmazonFieldNames.VRNT_SPEC) != null){
//            if(response.get(AmazonFieldNames.VRNT_IDS) == null)
//                Assert.fail("Failed: Variant not captured for " + name + " item.");
//        }
        LogUtil.afterTestMarker();;
    }

}
