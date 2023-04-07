package nogroup.inpaint.image.repository;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

class FileRepositoryTest extends RepositoryTest {
    static String getTempDir() {
        try {
            Path tempDirectory = Files.createTempDirectory("test-file-image-repo-dir_");
            return tempDirectory.toString();
        } catch (IOException e) {
            return "test-file-image-repo-dir";
        }
    }

    static void deleteTempDir(String dir) {
        Path pathToBeDeleted = Paths.get(dir);
        try {
            Files.walk(pathToBeDeleted)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void createSource() {
        String tempDir = getTempDir();
        RepositoryTest.createSource(new RepositoryFactory(), RepositoryImpl.FILE, tempDir);
        deleteTempDir(tempDir);
    }

    @Test
    void createTargets() {
        String tempDir = getTempDir();
        RepositoryTest.createTargets(new RepositoryFactory(), RepositoryImpl.FILE, tempDir);
        deleteTempDir(tempDir);
    }

    @Test
    void readSourceByTarget() {
        String tempDir = getTempDir();
        RepositoryTest.readSourceByTarget(new RepositoryFactory(), RepositoryImpl.FILE, tempDir);
        deleteTempDir(tempDir);
    }

    @Test
    void readSourceByName() {
        String tempDir = getTempDir();
        RepositoryTest.readSourceByName(new RepositoryFactory(), RepositoryImpl.FILE, tempDir);
        deleteTempDir(tempDir);
    }

    @Test
    void readSources() {
        String tempDir = getTempDir();
        RepositoryTest.readSources(new RepositoryFactory(), RepositoryImpl.FILE, tempDir);
        deleteTempDir(tempDir);
    }

    @Test
    void readTargets() {
        String tempDir = getTempDir();
        RepositoryTest.readTargets(new RepositoryFactory(), RepositoryImpl.FILE, tempDir);
        deleteTempDir(tempDir);
    }

    @Test
    void readTarget() {
        String tempDir = getTempDir();
        RepositoryTest.readTarget(new RepositoryFactory(), RepositoryImpl.FILE, tempDir);
        deleteTempDir(tempDir);
    }

    @Test
    void updateSource() {
        String tempDir = getTempDir();
        RepositoryTest.updateSource(new RepositoryFactory(), RepositoryImpl.FILE, tempDir);
        deleteTempDir(tempDir);
    }

    @Test
    void updateTarget() {
        String tempDir = getTempDir();
        RepositoryTest.updateTarget(new RepositoryFactory(), RepositoryImpl.FILE, tempDir);
        deleteTempDir(tempDir);
    }

    @Test
    void deleteSource() {
        String tempDir = getTempDir();
        RepositoryTest.deleteSource(new RepositoryFactory(), RepositoryImpl.FILE, tempDir);
        deleteTempDir(tempDir);
    }

    @Test
    void deleteTarget() {
        String tempDir = getTempDir();
        RepositoryTest.deleteTarget(new RepositoryFactory(), RepositoryImpl.FILE, tempDir);
        deleteTempDir(tempDir);
    }

    @Test
    void deleteTargets() {
        String tempDir = getTempDir();
        RepositoryTest.deleteTargets(new RepositoryFactory(), RepositoryImpl.FILE, tempDir);
        deleteTempDir(tempDir);
    }
}