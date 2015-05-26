package io.github.gunnaringe.secretsharing;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.Sets;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public final class Shamir {

    private static final int CERTAINTY = 200;
    private static final Random random = new SecureRandom();

    public Shamir() {}

    public static Result split(final int threshold, final int numberOfShares, final BigInteger prime, final byte[] secret) {
        final BigInteger secretInteger = new BigInteger(secret);
        return split(threshold, numberOfShares, prime, secretInteger);
    }

    public static Result split(final int threshold, final int numberOfShares, final byte[] secret) {
        final BigInteger secretInteger = new BigInteger(secret);
        final BigInteger prime = findPrime(secretInteger);
        return split(threshold, numberOfShares, prime, secretInteger);
    }


    private static Result split(final int threshold, final int numberOfShares, final BigInteger prime, final BigInteger secret) {
        checkArgument(threshold <= numberOfShares, "threshold must be less or equal to number of shares");
        checkArgument(threshold > 0, "threshold must be positive");
        checkArgument(numberOfShares > 0, "number of shares must be positive");
        requireNonNull(secret, "secret can not be null");

        final Map<Integer, BigInteger> coefficient = getCoefficients(threshold, prime);
        final Set<ShamirShare> shares = Sets.newConcurrentHashSet();
        IntStream.rangeClosed(1, numberOfShares).parallel().forEach(i -> {
            BigInteger accumulate = secret;
            for (int j = 1; j < threshold; j++) {
                final BigInteger t1 = BigInteger.valueOf(i).modPow(BigInteger.valueOf(j), prime);
                final BigInteger t2 = coefficient.get(j - 1).multiply(t1).mod(prime);
                accumulate = accumulate.add(t2).mod(prime);
            }
            shares.add(ImmutableShamirShare.builder().index(i - 1).value(accumulate).build());
        });
        return ImmutableResult.builder().addAllShares(shares).prime(prime).build();
    }

    public static byte[] combine(final BigInteger prime, final Set<ShamirShare> shares) {
        requireNonNull(prime, "prime can not be null");
        requireNonNull(shares, "shares can not be null");

        BigInteger accumulate = BigInteger.ZERO;
        for (final ShamirShare share : shares) {
            BigInteger num = BigInteger.ONE;
            BigInteger den = BigInteger.ONE;
            for (final ShamirShare otherShare : shares) {
                if (share.getIndex() == otherShare.getIndex()) { continue; }
                num = num.multiply(BigInteger.valueOf(-(otherShare.getIndex() + 1))).mod(prime);
                den = den.multiply(BigInteger.valueOf(share.getIndex() - otherShare.getIndex())).mod(prime);
            }
            final BigInteger tmp = share.getValue().multiply(num).multiply(den.modInverse(prime)).mod(prime);
            accumulate = accumulate.add(prime).add(tmp).mod(prime);
        }
        return accumulate.toByteArray();
    }

    private static Map<Integer, BigInteger> getCoefficients(final int threshold, final BigInteger prime) {
        final Map<Integer, BigInteger> map = new ConcurrentHashMap<>();
        IntStream
                .range(0, threshold - 1)
                .parallel()
                .forEach(i -> map.put(i, getPrimeBetweenZeroAndPrime(prime)));
        return map;
    }

    public static BigInteger findPrime(final BigInteger secretInteger) {
        return new BigInteger(secretInteger.bitLength() + 1, CERTAINTY, random);
    }

    private static BigInteger getPrimeBetweenZeroAndPrime(final BigInteger prime) {
        while (true) {
            // 0 < candidate < prime
            final BigInteger candidate = new BigInteger(prime.bitLength(), random);
            if (candidate.compareTo(BigInteger.ZERO) > 0 && candidate.compareTo(prime) < 0) {
                return candidate;
            }
        }
    }
}