package io.github.gunnaringe.secretsharing;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.io.BaseEncoding.base64Url;
import static com.google.common.io.Resources.getResource;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.io.Resources;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ShamirUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ShamirUtils.class);
    private static final BigInteger PRIME_4096 = loadPrime();

    private static BigInteger loadPrime() {
        try {
            return new BigInteger(Resources.toString(getResource("prime4096"), UTF_8));
        } catch (final IOException e) {
            LOG.warn("Could not load prime from file");
            return null;
        }
    }

    private ShamirUtils() {}

    public static Result split(final int threshold, final int numberOfShares, final byte[] secret) {
        checkArgument(secret.length <= 512, "Secret must be below 512 bytes");
        return Shamir.split(threshold, numberOfShares, PRIME_4096, secret);
    }

    public static byte[] combine(final List<ShamirShare> shares) {
        return Shamir.combine(PRIME_4096, shares);
    }

    public static ShamirShare valueOf(final String value) {
        final String[] values = value.split(":", 2);
        final int index = Integer.parseInt(values[0]);
        final byte[] bytes = base64Url().omitPadding().decode(values[1]);
        return ImmutableShamirShare.builder().index(index).value(new BigInteger(bytes)).build();
    }

    public static String toString(final ShamirShare share) {
        final String value = base64Url().omitPadding().encode(share.getValue().toByteArray());
        return Integer.toString(share.getIndex()) + ":" + value;
    }
}