package nogroup.inpaint.image.source;

import nogroup.inpaint.image.ImageBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SourceImageBuilder extends ImageBuilder {
    private List<String> tags;

    public SourceImageBuilder(String name) {
        super(name);
    }

    @Override
    public SourceImageBuilder description(String description) {
        return (SourceImageBuilder) super.description(description);
    }

    @Override
    public SourceImageBuilder width(int width) {
        return (SourceImageBuilder) super.width(width);
    }

    @Override
    public SourceImageBuilder height(int height) {
        return (SourceImageBuilder) super.height(height);
    }

    public SourceImageBuilder tags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public SourceImageBuilder addTag(String tag) {
        if (tags == null)
            tags = new ArrayList<>();

        if (!tags.contains(tag))
            tags.add(tag);

        return this;
    }

    @Override
    public SourceImage build() {
        if (tags == null)
            tags = new ArrayList<>();

        return new SourceImage(name, description, width, height, tags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SourceImageBuilder that = (SourceImageBuilder) o;
        return Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tags);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SourceImageBuilder{");
        sb.append("name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", width=").append(width);
        sb.append(", height=").append(height);
        sb.append(", tags=").append(tags);
        sb.append('}');
        return sb.toString();
    }
}
