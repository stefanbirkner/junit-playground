package rule.example;

import static java.util.Arrays.asList;

import java.io.File;
import java.nio.file.Files;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TemporaryFolderTest {
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void writesToFile() throws Exception {
        File file = folder.newFile();
        Files.write(file.toPath(), asList("dummy text"));
    }
}
