package io.github.gunnaringe.secretsharing;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
public interface Result {

    Set<ShamirShare> getShares();
    BigInteger getPrime();
}
