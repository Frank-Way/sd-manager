package nogroup.inpaint.image.repository;

import nogroup.inpaint.image.source.SourceImage;
import nogroup.inpaint.image.target.TargetImage;

import java.util.List;
import java.util.Set;

public interface Repository {
    SourceImage createSource(SourceImage source) throws AlreadyExistsException;

    TargetImage createTarget(SourceImage source, TargetImage target) throws AlreadyExistsException, NotFoundException;

    SourceImage readSource(TargetImage target) throws NotFoundException;

    SourceImage readSource(String name) throws NotFoundException;

    Set<SourceImage> readSources();

    List<TargetImage> readTargets(SourceImage source) throws NotFoundException;

    TargetImage readTarget(String name) throws NotFoundException;

    SourceImage updateSource(SourceImage source) throws NotFoundException;

    TargetImage updateTarget(TargetImage target) throws NotFoundException;

    SourceImage deleteSource(SourceImage source);

    TargetImage deleteTarget(TargetImage target);

    List<TargetImage> deleteTargets(SourceImage source) throws NotFoundException;
}
