package io.github.gunnaringe.secretsharing;

import java.math.BigInteger;
import org.immutables.value.Value;

@Value.Immutable
public interface ShamirShare {
    int getIndex();
    BigInteger getValue();
}
