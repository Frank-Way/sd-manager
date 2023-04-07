package nogroup.inpaint.image.repository.impl;

import nogroup.inpaint.image.repository.AlreadyExistsException;
import nogroup.inpaint.image.repository.NotFoundException;
import nogroup.inpaint.image.repository.Repository;
import nogroup.inpaint.image.source.SourceImage;
import nogroup.inpaint.image.target.TargetImage;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRepository implements Repository, Serializable {
    private final Map<String, SourceImage> sources;
    private final Map<String, TargetImage> targets;
    private final Map<String, List<String>> sourceToTargets;

    public InMemoryRepository() {
        sources = new ConcurrentHashMap<>();
        targets = new ConcurrentHashMap<>();
        sourceToTargets = new ConcurrentHashMap<>();
    }

    @Override
    public SourceImage createSource(SourceImage source) throws AlreadyExistsException {
        if (this.sources.containsKey(source.getName()))
            if (!this.sources.get(source.getName()).equals(source))
                throw new AlreadyExistsException(String.format("duplicate source nogroup.inpaint.image: provided [%s], existing [%s]; use update to replace it",
                        source, this.sources.get(source.getName())));
            else
                return null;

        this.sources.put(source.getName(), source.copy());
        this.sourceToTargets.put(source.getName(), new ArrayList<>());

        return this.sources.get(source.getName()).copy();
    }

    @Override
    public TargetImage createTarget(SourceImage source, TargetImage target) throws AlreadyExistsException, NotFoundException {
        if (!this.sources.containsKey(source.getName()))
            throw new NotFoundException(String.format("unknown source nogroup.inpaint.image: %s", source));
        if (this.targets.containsKey(target.getName())) {
            if (!this.targets.get(target.getName()).equals(target))
                throw new AlreadyExistsException(String.format("duplicate target nogroup.inpaint.image: provided [%s], existing [%s]; use update to replace it",
                        target, this.targets.get(target.getName())));

            for (Map.Entry<String, List<String>> entry : this.sourceToTargets.entrySet())
                if (entry.getValue().contains(target.getName()) && !entry.getKey().equals(source.getName()))
                    throw new AlreadyExistsException(String.format("target nogroup.inpaint.image assigned to another source nogroup.inpaint.image: target [%s], " +
                                    "provided source [%s], assigned source [%s]; delete target before reassignment it to another source",
                            target, source, this.sources.get(entry.getKey())));

            return null;
        }

        this.targets.put(target.getName(), target.copy());
        this.sourceToTargets.get(source.getName()).add(target.getName());

        return this.targets.get(target.getName()).copy();
    }

    @Override
    public SourceImage readSource(TargetImage target) throws NotFoundException {
        if (!this.targets.containsKey(target.getName()))
            throw new NotFoundException(String.format("unknown target nogroup.inpaint.image: %s", target));

        for (Map.Entry<String, List<String>> entry : this.sourceToTargets.entrySet())
            if (entry.getValue().contains(target.getName()))
                return this.sources.get(entry.getKey()).copy();

        // unreachable code, stub prevents compilation errors
        throw new NotFoundException(String.format("unknown source for target nogroup.inpaint.image: %s", target));
    }

    @Override
    public SourceImage readSource(String name) throws NotFoundException {
        if (!this.sources.containsKey(name))
            throw new NotFoundException(String.format("unknown source nogroup.inpaint.image: %s", name));

        return this.sources.get(name).copy();
    }

    @Override
    public Set<SourceImage> readSources() {
        final Set<SourceImage> sources = new HashSet<>(this.sources.size());
        for (SourceImage source : this.sources.values())
            sources.add(source.copy());
        return sources;
    }

    @Override
    public List<TargetImage> readTargets(SourceImage source) throws NotFoundException {
        if (!this.sources.containsKey(source.getName()) || !this.sourceToTargets.containsKey(source.getName()))
            throw new NotFoundException(String.format("unknown source nogroup.inpaint.image: %s", source));

        final List<TargetImage> targets = new ArrayList<>(this.sourceToTargets.get(source.getName()).size());
        for (String targetName : this.sourceToTargets.get(source.getName()))
            targets.add(this.targets.get(targetName).copy());

        return targets;
    }

    @Override
    public TargetImage readTarget(String name) throws NotFoundException {
        if (!this.targets.containsKey(name))
            throw new NotFoundException(String.format("unknown target nogroup.inpaint.image: %s", name));

        return this.targets.get(name).copy();
    }

    @Override
    public SourceImage updateSource(SourceImage source) throws NotFoundException {
        if (!this.sources.containsKey(source.getName()))
            throw new NotFoundException(String.format("unknown source nogroup.inpaint.image: %s", source));

        if (this.sources.get(source.getName()).equals(source))
            return null;

        this.sources.put(source.getName(), source.copy());

        return this.sources.get(source.getName()).copy();
    }

    @Override
    public TargetImage updateTarget(TargetImage target) throws NotFoundException {
        if (!this.targets.containsKey(target.getName()))
            throw new NotFoundException(String.format("unknown target nogroup.inpaint.image: %s", target));

        if (this.targets.get(target.getName()).equals(target))
            return null;

        this.targets.put(target.getName(), target.copy());

        return this.targets.get(target.getName()).copy();
    }

    @Override
    public SourceImage deleteSource(SourceImage source) {
        if (!this.sources.containsKey(source.getName()))
            return null;

        this.sourceToTargets.remove(source.getName());

        return this.sources.remove(source.getName());
    }

    @Override
    public TargetImage deleteTarget(TargetImage target) {
        if (!this.targets.containsKey(target.getName()))
            return null;

        for (Map.Entry<String, List<String>> entry : this.sourceToTargets.entrySet())
            entry.getValue().remove(target.getName());

        return this.targets.remove(target.getName());
    }

    @Override
    public List<TargetImage> deleteTargets(SourceImage source) throws NotFoundException {
        if (!this.sources.containsKey(source.getName()) || !this.sourceToTargets.containsKey(source.getName()))
            throw new NotFoundException(String.format("unknown source nogroup.inpaint.image: %s", source));

        final List<TargetImage> removedTargets = new ArrayList<>(this.sourceToTargets.get(source.getName()).size());
        for (String targetName : this.sourceToTargets.get(source.getName())) {
            TargetImage removedTarget = this.targets.remove(targetName);
            if (removedTarget != null)
                removedTargets.add(removedTarget);
        }

        this.sourceToTargets.get(source.getName()).clear();
        return removedTargets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryRepository that = (InMemoryRepository) o;
        return Objects.equals(sources, that.sources) && Objects.equals(targets, that.targets) && Objects.equals(sourceToTargets, that.sourceToTargets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sources, targets, sourceToTargets);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InMemoryRepository{");
        sb.append("sources=").append(sources);
        sb.append(", targets=").append(targets);
        sb.append(", sourceToTargets=").append(sourceToTargets);
        sb.append('}');
        return sb.toString();
    }
}
