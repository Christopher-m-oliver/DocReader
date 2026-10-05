package com.christopher.docreader;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ViewerActivity extends AppCompatActivity {

    private TextView tvFileName;
    private TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        tvFileName = findViewById(R.id.tvFileName);
        tvContent = findViewById(R.id.tvContent);

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
        } else {
            tvContent.setText(
                    "Visualização deste formato ainda não implementada."
            );
        }
    }

    private void openTextDocument(Uri uri) {
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
            tvContent.setText(
                    "Erro ao abrir o documento."
            );
        }
    }
}