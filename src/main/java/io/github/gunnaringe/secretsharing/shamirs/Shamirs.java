package io.github.gunnaringe.secretsharing.shamirs;

import static com.google.common.base.Preconditions.checkArgument;
import static io.github.gunnaringe.secretsharing.shamirs.Utils.loadBigInteger;

import io.github.gunnaringe.secretsharing.Result;
import io.github.gunnaringe.secretsharing.Shamir;
import io.github.gunnaringe.secretsharing.ShamirShare;
import java.math.BigInteger;
import java.util.Set;

public class Shamirs {

    private final BigInteger prime;
    private final int limit;

    private Shamirs(final BigInteger prime, final int limit) {
        this.prime = prime;
        this.limit = limit;
    }

    public static Shamirs of(final BigInteger prime) {
        return new Shamirs(prime, -1);
    }

    public static BigInteger findPrime(final byte[] secret) {
        return Shamir.findPrime(new BigInteger(secret));
    }

    // TODO(gunnaringe): Do not create new instances more than once
    public static Shamirs shamirs128() {
        return new Shamirs(loadBigInteger("prime128"), 128);
    }

    public static Shamirs shamirs256() {
        return new Shamirs(loadBigInteger("prime256"), 256);
    }

    public static Shamirs shamirs512() {
        return new Shamirs(loadBigInteger("prime512"), 512);
    }

    public static Shamirs shamirs1024() {
        return new Shamirs(loadBigInteger("prime1024"), 1024);
    }

    public Set<String> split(final int threshold, final int numberOfShares, final byte[] secret) {
        if (limit > 0) { checkArgument(secret.length <= limit, "Secret size must be less or equal to {%d} bytes", limit); }
        final Result result = Shamir.split(threshold, numberOfShares, prime, secret);
        return Utils.toStringRepresentation(result.getShares());
    }

    public byte[] combine(final Set<String> shares) {
        final Set<ShamirShare> shamirShares = Utils.fromStringRepresentation(shares);
        return Shamir.combine(prime, shamirShares);
    }
}
