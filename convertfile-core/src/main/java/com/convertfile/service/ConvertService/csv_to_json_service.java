package com.convertfile.service.ConvertService;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.File;
import java.util.List;
import java.util.Map;

public class csv_to_json_service {
    public void convertCsvToJson(String csvPath, String jsonPath) throws Exception {
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        ObjectMapper jsonMapper = new ObjectMapper();
        
        // Use try-with-resources to ensure the iterator is closed properly
        try (MappingIterator<Map<String, String>> iterator = 
                csvMapper.readerFor(Map.class).with(schema).readValues(new File(csvPath))) {
            List<Map<String, String>> readAll = iterator.readAll();
            jsonMapper.writerWithDefaultPrettyPrinter().writeValue(new File(jsonPath), readAll);
        }
        
        System.out.println("CSV converted to JSON.");
    }
}
