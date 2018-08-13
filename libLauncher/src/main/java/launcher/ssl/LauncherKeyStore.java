package launcher.ssl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class LauncherKeyStore {
    public static KeyStore getKeyStore(String keystore,String password) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance("JKS");
        InputStream ksIs = new FileInputStream(keystore);
        try {
            ks.load(ksIs, password.toCharArray());
        } finally {
            if (ksIs != null) {
                ksIs.close();
            }
        }
        return ks;
    }
}
