package com.uni.packetimport;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.contentstream.operator.graphics.AppendRectangleToPath;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class PDFStyleStripper extends PDFTextStripper {

    HashSet<String> boldFonts = new HashSet<>();

    public PDFStyleStripper(PDDocument doc) throws IOException {
        super();
        for (int i = 0; i < doc.getNumberOfPages(); ++i) {
            PDPage page = doc.getPage(i);
            PDResources res = page.getResources();
            for (COSName fontName : res.getFontNames()) {
                PDFont font = res.getFont(fontName);
                if (font.getName().toLowerCase().contains("bold")) {
                    boldFonts.add(font.getName());
                }
            }
        }
    }

    boolean can_bold = false;

    @Override
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
        for (TextPosition textPosition : textPositions) {
            boolean displayable = textPosition.toString().trim().length() > 0;
            if (displayable && boldFonts.contains(textPosition.getFont().getName())) {
                if (can_bold) {
                    output.write("$B$");
                    can_bold = false;
                }
            } else if (!can_bold) {
                output.write("$/B$");
                can_bold = true;
            }
            output.write(textPosition.toString());
        }
    }
}