/*
 * Copyright 2018-2026 Pavel Castornii.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.techsenger.alpha.demo.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author Pavel Castornii
 */
public class MavenCertificateLoader {

   public static void main(String[] args) {
        try {
            if (args.length != 2) {
                System.err.println("Usage: java MavenCertificateLoader <repository_url> <output_path>");
                return;
            }
            String repositoryUrl = args[0];
            String outputPath = args[1];
            var loader = new MavenCertificateLoader();
            loader.run(repositoryUrl, outputPath);
        } catch (Exception e) {
            e.printStackTrace();
            //this loader is executed on the thread of the plugin,
            //so, using system.exit we kill it
            System.exit(1);
        }
    }

    private void run(String repositoryUrl, String outputPath) throws Exception {
        X509Certificate certificate = downloadCertificate(repositoryUrl);
        saveCertificateToFile(certificate, outputPath);
        System.out.println("Certificate saved successfully to " + outputPath);
    }

    private X509Certificate downloadCertificate(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.connect();
        Certificate[] certs = connection.getServerCertificates();
        for (Certificate cert : certs) {
            if (cert instanceof X509Certificate) {
                return (X509Certificate) cert;
            }
        }
        throw new Exception("No X.509 certificate found for " + urlString);
    }

    private void saveCertificateToFile(X509Certificate certificate, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            byte[] encodedCert = certificate.getEncoded();
            fos.write(encodedCert);
        } catch (Exception e) {
            throw new IOException("Failed to save certificate: " + e.getMessage(), e);
        }
    }
}
