package nogroup.inpaint.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class ImageLoader {
    protected final Path file;

    public ImageLoader(String filename) throws IllegalArgumentException {
        file = Paths.get(filename);
        if (!Files.isRegularFile(file))
            throw new IllegalArgumentException("not a file: " + filename);
        if (Files.notExists(file))
            throw new IllegalArgumentException("unknown (non existing) file: " + filename);
        if (!file.endsWith(".png") || !file.endsWith(".jpeg"))
            throw new IllegalArgumentException(String.format("not an image [%s], allowed extensions: png, jpeg", filename));
    }

    public BufferedImage load(ImageBuilder builder) throws IllegalArgumentException, IOException {
        if (!builder.name.equals(file.toString()))
            throw new IllegalArgumentException(String.format("wrong builder for file [%s]: %s", file, builder));

        BufferedImage image = ImageIO.read(file.toFile());
        builder.width(image.getWidth());
        builder.height(image.getHeight());
        return image;
    }

    public BufferedImage load() throws IOException {
        return ImageIO.read(file.toFile());
    }
}
