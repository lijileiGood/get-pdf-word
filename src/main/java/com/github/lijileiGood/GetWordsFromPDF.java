package com.github.lijileiGood;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class GetWordsFromPDF extends PDFTextStripper {

    static Map<String, Integer> wordMap = new TreeMap<>();

    public GetWordsFromPDF() throws IOException {
    }

    public static void main(String[] args) throws IOException {
//        String fileName = "d://abc.pdf";
        String fileName = "d://bcd.pdf";
        try (PDDocument document = PDDocument.load(new File(fileName))) {
            PDFTextStripper stripper = new GetWordsFromPDF();
            stripper.setSortByPosition(true);
            stripper.setStartPage(0);
            stripper.setEndPage(document.getNumberOfPages());

            Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
            stripper.writeText(document, dummy);

            List<Demo> list = new ArrayList<>(10000);
            for (String word : wordMap.keySet()) {
                list.add(new Demo(word, wordMap.get(word)));
            }

            ExcelWriter writer = ExcelUtil.getWriter("d://efg.xlsx");

            List<Map<String, Object>> rows = list.stream().map(item -> {
                Map<String, Object> maps = new HashMap<>();
                maps.put("name", item.getName());
                maps.put("aa", item.getValue());
                return maps;
            }).collect(Collectors.toList());
            writer.write(rows);
            writer.close();
        }
    }


    @Override
    protected void writeString(String str, List<TextPosition> textPositions) throws IOException {
        String[] wordsInStream = str.split(getWordSeparator());
        for (String word : wordsInStream) {
            word = word.replace(".", "")
                    .replace(",", "")
                    .replace(":", "")
                    .replace("/", "")
                    .replace("(", "")
                    .replace(")", "")
                    .replace("{", "")
                    .replace("}", "")
                    .replace(";", "")
                    .replace("!", "");
            wordMap.put(word, wordMap.getOrDefault(word, 0) + 1);
        }
    }


}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Demo {
    private String name;
    private Integer value;
}