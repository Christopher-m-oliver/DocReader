package com.christopher.docreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<String[]> openDocumentLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnOpenDocument = findViewById(R.id.btnOpenDocument);

        openDocumentLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.OpenDocument(),
                        uri -> {
                            if (uri != null) {
                                handleDocument(uri);
                            }
                        }
                );

        btnOpenDocument.setOnClickListener(v -> {
            openDocumentLauncher.launch(new String[]{"*/*"});
        });
    }

    private void handleDocument(Uri uri) {
        String fileName = getFileName(uri);
        String mimeType = getContentResolver().getType(uri);
        String extension = getFileExtension(fileName);

        RecentDocument document = new RecentDocument(
                fileName,
                uri.toString(),
                mimeType,
                extension,
                System.currentTimeMillis(),
                0f
        );

        DocumentTypeResolver.DocumentType type =
                DocumentTypeResolver.resolve(
                        document.getExtension(),
                        document.getMimeType()
                );

        android.util.Log.d(
                "DocReader",
                "Nome: " + document.getName()
                        + " | MIME: " + document.getMimeType()
                        + " | Extensão: " + document.getExtension()
                        + " | Tipo: " + type
        );

        Intent intent =
                new Intent(this, ViewerActivity.class);

        intent.putExtra(
                "document_uri",
                document.getUri()
        );

        intent.putExtra(
                "document_name",
                document.getName()
        );

        intent.putExtra(
                "document_type",
                type.name()
        );

        startActivity(intent);
    }

    private String getFileName(Uri uri) {
        String fileName = null;

        if ("content".equals(uri.getScheme())) {
            try (android.database.Cursor cursor = getContentResolver().query(
                    uri,
                    null,
                    null,
                    null,
                    null
            )) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(
                            android.provider.OpenableColumns.DISPLAY_NAME
                    );

                    if (nameIndex >= 0) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            }
        }

        if (fileName == null) {
            fileName = uri.getLastPathSegment();
        }

        return fileName;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }

        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(dotIndex + 1).toLowerCase();
    }
}