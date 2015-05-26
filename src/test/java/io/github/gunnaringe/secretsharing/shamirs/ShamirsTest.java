package io.github.gunnaringe.secretsharing.shamirs;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import java.math.BigInteger;
import java.util.Set;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShamirsTest {

    private static final Logger LOG = LoggerFactory.getLogger(ShamirsTest.class);

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
    public void testOf() {
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
}
