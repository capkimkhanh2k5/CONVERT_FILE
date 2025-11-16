package com.convertfile.service.microService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.File;
import java.util.Map;

public class csv_to_json_service {
    public void convertCsvToJson(String csvPath, String jsonPath) throws Exception {
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        ObjectMapper jsonMapper = new ObjectMapper();
        var readAll = csvMapper.readerFor(Map.class).with(schema).readValues(new File(csvPath)).readAll();
        jsonMapper.writerWithDefaultPrettyPrinter().writeValue(new File(jsonPath), readAll);
        System.out.println("CSV converted to JSON.");
    }
}
