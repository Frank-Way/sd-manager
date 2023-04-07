package nogroup.inpaint.service;

import nogroup.inpaint.image.Image;
import nogroup.inpaint.image.source.SourceImage;
import nogroup.inpaint.image.target.Checkpoint;
import nogroup.inpaint.image.target.Sampler;
import nogroup.inpaint.image.target.TargetImage;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface InpaintManagerService {

    List<SourceImage> loadSources(String dir);

    List<TargetImage> loadTargets(String dir);

    List<BufferedImage> loadImages(String dir);

    boolean assign(SourceImage source, TargetImage target);

    boolean reassign(SourceImage source, TargetImage target);

    void deassign(SourceImage source, TargetImage target);

    boolean assignAll(SourceImage source, TargetImage... targets);

    boolean reassignAll(SourceImage source, TargetImage... targets);

    void deassignAll(SourceImage source, TargetImage... targets);

    boolean setTags(SourceImage source, String... tags);

    boolean addDescription(SourceImage source, String description);

    boolean addDescription(TargetImage source, String description);

    default List<Checkpoint> getCheckpoints() {
        return new ArrayList<>(Arrays.asList(Checkpoint.values()));
    }

    default List<Sampler> getSamplers() {
        return new ArrayList<>(Arrays.asList(Sampler.values()));
    }

    boolean setSampler
}
