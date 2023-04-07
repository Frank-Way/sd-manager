package nogroup.inpaint.image.source;

import nogroup.inpaint.image.Image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SourceImage extends Image implements Serializable {
    private final List<String> tags;

    public SourceImage() {
        this(null, null, 0, 0, null);
    }

    public SourceImage(String name, String description, int width, int height, List<String> tags) {
        super(name, description, width, height);
        this.tags = tags;
    }

    public List<String> getTags() {
        return tags;
    }

    @Override
    public SourceImage copy() {
        return new SourceImage(name, description, width, height, new ArrayList<>(tags));
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

    public static class Builder extends Image.Builder {
        private List<String> tags;

        public Builder(String name) {
            super(name);
        }

        @Override
        public Builder description(String description) {
            return (Builder) super.description(description);
        }

        @Override
        public Builder width(int width) {
            return (Builder) super.width(width);
        }

        @Override
        public Builder height(int height) {
            return (Builder) super.height(height);
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder addTag(String tag) {
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
    }
}
