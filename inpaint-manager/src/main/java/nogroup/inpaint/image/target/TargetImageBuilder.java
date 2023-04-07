package nogroup.inpaint.image.target;

import nogroup.inpaint.image.ImageBuilder;

import java.util.Objects;

public class TargetImageBuilder extends ImageBuilder {
    private int rating;
    private Sampler sampler;
    private Checkpoint checkpoint;

    public TargetImageBuilder(String name) {
        super(name);
    }

    @Override
    public TargetImageBuilder description(String description) {
        return (TargetImageBuilder) super.description(description);
    }

    @Override
    public TargetImageBuilder width(int width) {
        return (TargetImageBuilder) super.width(width);
    }

    @Override
    public TargetImageBuilder height(int height) {
        return (TargetImageBuilder) super.height(height);
    }

    public TargetImageBuilder rating(int rating) {
        this.rating = rating;
        return this;
    }

    public TargetImageBuilder sampler(Sampler sampler) {
        this.sampler = sampler;
        return this;
    }

    public TargetImageBuilder checkpoint(Checkpoint checkpoint) {
        this.checkpoint = checkpoint;
        return this;
    }

    @Override
    public TargetImage build() {
        if (sampler == null)
            sampler = Sampler.EULER_A;
        if (checkpoint == null)
            checkpoint = Checkpoint.SD;

        return new TargetImage(name, description, width, height, rating, sampler, checkpoint);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TargetImageBuilder that = (TargetImageBuilder) o;
        return rating == that.rating && sampler == that.sampler && checkpoint == that.checkpoint;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), rating, sampler, checkpoint);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TargetImageBuilder{");
        sb.append("name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", width=").append(width);
        sb.append(", height=").append(height);
        sb.append(", rating=").append(rating);
        sb.append(", sampler=").append(sampler);
        sb.append(", checkpoint=").append(checkpoint);
        sb.append('}');
        return sb.toString();
    }
}
