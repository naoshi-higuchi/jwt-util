package org.nopware.jwt_util.psalg;

import javax.annotation.Nullable;
import java.security.PrivateKey;
import java.security.PublicKey;

public class PS384 extends PS {
    public PS384(@Nullable PublicKey publicKey, @Nullable PrivateKey privateKey) {
        super(384, publicKey, privateKey);
    }
}
