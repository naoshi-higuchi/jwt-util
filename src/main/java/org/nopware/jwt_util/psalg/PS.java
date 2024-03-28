package org.nopware.jwt_util.psalg;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureGenerationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.nopware.jwt_util.Header;
import org.nopware.jwt_util.IOUtil;

import javax.annotation.Nullable;
import java.security.*;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.Objects;

class PS extends Algorithm {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private final int TRAILER_FIELD = 1; // It must be 1. (by RFC 4055)

    @Nullable
    private final PublicKey publicKey;

    @Nullable
    private final PrivateKey privateKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final int bits;
    protected PS(int bits,
                 @Nullable PublicKey publicKey,
                 @Nullable PrivateKey privateKey) {
        super(name(bits), description(bits));
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.bits = bits;
    }

    private static String name(int bits) {
        return String.format("PS%d", bits);
    }

    private static String description(int bits) {
        return String.format("RSA-PSS using SHA-%d and MGF1 with SHA-%d", bits, bits);
    }

    private Header headerOf(DecodedJWT decodedJWT) throws JsonProcessingException {
        String headerStr = new String(IOUtil.base64Decode(decodedJWT.getHeader()));
        return objectMapper.readValue(headerStr, Header.class);
    }

    private void checkAlgorithm(DecodedJWT decodedJWT) throws RuntimeException {
        try {
            Header header = headerOf(decodedJWT);
            String alg = header.getAlg();
            if (!Objects.equals(alg, name(bits))) {
                throw new RuntimeException(String.format("Expected algorithm %s, but got %s", name(bits), alg));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void verify(DecodedJWT decodedJWT) throws SignatureVerificationException {
        try {
            checkAlgorithm(decodedJWT);

            String headerAndPayload = decodedJWT.getHeader() + "." + decodedJWT.getPayload();
            byte[] data = headerAndPayload.getBytes();
            byte[] signature = IOUtil.base64Decode(decodedJWT.getSignature());

            if (!doVerify(data, signature)) {
                throw new SignatureVerificationException(this);
            }
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException |
                 SignatureException | InvalidKeyException e) {
            throw new SignatureVerificationException(this, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] sign(byte[] bytes) throws SignatureGenerationException {
        try {
            return doSign(bytes);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | SignatureException e) {
            throw new SignatureGenerationException(this, e);
        }
    }

    private String algorithm() {
        return String.format("SHA%dwithRSA/PSS", bits);
    }

    private String mdName() {
        return String.format("SHA-%d", bits);
    }

    private int saltLength() {
        return bits / 8; // The recommended value is the number of octets in the hash value. (by RFC 4055)
    }

    private MGF1ParameterSpec mgf1ParameterSpec() throws NoSuchAlgorithmException {
        return switch (bits) {
            case 256 -> MGF1ParameterSpec.SHA256;
            case 384 -> MGF1ParameterSpec.SHA384;
            case 512 -> MGF1ParameterSpec.SHA512;
            default -> throw new NoSuchAlgorithmException("Unsupported bits");
        };
    }

    private PSSParameterSpec pssParameterSpec() throws NoSuchAlgorithmException {
        return new PSSParameterSpec(mdName(), "MGF1", mgf1ParameterSpec(), saltLength(), TRAILER_FIELD);
    }

    // doSign is package-private for testing.
    byte[] doSign(byte[] data) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, SignatureException {
        Signature signatureSpi = Signature.getInstance(algorithm());
        signatureSpi.setParameter(pssParameterSpec());

        signatureSpi.initSign(privateKey);
        signatureSpi.update(data);

        return signatureSpi.sign();
    }

    // doVerify is package-private for testing.
    boolean doVerify(byte[] data, byte[] signature) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, SignatureException {
        Signature signatureSpi = Signature.getInstance(algorithm());
        signatureSpi.setParameter(pssParameterSpec());

        signatureSpi.initVerify(publicKey);
        signatureSpi.update(data);

        return signatureSpi.verify(signature);
    }
}
