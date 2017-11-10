package travel.excel;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ExcelExporterTest {

    @Test
    public void testWriteToFile() throws IOException {
        File test = new File("test.te.34");
        if(!test.exists()) {
            test.createNewFile();
        }
    }

}