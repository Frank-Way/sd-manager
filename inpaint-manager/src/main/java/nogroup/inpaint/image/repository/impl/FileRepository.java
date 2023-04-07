package nogroup.inpaint.image.repository.impl;

import nogroup.inpaint.image.repository.AlreadyExistsException;
import nogroup.inpaint.image.repository.NotFoundException;
import nogroup.inpaint.image.repository.Repository;
import nogroup.inpaint.image.source.SourceImage;
import nogroup.inpaint.image.target.TargetImage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileRepository implements Repository {
    private final static String filename = "images-repository.dat";
    private static Map<String, FileRepository> instances;
    private final Path file;
    private InMemoryRepository inMemRepo;
    private boolean wasInit;

    private FileRepository(Path file) {
        this.file = file;
        wasInit = false;
    }

    public static FileRepository getInstance(String dataDir) throws IllegalArgumentException, IOException {
        if (instances == null)
            instances = new HashMap<>();
        if (instances.containsKey(dataDir))
            return instances.get(dataDir);
        FileRepository instance = newInstance(dataDir);
        instances.put(dataDir, instance);
        return instance;
    }

    private static FileRepository newInstance(String dataDir) throws IllegalArgumentException, IOException {
        Path dir = Paths.get(dataDir);
        if (!Files.isDirectory(dir))
            throw new IllegalArgumentException(String.format("not a dir: %s", dataDir));

        Path file = Paths.get(dataDir, filename);

        if (!Files.exists(dir))
            Files.createDirectory(dir);
        if (!Files.exists(file))
            Files.createFile(file);

        FileRepository instance = new FileRepository(file);

        instance.init();

        return instance;
    }

    private void init() throws IOException {
        inMemRepo = new InMemoryRepository();
        if (!Files.exists(file))
            return;
        byte[] serialized = Files.readAllBytes(file);
        if (serialized.length < 1)
            return;
        ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            try {
                Object o = in.readObject();
                if (o != null)
                    inMemRepo = (InMemoryRepository) o;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void dump() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream out = null;
            out = new ObjectOutputStream(bos);
            out.writeObject(inMemRepo);
            out.flush();
            byte[] serialized = bos.toByteArray();
            Files.write(file, serialized);
        }
    }

    public void reset() throws IOException {
        this.inMemRepo = new InMemoryRepository();
        dump();
    }

    @Override
    public SourceImage createSource(SourceImage source) throws AlreadyExistsException {
        SourceImage result = inMemRepo.createSource(source);
        if (result != null) {
            try {
                dump();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public TargetImage createTarget(SourceImage source, TargetImage target) throws AlreadyExistsException, NotFoundException {
        TargetImage result = inMemRepo.createTarget(source, target);
        if (result != null) {
            try {
                dump();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public SourceImage readSource(TargetImage target) throws NotFoundException {
        return inMemRepo.readSource(target);
    }

    @Override
    public SourceImage readSource(String name) throws NotFoundException {
        return inMemRepo.readSource(name);
    }

    @Override
    public Set<SourceImage> readSources() {
        return inMemRepo.readSources();
    }

    @Override
    public List<TargetImage> readTargets(SourceImage source) throws NotFoundException {
        return inMemRepo.readTargets(source);
    }

    @Override
    public TargetImage readTarget(String name) throws NotFoundException {
        return inMemRepo.readTarget(name);
    }

    @Override
    public SourceImage updateSource(SourceImage source) throws NotFoundException {
        SourceImage result = inMemRepo.updateSource(source);
        if (result != null) {
            try {
                dump();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public TargetImage updateTarget(TargetImage target) throws NotFoundException {
        TargetImage result = inMemRepo.updateTarget(target);
        if (result != null) {
            try {
                dump();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public SourceImage deleteSource(SourceImage source) {
        SourceImage result = inMemRepo.deleteSource(source);
        if (result != null) {
            try {
                dump();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public TargetImage deleteTarget(TargetImage target) {
        TargetImage result = inMemRepo.deleteTarget(target);
        if (result != null) {
            try {
                dump();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public List<TargetImage> deleteTargets(SourceImage source) throws NotFoundException {
        List<TargetImage> result = inMemRepo.deleteTargets(source);
        if (result != null && result.size() > 0) {
            try {
                dump();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileRepository that = (FileRepository) o;
        return Objects.equals(file, that.file) && Objects.equals(inMemRepo, that.inMemRepo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, inMemRepo);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FileRepository{");
        sb.append("file=").append(file);
        sb.append(", inMemRepo=").append(inMemRepo);
        sb.append('}');
        return sb.toString();
    }
}
