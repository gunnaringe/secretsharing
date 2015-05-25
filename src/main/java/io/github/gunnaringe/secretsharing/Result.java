package io.github.gunnaringe.secretsharing;

import java.math.BigInteger;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
public interface Result {

    List<ShamirShare> getShares();
    BigInteger getPrime();
}
