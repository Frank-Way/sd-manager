package nogroup.inpaint.image.target;


import nogroup.inpaint.image.Image;

import java.io.Serializable;
import java.util.Objects;

public class TargetImage extends Image implements Serializable {
    private final int rating;
    private final Sampler sampler;
    private final Checkpoint checkpoint;

    public TargetImage(String name, String description, int width, int height, int rating, Sampler sampler, Checkpoint checkpoint) {
        super(name, description, width, height);
        this.rating = rating;
        this.sampler = sampler;
        this.checkpoint = checkpoint;
    }

    public int getRating() {
        return rating;
    }

    public Sampler getSampler() {
        return sampler;
    }

    public Checkpoint getCheckpoint() {
        return checkpoint;
    }

    @Override
    public TargetImage copy() {
        return new TargetImage(name, description, width, height, rating, sampler, checkpoint);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TargetImage that = (TargetImage) o;
        return rating == that.rating && sampler == that.sampler && checkpoint == that.checkpoint;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), rating, sampler, checkpoint);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TargetImage{");
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

    public static class Builder extends Image.Builder {
        private int rating;
        private Sampler sampler;
        private Checkpoint checkpoint;

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

        public Builder rating(int rating) {
            this.rating = rating;
            return this;
        }

        public Builder sampler(Sampler sampler) {
            this.sampler = sampler;
            return this;
        }

        public Builder checkpoint(Checkpoint checkpoint) {
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
    }
}
