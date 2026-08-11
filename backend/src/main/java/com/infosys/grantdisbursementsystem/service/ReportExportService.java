package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.dto.FundUtilizationDTO;
import com.infosys.grantdisbursementsystem.dto.RegionUtilizationDTO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Generic Excel/PDF table export (Module 4: "Support downloadable PDF and
 * Excel reports for scheme and regional summaries"). Kept format-agnostic
 * (title + headers + rows in, bytes out) so both the scheme-wise
 * (FundUtilizationDTO) and region-wise (RegionUtilizationDTO) summaries can
 * reuse the same table-rendering logic instead of duplicating it per report.
 */
@Service
public class ReportExportService {

    private static final DateTimeFormatter GENERATED_AT_FORMAT =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy");


    // ------------------------------------------------------------------
    // Scheme-wise (fund utilization) report
    // ------------------------------------------------------------------

    public byte[] fundUtilizationToExcel(List<FundUtilizationDTO> rows) {

        List<String> headers = List.of(
                "Scheme", "Total Amount", "Released Amount", "Remaining Amount"
        );

        List<List<String>> data = rows.stream()
                .map(r -> List.of(
                        nullSafe(r.getSchemeName()),
                        currency(r.getTotalAmount()),
                        currency(r.getReleasedAmount()),
                        currency(r.getRemainingAmount())
                ))
                .toList();

        return toExcel("Scheme-wise Fund Utilization", headers, data);
    }

    public byte[] fundUtilizationToPdf(List<FundUtilizationDTO> rows) {

        List<String> headers = List.of(
                "Scheme", "Total", "Released", "Remaining"
        );

        List<List<String>> data = rows.stream()
                .map(r -> List.of(
                        nullSafe(r.getSchemeName()),
                        currency(r.getTotalAmount()),
                        currency(r.getReleasedAmount()),
                        currency(r.getRemainingAmount())
                ))
                .toList();

        return toPdf("Scheme-wise Fund Utilization", headers, data);
    }


    // ------------------------------------------------------------------
    // Region-wise report
    // ------------------------------------------------------------------

    public byte[] regionUtilizationToExcel(List<RegionUtilizationDTO> rows) {

        List<String> headers = List.of("Region", "Total Amount Disbursed");

        List<List<String>> data = rows.stream()
                .map(r -> List.of(
                        nullSafe(r.getRegion()),
                        currency(r.getTotalAmount())
                ))
                .toList();

        return toExcel("Region-wise Disbursement Summary", headers, data);
    }

    public byte[] regionUtilizationToPdf(List<RegionUtilizationDTO> rows) {

        List<String> headers = List.of("Region", "Total Amount Disbursed");

        List<List<String>> data = rows.stream()
                .map(r -> List.of(
                        nullSafe(r.getRegion()),
                        currency(r.getTotalAmount())
                ))
                .toList();

        return toPdf("Region-wise Disbursement Summary", headers, data);
    }


    // ------------------------------------------------------------------
    // Generic Excel table renderer
    // ------------------------------------------------------------------

    private byte[] toExcel(String title, List<String> headers, List<List<String>> rows) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(safeSheetName(title));

            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            int rowIdx = 0;

            Row titleRow = sheet.createRow(rowIdx++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title);
            titleCell.setCellStyle(titleStyle);

            Row generatedRow = sheet.createRow(rowIdx++);
            generatedRow.createCell(0)
                    .setCellValue("Generated: " + LocalDate.now().format(GENERATED_AT_FORMAT));

            rowIdx++; // blank spacer row

            Row headerRow = sheet.createRow(rowIdx++);
            for (int c = 0; c < headers.size(); c++) {
                Cell cell = headerRow.createCell(c);
                cell.setCellValue(headers.get(c));
                cell.setCellStyle(headerStyle);
            }

            for (List<String> dataRow : rows) {
                Row row = sheet.createRow(rowIdx++);
                for (int c = 0; c < dataRow.size(); c++) {
                    row.createCell(c).setCellValue(dataRow.get(c));
                }
            }

            if (rows.isEmpty()) {
                sheet.createRow(rowIdx).createCell(0).setCellValue("No data available");
            }

            for (int c = 0; c < headers.size(); c++) {
                sheet.autoSizeColumn(c);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new UncheckedIOException("Failed to generate Excel report", e);
        }
    }


    // ------------------------------------------------------------------
    // Generic PDF table renderer (simple single/multi-page table, no
    // external table library - PDFBox only draws text/lines, so layout is
    // done manually with fixed column widths).
    // ------------------------------------------------------------------

    private byte[] toPdf(String title, List<String> headers, List<List<String>> rows) {

        float margin = 40f;
        float rowHeight = 20f;
        float titleFontSize = 16f;
        float headerFontSize = 11f;
        float cellFontSize = 10f;

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            float tableWidth = pageWidth - 2 * margin;
            float colWidth = tableWidth / headers.size();

            PDPageContentStream content = new PDPageContentStream(document, page);
            float y = pageHeight - margin;

            y = writeText(content, title, margin, y, titleFontSize, true);
            y -= 6;
            y = writeText(
                    content,
                    "Generated: " + LocalDate.now().format(GENERATED_AT_FORMAT),
                    margin, y, 9f, false
            );
            y -= 14;

            y = drawRow(content, headers, margin, y, colWidth, rowHeight, headerFontSize, true);

            for (List<String> row : rows) {

                if (y - rowHeight < margin) {
                    content.close();
                    PDPage nextPage = new PDPage(PDRectangle.A4);
                    document.addPage(nextPage);
                    content = new PDPageContentStream(document, nextPage);
                    y = pageHeight - margin;
                    y = drawRow(content, headers, margin, y, colWidth, rowHeight, headerFontSize, true);
                }

                y = drawRow(content, row, margin, y, colWidth, rowHeight, cellFontSize, false);
            }

            if (rows.isEmpty()) {
                y = writeText(content, "No data available", margin, y - rowHeight, cellFontSize, false);
            }

            content.close();

            document.save(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new UncheckedIOException("Failed to generate PDF report", e);
        }
    }

    private float writeText(
            PDPageContentStream content,
            String text,
            float x,
            float y,
            float fontSize,
            boolean bold
    ) throws IOException {

        content.beginText();
        content.setFont(
                bold ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA,
                fontSize
        );
        content.newLineAtOffset(x, y);
        content.showText(text == null ? "" : text);
        content.endText();

        return y - fontSize;
    }

    private float drawRow(
            PDPageContentStream content,
            List<String> cells,
            float x,
            float y,
            float colWidth,
            float rowHeight,
            float fontSize,
            boolean bold
    ) throws IOException {

        float cellX = x;

        for (String cell : cells) {

            content.beginText();
            content.setFont(
                    bold ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA,
                    fontSize
            );
            content.newLineAtOffset(cellX + 2, y - fontSize);
            content.showText(truncate(cell, colWidth, fontSize));
            content.endText();

            cellX += colWidth;
        }

        content.moveTo(x, y - rowHeight + 4);
        content.lineTo(x + colWidth * cells.size(), y - rowHeight + 4);
        content.setLineWidth(0.5f);
        content.stroke();

        return y - rowHeight;
    }

    // Rough character-count truncation so long scheme/region names don't
    // overrun their column - good enough for a simple tabular report
    // without pulling in full text-measurement/wrapping logic.
    private String truncate(String text, float colWidth, float fontSize) {

        if (text == null) {
            return "";
        }

        int maxChars = Math.max(4, (int) (colWidth / (fontSize * 0.55f)));

        return text.length() > maxChars
                ? text.substring(0, Math.max(1, maxChars - 3)) + "..."
                : text;
    }

    private String nullSafe(String value) {
        return value == null ? "-" : value;
    }

    private String currency(Double amount) {
        return amount == null
                ? "0.00"
                : String.format(Locale.US, "%,.2f", amount);
    }

    private String safeSheetName(String title) {
        // Excel sheet names can't exceed 31 chars or contain \ / ? * [ ]
        String cleaned = title.replaceAll("[\\\\/?*\\[\\]]", "");
        return cleaned.length() > 31 ? cleaned.substring(0, 31) : cleaned;
    }
}