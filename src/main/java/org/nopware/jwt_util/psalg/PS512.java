package org.nopware.jwt_util.psalg;

import javax.annotation.Nullable;
import java.security.PrivateKey;
import java.security.PublicKey;

public class PS512 extends PS {
    public PS512(@Nullable PublicKey publicKey, @Nullable PrivateKey privateKey) {
        super(512, publicKey, privateKey);
    }
}
