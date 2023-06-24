package com.pdfconvert.converter.controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.convert.out.pdf.PdfConversion;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;

@Controller
public class FileConverterController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/image-to-pdf")
    public void convertImageToPdf(@RequestParam("image") MultipartFile image, HttpServletResponse response) throws IOException {
        if (image.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No image uploaded.");
            return;
        }

        try (PDDocument document = new PDDocument()) {
            byte[] imageBytes = image.getBytes();

            // Load the image using PDFBox
            PDImageXObject imageXObject = PDImageXObject.createFromByteArray(document, imageBytes, image.getOriginalFilename());

            // Calculate the aspect ratio of the image
            int imageWidth = imageXObject.getWidth();
            int imageHeight = imageXObject.getHeight();
            float aspectRatio = (float) imageWidth / imageHeight;

            // Determine the dimensions of the PDF page based on the aspect ratio
            float pageWidth = 595; // Adjust as needed
            float pageHeight = pageWidth / aspectRatio;

            // Create a new page with the calculated dimensions
            PDPage page = new PDPage(new PDRectangle(pageWidth, pageHeight));
            document.addPage(page);

            // Draw the image onto the page
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(imageXObject, 0, 0, pageWidth, pageHeight);
            }

            // Set the response content type and headers
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "attachment; filename=converted.pdf");

            // Save the PDF document to the response output stream
            document.save(response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            response.setContentType("text/plain");
            response.getWriter().write("Error occurred during image-to-PDF conversion: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
