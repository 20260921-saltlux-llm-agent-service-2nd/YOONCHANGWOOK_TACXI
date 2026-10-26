package app.controller;

import app.repository.TG_TabelGenRepository;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class TG_TableCsvController {
    private final TG_TabelGenRepository tgRepo = new TG_TabelGenRepository();

    public boolean readCsv(String path) {
        // 파일 읽기
        try {
            InputStream tgTableCsv = new FileInputStream(path);
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        tgRepo
    }
}
