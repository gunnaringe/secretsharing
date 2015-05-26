package io.github.gunnaringe.secretsharing;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import java.math.BigInteger;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShamirUtilsTest {

    private static final Logger LOG = LoggerFactory.getLogger(ShamirUtilsTest.class);

    @Test
    public void splitAndCombine() {
        final byte[] secret = Strings.repeat("x", 512).getBytes(UTF_8);

        final Stopwatch stopWatch = Stopwatch.createStarted();
        Result result = ShamirUtils.split(19, 20, secret);
        final byte[] combined = ShamirUtils.combine(result.getShares());
        stopWatch.stop();

        assertThat(combined).isEqualTo(secret);
        LOG.debug("Time elapsed: {} ms", stopWatch.elapsed(MILLISECONDS));
    }

    @Test
    public void shareToAndFromStringRepresentation() {
        final ShamirShare shamirShare
                = ImmutableShamirShare.builder().index(13).value(new BigInteger("1853045854103335027193")).build();
        final String stringRepresentation = ShamirUtils.toString(shamirShare);

        // String format {index}:{base64 encoding of bigInteger}
        final ShamirShare shamirFromString = ShamirUtils.valueOf(stringRepresentation);
        assertThat(shamirFromString.getIndex()).isEqualTo(13);
        assertThat(shamirFromString.getValue()).isEqualTo(new BigInteger("1853045854103335027193"));
    }
}