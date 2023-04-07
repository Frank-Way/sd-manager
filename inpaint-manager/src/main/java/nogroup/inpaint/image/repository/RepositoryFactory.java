package nogroup.inpaint.image.repository;

import nogroup.inpaint.image.repository.impl.FileRepository;
import nogroup.inpaint.image.repository.impl.InMemoryRepository;

import java.io.IOException;

public class RepositoryFactory {
    public Repository create(RepositoryImpl implType, Object... args) throws IOException {
        switch (implType) {
            case FILE:
                return FileRepository.getInstance((String) args[0]);
            case IN_MEMORY:
                return new InMemoryRepository();
        }
        throw new IllegalArgumentException("unknown repository implementation: " + implType);
    }
}
