import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.ir.crawl.parse.parser.AmazonParser;
import org.junit.BeforeClass;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Map;

@Test
public class AmazonParserTest {

    private static final String TEST_RESOURCES_DIR = "src/test/resources/";

    private static String  COMPUTER = null , ELECTRONIC = null, CLOTHING = null, BABY = null, HEALTH = null, KITCHEN = null;

    @BeforeClass
    public void setUp() throws Exception {
        COMPUTER = Files.toString(new File(TEST_RESOURCES_DIR+"amazon/Dell-Inspiron.html"), Charsets.UTF_8);
        ELECTRONIC = Files.toString(new File(TEST_RESOURCES_DIR+"amazon/Duracell-Procell.html"), Charsets.UTF_8);
        CLOTHING = Files.toString(new File(TEST_RESOURCES_DIR+"amazon/Levi-Mens-505.html"), Charsets.UTF_8);
        HEALTH = Files.toString(new File(TEST_RESOURCES_DIR+"amazon/Tide-Pods.html"), Charsets.UTF_8);
        BABY = Files.toString(new File(TEST_RESOURCES_DIR+"amazon/Pampers-Sensitive-Wipes.html"), Charsets.UTF_8);
        KITCHEN = Files.toString(new File(TEST_RESOURCES_DIR+"amazon/Keurig-K65.html"), Charsets.UTF_8);

    }

    @Test(groups = { "ProductDetails" })
    public void testComputerItem() {
        boolean passed = testParseProduct(COMPUTER);
        if(!passed){
            Assert.fail("Failed to parse computer item.");
        }
    }

    @Test(groups = { "ProductDetails" })
    public void testElectronicItem() {
        boolean passed = testParseProduct(ELECTRONIC);
        if(!passed){
            Assert.fail("Failed to parse electronic item.");
        }
    }

    @Test(groups = { "ProductDetails" })
    public void testClothingItem() {
        boolean passed = testParseProduct(CLOTHING);
        if(!passed){
            Assert.fail("Failed to parse clothing item.");
        }
    }

    @Test(groups = { "ProductDetails" })
    public void testBabyItem() {
        boolean passed = testParseProduct(BABY);
        if(!passed){
            Assert.fail("Failed to parse baby item.");
        }
    }

    @Test(groups = { "ProductDetails" })
    public void testHealthItem() {
        boolean passed = testParseProduct(HEALTH);
        if(!passed){
            Assert.fail("Failed to parse health item.");
        }
    }

    @Test(groups = { "ProductDetails" })
    public void testKitchenItem() {
        boolean passed = testParseProduct(KITCHEN);
        if(!passed){
            Assert.fail("Failed to parse kitchen item.");
        }
    }

    public boolean testParseProduct(String htmlData){

        Map<String, String> responseMap = new AmazonParser().parseProductAttributes(htmlData);
        System.out.println(responseMap);
        for(Map.Entry<String, String> e : responseMap.entrySet()){
            if(e.getValue() == null || e.getValue().equalsIgnoreCase("")){
                System.out.println("KEY: " + e.getKey() + " VAL: " + e.getValue());
                return false;
            }
        }
        return true;
    }
}
