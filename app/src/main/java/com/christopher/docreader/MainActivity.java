package com.christopher.docreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<String[]> openDocumentLauncher;

    private RecyclerView recyclerRecent;
    private TextView tvNoRecent;

    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnOpenDocument =
                findViewById(R.id.btnOpenDocument);

        recyclerRecent =
                findViewById(R.id.recyclerRecent);

        tvNoRecent =
                findViewById(R.id.tvNoRecent);

        recyclerRecent.setLayoutManager(
                new LinearLayoutManager(this)
        );

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
            openDocumentLauncher.launch(
                    new String[]{"*/*"}
            );
        });

        loadRecentDocuments();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (recyclerRecent != null) {
            loadRecentDocuments();
        }
    }

    private void loadRecentDocuments() {

        databaseExecutor.execute(() -> {

            AppDatabase database =
                    AppDatabase.getInstance(
                            getApplicationContext()
                    );

            List<RecentDocument> documents =
                    database
                            .recentDocumentDao()
                            .getAll();

            runOnUiThread(() -> {

                if (documents.isEmpty()) {

                    tvNoRecent.setVisibility(
                            View.VISIBLE
                    );

                    recyclerRecent.setVisibility(
                            View.GONE
                    );

                } else {

                    tvNoRecent.setVisibility(
                            View.GONE
                    );

                    recyclerRecent.setVisibility(
                            View.VISIBLE
                    );

                    RecentDocumentAdapter adapter =
                            new RecentDocumentAdapter(
                                    documents,
                                    this::openRecentDocument
                            );

                    recyclerRecent.setAdapter(
                            adapter
                    );
                }
            });
        });
    }

    private void openRecentDocument(
            RecentDocument document
    ) {

        DocumentTypeResolver.DocumentType type =
                DocumentTypeResolver.resolve(
                        document.getExtension(),
                        document.getMimeType()
                );

        Intent intent =
                new Intent(
                        this,
                        ViewerActivity.class
                );

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

    private void handleDocument(Uri uri) {

        try {
            getContentResolver()
                    .takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );

        } catch (SecurityException e) {

            android.util.Log.w(
                    "DocReader",
                    "Não foi possível manter permissão para URI: "
                            + uri,
                    e
            );
        }

        String fileName =
                getFileName(uri);

        String mimeType =
                getContentResolver().getType(uri);

        String extension =
                getFileExtension(fileName);

        RecentDocument document =
                new RecentDocument(
                        fileName,
                        uri.toString(),
                        mimeType,
                        extension,
                        System.currentTimeMillis(),
                        0f
                );

        databaseExecutor.execute(() -> {

            AppDatabase database =
                    AppDatabase.getInstance(
                            getApplicationContext()
                    );

            database
                    .recentDocumentDao()
                    .insert(document);
        });

        DocumentTypeResolver.DocumentType type =
                DocumentTypeResolver.resolve(
                        document.getExtension(),
                        document.getMimeType()
                );

        android.util.Log.d(
                "DocReader",
                "Nome: "
                        + document.getName()
                        + " | MIME: "
                        + document.getMimeType()
                        + " | Extensão: "
                        + document.getExtension()
                        + " | Tipo: "
                        + type
        );

        Intent intent =
                new Intent(
                        this,
                        ViewerActivity.class
                );

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

        if ("content".equals(
                uri.getScheme()
        )) {

            try (
                    android.database.Cursor cursor =
                            getContentResolver()
                                    .query(
                                            uri,
                                            null,
                                            null,
                                            null,
                                            null
                                    )
            ) {

                if (
                        cursor != null
                                && cursor.moveToFirst()
                ) {

                    int nameIndex =
                            cursor.getColumnIndex(
                                    android.provider
                                            .OpenableColumns
                                            .DISPLAY_NAME
                            );

                    if (nameIndex >= 0) {
                        fileName =
                                cursor.getString(
                                        nameIndex
                                );
                    }
                }
            }
        }

        if (fileName == null) {
            fileName =
                    uri.getLastPathSegment();
        }

        return fileName;
    }

    private String getFileExtension(
            String fileName
    ) {

        if (fileName == null) {
            return "";
        }

        int dotIndex =
                fileName.lastIndexOf('.');

        if (
                dotIndex == -1
                        || dotIndex
                        == fileName.length() - 1
        ) {
            return "";
        }

        return fileName
                .substring(
                        dotIndex + 1
                )
                .toLowerCase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        databaseExecutor.shutdown();
    }
}