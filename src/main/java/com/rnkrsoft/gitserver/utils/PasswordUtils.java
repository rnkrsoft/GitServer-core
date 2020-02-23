package com.rnkrsoft.gitserver.utils;

import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.util.encoders.Hex;

public class PasswordUtils {
    public static String generateSha1(String password) {
        byte[] dataBytes = password.getBytes();

        SHA1Digest sha1 = new SHA1Digest();
        sha1.reset();
        sha1.update(dataBytes, 0, dataBytes.length);

        int outputSize = sha1.getDigestSize();
        byte[] dataDigest = new byte[outputSize];

        sha1.doFinal(dataDigest, 0);

        String dataSha1 = new String(Hex.encode(dataDigest));

        return dataSha1;
    }
}
