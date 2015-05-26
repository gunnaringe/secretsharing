package io.github.gunnaringe.secretsharing.shamirs;

import static com.google.common.io.BaseEncoding.base64Url;
import static com.google.common.io.Resources.getResource;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.io.Resources;
import io.github.gunnaringe.secretsharing.ImmutableShamirShare;
import io.github.gunnaringe.secretsharing.ShamirShare;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Set;
import java.util.stream.Collectors;

/** Package-Private */
final class Utils {

    static BigInteger loadBigInteger(final String resource) {
        try {
            return new BigInteger(Resources.toString(getResource(resource), UTF_8));
        } catch (final IOException e) {
            throw new RuntimeException("Could not load prime: " + resource, e);
        }
    }

    private Utils() {}

    static ShamirShare fromStringRepresentation(final String value) {
        final String[] values = value.split(":", 2);
        final int index = Integer.parseInt(values[0]);
        final byte[] bytes = base64Url().omitPadding().decode(values[1]);
        return ImmutableShamirShare.builder().index(index).value(new BigInteger(bytes)).build();
    }

    static Set<ShamirShare> fromStringRepresentation(final Set<String> values) {
        return values.stream().map(Utils::fromStringRepresentation).collect(Collectors.toSet());
    }

    static String toStringRepresentation(final ShamirShare share) {
        final String value = base64Url().omitPadding().encode(share.getValue().toByteArray());
        return Integer.toString(share.getIndex()) + ":" + value;
    }

    static Set<String> toStringRepresentation(final Set<ShamirShare> shares) {
        return shares
                .stream()
                .map(Utils::toStringRepresentation)
                .collect(Collectors.toSet());
    }
}
