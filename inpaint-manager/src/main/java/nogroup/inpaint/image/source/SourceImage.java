package nogroup.inpaint.image.source;

import nogroup.inpaint.image.Image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SourceImage extends Image implements Serializable {
    private final List<String> tags;

    public SourceImage(String name, String description, int width, int height, List<String> tags) {
        super(name, description, width, height);
        this.tags = tags;
    }

    public List<String> getTags() {
        return tags;
    }

    @Override
    public SourceImageBuilder toBuilder() {
        SourceImageBuilder builder = new SourceImageBuilder(this.name)
                .tags(new ArrayList<>(this.tags));
        fillBuilder(builder);
        return builder;
    }

    @Override
    public SourceImage copy() {
        return toBuilder().build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SourceImage that = (SourceImage) o;
        return Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tags);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SourceImage{");
        sb.append("name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", width=").append(width);
        sb.append(", height=").append(height);
        sb.append(", tags=").append(tags);
        sb.append('}');
        return sb.toString();
    }

}
