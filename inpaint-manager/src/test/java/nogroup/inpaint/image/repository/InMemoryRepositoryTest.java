package nogroup.inpaint.image.repository;

import org.junit.jupiter.api.Test;

class InMemoryRepositoryTest extends RepositoryTest {

    @Test
    void createSource() {
        RepositoryTest.createSource(new RepositoryFactory(), RepositoryImpl.IN_MEMORY);
    }

    @Test
    void createTargets() {
        RepositoryTest.createTargets(new RepositoryFactory(), RepositoryImpl.IN_MEMORY);
    }

    @Test
    void readSourceByTarget() {
        RepositoryTest.readSourceByTarget(new RepositoryFactory(), RepositoryImpl.IN_MEMORY);
    }

    @Test
    void readSourceByName() {
        RepositoryTest.readSourceByName(new RepositoryFactory(), RepositoryImpl.IN_MEMORY);
    }

    @Test
    void readSources() {
        RepositoryTest.readSources(new RepositoryFactory(), RepositoryImpl.IN_MEMORY);
    }

    @Test
    void readTargets() {
        RepositoryTest.readTargets(new RepositoryFactory(), RepositoryImpl.IN_MEMORY);
    }

    @Test
    void readTarget() {
        RepositoryTest.readTarget(new RepositoryFactory(), RepositoryImpl.IN_MEMORY);
    }

    @Test
    void updateSource() {
        RepositoryTest.updateSource(new RepositoryFactory(), RepositoryImpl.IN_MEMORY);
    }

    @Test
    void updateTarget() {
        RepositoryTest.updateTarget(new RepositoryFactory(), RepositoryImpl.IN_MEMORY);
    }

    @Test
    void deleteSource() {
        RepositoryTest.deleteSource(new RepositoryFactory(), RepositoryImpl.IN_MEMORY);
    }

    @Test
    void deleteTarget() {
        RepositoryTest.deleteTarget(new RepositoryFactory(), RepositoryImpl.IN_MEMORY);
    }

    @Test
    void deleteTargets() {
        RepositoryTest.deleteTargets(new RepositoryFactory(), RepositoryImpl.IN_MEMORY);
    }
}