package com.paperforge.engine;

import com.paperforge.dto.EncryptRequestDto;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class PdfSecurityEngine {

    public void encrypt(File pdfFile, EncryptRequestDto request, File outputFile) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            AccessPermission ap = new AccessPermission();
            ap.setCanPrint(request.isAllowPrinting());
            ap.setCanModify(request.isAllowModification());
            ap.setCanExtractContent(request.isAllowCopy());
            ap.setCanFillInForm(request.isAllowFormFilling());

            String ownerPassword = request.getOwnerPassword();
            if (ownerPassword == null || ownerPassword.isEmpty()) {
                ownerPassword = request.getUserPassword();
            }

            StandardProtectionPolicy spp = new StandardProtectionPolicy(
                    ownerPassword,
                    request.getUserPassword() != null ? request.getUserPassword() : "",
                    ap
            );

            spp.setEncryptionKeyLength(request.getKeyLength() == 256 ? 256 : 128);
            spp.setPermissions(ap);

            document.protect(spp);
            document.save(outputFile);
        }
    }

    public void decrypt(File pdfFile, String password, File outputFile) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfFile, password != null ? password : "")) {
            document.setAllSecurityToBeRemoved(true);
            document.save(outputFile);
        } catch (InvalidPasswordException e) {
            throw new IllegalArgumentException("Incorrect password provided for PDF decryption", e);
        }
    }
}
