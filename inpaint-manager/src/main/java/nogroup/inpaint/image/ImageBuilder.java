package nogroup.inpaint.image;

import java.util.Objects;

public abstract class ImageBuilder {
    protected final String name;
    protected String description;
    protected int width;
    protected int height;

    public ImageBuilder(String name) {
        this.name = name;
    }

    public ImageBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ImageBuilder width(int width) {
        this.width = width;
        return this;
    }

    public ImageBuilder height(int height) {
        this.height = height;
        return this;
    }

    public abstract Image build();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageBuilder that = (ImageBuilder) o;
        return width == that.width && height == that.height && Objects.equals(name, that.name) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, width, height);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ImageBuilder{");
        sb.append("name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", width=").append(width);
        sb.append(", height=").append(height);
        sb.append('}');
        return sb.toString();
    }
}
