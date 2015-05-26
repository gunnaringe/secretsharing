package io.github.gunnaringe.secretsharing.shamirs;

import static com.google.common.io.Resources.getResource;
import static com.google.common.io.Resources.toByteArray;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.Set;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShamirsTest {

    private static final Logger LOG = LoggerFactory.getLogger(ShamirsTest.class);
    private static final Random RANDOM = new Random();

    @Test
    public void findPrime() {
        final int size = 16;
        final Stopwatch stopWatch = Stopwatch.createStarted();
        final BigInteger prime = Shamirs.findPrime(Strings.repeat("x", size).getBytes(UTF_8));
        LOG.debug("Time elapsed: {} ms", stopWatch.elapsed(MILLISECONDS));
        LOG.info("Prime: {}", prime);
        assertThat(prime.bitLength()).isGreaterThanOrEqualTo(16 * 8);
    }

    @Test
    public void testGeneratedPrime() {
        final int threshold = 4;
        final int numberOfShares = 8;
        final byte[] secret = "a typical secret".getBytes(UTF_8);

        final Stopwatch stopWatch = Stopwatch.createStarted();
        final BigInteger prime = Shamirs.findPrime(secret);
        final Set<String> shares = Shamirs.prime(prime).split(threshold, numberOfShares, secret);
        byte[] combined = Shamirs.prime(prime).combine(shares);
        stopWatch.stop();

        assertThat(combined).isEqualTo(secret);
        LOG.debug("Time elapsed: {} ms", stopWatch.elapsed(MILLISECONDS));
    }

    @Test
    public void test128() {
        final int threshold = 3;
        final int numberOfShares = 5;
        final byte[] secret = "secret".getBytes(UTF_8);
        final Set<String> shares = Shamirs.shamirs128().split(threshold, numberOfShares, secret);
        assertThat(Shamirs.shamirs128().combine(shares)).isEqualTo(secret);
    }

    @Test
    public void test256() {
        final int threshold = 3;
        final int numberOfShares = 5;
        final byte[] secret = "secret".getBytes(UTF_8);
        final Set<String> shares = Shamirs.shamirs256().split(threshold, numberOfShares, secret);
        assertThat(Shamirs.shamirs256().combine(shares)).isEqualTo(secret);
    }

    @Test
    public void test512() {
        final int threshold = 4;
        final int numberOfShares = 8;
        final byte[] secret = "a typical secret".getBytes(UTF_8);

        final Stopwatch stopWatch = Stopwatch.createStarted();
        final Set<String> shares = Shamirs.shamirs512().split(threshold, numberOfShares, secret);
        byte[] combined = Shamirs.shamirs512().combine(shares);

        assertThat(combined).isEqualTo(secret);
        LOG.debug("Time elapsed: {} ms", stopWatch.elapsed(MILLISECONDS));
    }

    @Test
    public void test1024() {
        final int threshold = 9;
        final int numberOfShares = 10;
        final byte[] secret = Strings.repeat("x", 1024).getBytes(UTF_8);

        final Stopwatch stopWatch = Stopwatch.createStarted();
        final Set<String> shares = Shamirs.shamirs1024().split(threshold, numberOfShares, secret);
        byte[] combined = Shamirs.shamirs1024().combine(shares);

        assertThat(combined).isEqualTo(secret);
        LOG.debug("Time elapsed: {} ms", stopWatch.elapsed(MILLISECONDS));
    }

    @Test
    public void testFromFile() throws IOException {
        // Secret with threshold=30, shares=40
        final byte[] secret = toByteArray(getResource("01-secret"));
        final Stopwatch stopWatch = Stopwatch.createStarted();
        assertThat(Shamirs.shamirs512().combine(readShares("01-shares-all"))).isEqualTo(secret);
        assertThat(Shamirs.shamirs512().combine(readShares("01-shares-threshold"))).isEqualTo(secret);
        assertThat(Shamirs.shamirs512().combine(readShares("01-shares-below-threshold"))).isNotEqualTo(secret);
        LOG.debug("Time elapsed: {} ms", stopWatch.elapsed(MILLISECONDS));
    }

    private static Set<String> readShares(final String resource) throws IOException {
        return ImmutableSet.copyOf(
                Splitter.on('\n')
                        .omitEmptyStrings()
                        .trimResults()
                        .split(Resources.toString(getResource(resource), UTF_8)));
    }
}
