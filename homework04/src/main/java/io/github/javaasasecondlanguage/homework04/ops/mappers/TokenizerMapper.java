package io.github.javaasasecondlanguage.homework04.ops.mappers;

import io.github.javaasasecondlanguage.homework04.Collector;
import io.github.javaasasecondlanguage.homework04.Record;
import io.github.javaasasecondlanguage.homework04.ops.Mapper;

import java.util.ArrayList;

import static java.util.List.of;

/**
 * Splits text in the specified column into words, then creates a new record with each word.
 *
 * Split should happen on the following symbols: " ", ".", ",", "!", ";", "?", "'", ":"
 */
public class TokenizerMapper implements Mapper {

    private static final String SPLIT_PATTERN = "[\\s,\\.\\!\\;\\?\\'\\:\"]+";
    private final String inputColumn;
    private final String outputColumn;

    public TokenizerMapper(String inputColumn, String outputColumn) {
        this.inputColumn = inputColumn;
        this.outputColumn = outputColumn;
        //throw new IllegalStateException("You must implement this");
    }

    @Override
    public void apply(Record inputRecord, Collector collector) {
        ArrayList<String> excludedColumns = new ArrayList<>();
        excludedColumns.add(this.inputColumn);
        Record newRecord = inputRecord.copyColumnsExcept(excludedColumns);
        String[] words = inputRecord.get(this.inputColumn).toString().split(SPLIT_PATTERN);
        for (int i = 0; i < words.length; i++) {
            collector.collect(newRecord.copy().set(this.outputColumn,words[i]));
        }
    }
}
