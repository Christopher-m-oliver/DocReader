package com.christopher.docreader;

public class DocumentTypeResolver {

    public enum DocumentType {
        TEXT,
        MARKDOWN,
        PDF,
        EPUB,
        ODT,
        XML,
        UNSUPPORTED
    }

    public static DocumentType resolve(String extension, String mimeType) {

        if (extension == null) {
            extension = "";
        }

        extension = extension.toLowerCase();

        switch (extension) {
            case "txt":
                return DocumentType.TEXT;

            case "md":
            case "markdown":
                return DocumentType.MARKDOWN;

            case "pdf":
                return DocumentType.PDF;

            case "epub":
                return DocumentType.EPUB;

            case "odt":
                return DocumentType.ODT;

            case "xml":
                return DocumentType.XML;
        }

        if (mimeType != null) {

            if (mimeType.equals("text/plain")) {
                return DocumentType.TEXT;
            }

            if (mimeType.equals("application/pdf")) {
                return DocumentType.PDF;
            }

            if (mimeType.equals("application/epub+zip")) {
                return DocumentType.EPUB;
            }

            if (mimeType.equals("application/xml") ||
                    mimeType.equals("text/xml")) {
                return DocumentType.XML;
            }
        }

        return DocumentType.UNSUPPORTED;
    }
}