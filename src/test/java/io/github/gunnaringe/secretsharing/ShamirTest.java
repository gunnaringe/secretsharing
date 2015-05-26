package io.github.gunnaringe.secretsharing;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import java.math.BigInteger;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShamirTest {

    private static final Logger LOG = LoggerFactory.getLogger(ShamirTest.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void splitAndCombine() {
        final byte[] secret = "This is my secret".getBytes(UTF_8);
        final int threshold = 11;
        final int numShares = 20;

        final Stopwatch stopWatch = Stopwatch.createStarted();
        final Result result = Shamir.split(threshold, numShares, secret);
        final byte[] combined = Shamir.combine(result.getPrime(), result.getShares());
        stopWatch.stop();

        assertThat(combined).isEqualTo(secret);
        LOG.debug("Time elapsed: {} ms", stopWatch.elapsed(MILLISECONDS));
    }

    @Test
    public void bigInput() {
        final byte[] secret = Strings.repeat("0123456789", 10).getBytes(UTF_8);
        final int threshold = 9;
        final int numShares = 10;

        final Stopwatch stopWatch = Stopwatch.createStarted();
        final Result result = Shamir.split(threshold, numShares, secret);
        final byte[] combined = Shamir.combine(result.getPrime(), result.getShares());
        stopWatch.stop();

        assertThat(combined).isEqualTo(secret);
        LOG.debug("Time elapsed: {} ms", stopWatch.elapsed(MILLISECONDS));
    }

    @Test
    public void combineWithThresholdValues() {
        final Stopwatch stopWatch = Stopwatch.createStarted();
        Set<ShamirShare> shares = ImmutableSet.of(
                ImmutableShamirShare.builder().index(3).value(new BigInteger("156585161940314957626")).build(),
                ImmutableShamirShare.builder().index(0).value(new BigInteger("2127494947956767830687")).build(),
                ImmutableShamirShare.builder().index(1).value(new BigInteger("1853045854103335027193")).build()
        );
        final BigInteger prime = new BigInteger("3802326154978466566103");
        final byte[] combined = Shamir.combine(prime, shares);
        stopWatch.stop();

        assertThat(combined).isEqualTo("my secret".getBytes(UTF_8));
        LOG.debug("Time elapsed: {} ms", stopWatch.elapsed(MILLISECONDS));
    }

    @Test
    public void combineWithLessThanThresholdValues() {
        final Stopwatch stopWatch = Stopwatch.createStarted();
        Set<ShamirShare> shares = ImmutableSet.of(
                ImmutableShamirShare.builder().index(3).value(new BigInteger("156585161940314957626")).build(),
                ImmutableShamirShare.builder().index(1).value(new BigInteger("1853045854103335027193")).build()
        );
        final BigInteger prime = new BigInteger("3802326154978466566103");
        final byte[] combined = Shamir.combine(prime, shares);
        stopWatch.stop();

        assertThat(combined).isNotEqualTo("my secret".getBytes(UTF_8));
        LOG.debug("Time elapsed: {} ms", stopWatch.elapsed(MILLISECONDS));
    }

    @Test
    public void combineWithDuplicateEntries() {
        final Stopwatch stopWatch = Stopwatch.createStarted();
        Set<ShamirShare> shares = ImmutableSet.of(
                ImmutableShamirShare.builder().index(3).value(new BigInteger("156585161940314957626")).build(),
                ImmutableShamirShare.builder().index(0).value(new BigInteger("2127494947956767830687")).build(),
                ImmutableShamirShare.builder().index(1).value(new BigInteger("1853045854103335027193")).build(),
                ImmutableShamirShare.builder().index(1).value(new BigInteger("1853045854103335027193")).build()
        );
        final BigInteger prime = new BigInteger("3802326154978466566103");
        final byte[] combined = Shamir.combine(prime, shares);
        stopWatch.stop();

        assertThat(combined).isEqualTo("my secret".getBytes(UTF_8));
        LOG.debug("Time elapsed: {} ms", stopWatch.elapsed(MILLISECONDS));
    }

    @Test
    public void checkSplitArguments() {
        expectedException.expect(IllegalArgumentException.class);
        Shamir.split(3, 2, "secret".getBytes(UTF_8));
    }
}
