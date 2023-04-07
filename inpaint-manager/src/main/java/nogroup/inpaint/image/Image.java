package nogroup.inpaint.image;

import java.io.Serializable;
import java.util.Objects;

public abstract class Image implements Serializable {
    protected final String name;
    protected final String description;
    protected final int width;
    protected final int height;

    public Image(String name, String description, int width, int height) {
        if (name == null) {
            throw new IllegalArgumentException("no name provided");
        }
        this.name = name;
        this.description = description;
        this.width = width;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getDescription() {
        return description;
    }

    public abstract Image copy();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return width == image.width && height == image.height && name.equals(image.name) && Objects.equals(description, image.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, width, height);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Image{");
        sb.append("name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", width=").append(width);
        sb.append(", height=").append(height);
        sb.append('}');
        return sb.toString();
    }

}
