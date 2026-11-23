package com.christopher.docreader;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ext.SdkExtensions;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.pdf.viewer.fragment.PdfViewerFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.noties.markwon.Markwon;

public class ViewerActivity extends AppCompatActivity {

    private TextView tvFileName;
    private TextView tvContent;
    private ScrollView textViewer;
    private View pdfViewerContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        initializeViews();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        String uriString = getIntent().getStringExtra("document_uri");
        String fileName = getIntent().getStringExtra("document_name");
        String documentType = getIntent().getStringExtra("document_type");

        tvFileName.setText(fileName);

        if (uriString == null) {
            tvContent.setText("Não foi possível acessar o documento.");
            return;
        }

        Uri uri = Uri.parse(uriString);

        if ("TEXT".equals(documentType)) {
            openTextDocument(uri);
        } else if ("MARKDOWN".equals(documentType)) {
            openMarkdownDocument(uri);
        } else if ("PDF".equals(documentType)) {
            openPdfDocument(uri);
        } else {
            tvContent.setText(
                    "Visualização deste formato ainda não implementada."
            );
        }
    }

    private void initializeViews() {
        tvFileName = findViewById(R.id.tvFileName);
        tvContent = findViewById(R.id.tvContent);
        textViewer = findViewById(R.id.textViewer);
        pdfViewerContainer = findViewById(R.id.pdfViewerContainer);
    }

    private void openPdfDocument(Uri uri) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                SdkExtensions.getExtensionVersion(Build.VERSION_CODES.S) < 13) {

            pdfViewerContainer.setVisibility(View.GONE);
            textViewer.setVisibility(View.VISIBLE);

            tvContent.setText(
                    "A visualização de PDF exige Android 12 com SDK Extension 13 ou superior."
            );
            return;
        }

        textViewer.setVisibility(View.GONE);
        pdfViewerContainer.setVisibility(View.VISIBLE);

        PdfViewerFragment fragment = new PdfViewerFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.pdfViewerContainer, fragment)
                .commitNow();

        fragment.setDocumentUri(uri);
    }

    private void openMarkdownDocument(Uri uri) {
        textViewer.setVisibility(View.VISIBLE);
        pdfViewerContainer.setVisibility(View.GONE);

        try (
                InputStream inputStream =
                        getContentResolver().openInputStream(uri);

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(inputStream)
                        )
        ) {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            Markwon markwon = Markwon.create(this);

            markwon.setMarkdown(
                    tvContent,
                    content.toString()
            );

        } catch (IOException | NullPointerException e) {
            tvContent.setText("Erro ao abrir o documento Markdown.");
        }
    }

    private void openTextDocument(Uri uri) {
        textViewer.setVisibility(View.VISIBLE);
        pdfViewerContainer.setVisibility(View.GONE);

        try (
                InputStream inputStream =
                        getContentResolver().openInputStream(uri);

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(inputStream)
                        )
        ) {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            tvContent.setText(content.toString());

        } catch (IOException | NullPointerException e) {
            tvContent.setText("Erro ao abrir o documento.");
        }
    }
}