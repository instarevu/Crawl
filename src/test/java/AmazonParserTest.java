import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.ir.crawl.parse.parser.AmazonParser;
import org.junit.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Map;

@Test
public class AmazonParserTest {

    private static final String TEST_RESOURCES_DIR = "src/test/resources/";

    String electronicProductData = null;

    @BeforeClass
    public void setUp() throws Exception {
        electronicProductData = Files.toString(new File(TEST_RESOURCES_DIR+"amazon/Electronic-MemoryCard.html"), Charsets.UTF_8);
    }

    @Test(groups = { "Electronics" })
    public void testAnElectronicProduct() {
        Map<String, String> responseMap = new AmazonParser().parseProductAttributes(electronicProductData);
        System.out.println(responseMap);
    }
}
