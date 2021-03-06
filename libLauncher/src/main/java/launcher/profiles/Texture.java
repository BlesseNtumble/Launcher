package launcher.profiles;

import launcher.LauncherAPI;
import launcher.helper.IOHelper;
import launcher.helper.SecurityHelper;
import launcher.serialize.HInput;
import launcher.serialize.HOutput;
import launcher.serialize.stream.StreamObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

public final class Texture extends StreamObject {
    private static final SecurityHelper.DigestAlgorithm DIGEST_ALGO = SecurityHelper.DigestAlgorithm.SHA256;

    // Instance
    @LauncherAPI
    public final String url;
    @LauncherAPI
    public final byte[] digest;

    @LauncherAPI
    public Texture(String url, byte[] digest) {
        this.url = IOHelper.verifyURL(url);
        this.digest = Objects.requireNonNull(digest, "digest");
    }

    @LauncherAPI
    public Texture(String url, boolean cloak) throws IOException {
        this.url = IOHelper.verifyURL(url);

        // Fetch texture
        byte[] texture;
        try (InputStream input = IOHelper.newInput(new URL(url))) {
            texture = IOHelper.read(input);
        }
        try (ByteArrayInputStream input = new ByteArrayInputStream(texture)) {
            IOHelper.readTexture(input, cloak); // Verify texture
        }

        // Get digest of texture
        digest = SecurityHelper.digest(DIGEST_ALGO, new URL(url));
    }

    @LauncherAPI
    public Texture(HInput input) throws IOException {
        url = IOHelper.verifyURL(input.readASCII(2048));
        digest = input.readByteArray(-DIGEST_ALGO.bytes);
    }

    @Override
    public void write(HOutput output) throws IOException {
        output.writeASCII(url, 2048);
        output.writeByteArray(digest, -DIGEST_ALGO.bytes);
    }
}
