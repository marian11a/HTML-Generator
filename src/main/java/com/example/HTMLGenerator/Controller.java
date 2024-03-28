package com.example.HTMLGenerator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@RestController
public class Controller {

    @PostMapping("/list-files")
    @ResponseBody
    public List<String> listFiles() {
        List<String> fileList = new ArrayList<>();
        File folder = new File("src/main/resources/generated");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".html"));
        if (files != null) {
            Arrays.sort(files, Comparator.comparingInt(this::extractNumberFromFileName));

            for (File file : files) {
                fileList.add(file.getName());
            }
        }
        return fileList;
    }

    @PostMapping("/generate-html")
    public ResponseEntity<String> generateHTMLFile(@RequestBody String prompt) {
        try {
            int count = getCount();
            String generatedText = new Generator().generate(prompt);

            String filePath = "src/main/resources/generated/(" + count + ").html";
            FileWriter writer = new FileWriter(filePath);

            writer.write(generatedText);
            writer.close();

            return new ResponseEntity<>("HTML file generated successfully", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to generate HTML file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/open-html/{fileName}")
    public ResponseEntity<byte[]> openHtmlFile(@PathVariable String fileName) {
        fileName = fileName.replace(".html", "");
        String filePath = "src/main/resources/generated/" + fileName + ".html";

        File file = new File(filePath);
        if (!file.exists() || !filePath.toLowerCase().endsWith(".html")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return ResponseEntity.ok().body(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private int getCount() {
        List<String> fileList = new ArrayList<>();
        File folder = new File("src/main/resources/generated");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".html"));
        if (files != null) {
            for (File file : files) {
                fileList.add(file.getName());
            }
        }
        return fileList.size() + 1;
    }

    private int extractNumberFromFileName(File file) {
        String fileName = file.getName();

        int startIndex = fileName.indexOf('(');
        int endIndex = fileName.indexOf(')');
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            String numberStr = fileName.substring(startIndex + 1, endIndex);
            try {
                return Integer.parseInt(numberStr);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}