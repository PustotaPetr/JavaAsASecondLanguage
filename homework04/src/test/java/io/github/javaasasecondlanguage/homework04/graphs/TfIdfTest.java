package io.github.javaasasecondlanguage.homework04.graphs;


import io.github.javaasasecondlanguage.homework04.GraphPartBuilder;
import io.github.javaasasecondlanguage.homework04.ProcGraph;
import io.github.javaasasecondlanguage.homework04.Record;
import io.github.javaasasecondlanguage.homework04.nodes.ProcNode;
import io.github.javaasasecondlanguage.homework04.utils.ListDumper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.javaasasecondlanguage.homework04.ui.GraphVisualizer.visualizeGraph;
import static io.github.javaasasecondlanguage.homework04.utils.AssertionUtils.assertRecordsEqual;
import static io.github.javaasasecondlanguage.homework04.utils.TestUtils.convertToRecords;
import static java.lang.Thread.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TfIdfTest {

    private ProcNode inputNode;
    private ProcNode outputNode;

    @BeforeEach
    void setUp() {
        var graph = tfIdf.createGraph();

        assertNotNull(graph.getInputNodes());
        assertNotNull(graph.getInputNodes());

        assertEquals(1, graph.getInputNodes().size());
        assertEquals(1, graph.getOutputNodes().size());

        inputNode = graph.getInputNodes().get(0);
        outputNode = graph.getOutputNodes().get(0);
    }

    private static final List<Record> smallInputRecords = convertToRecords(
            new String[]{"Id",  "Text"},
            new Object[][]{
                    {1, "This is the first document."},
                    {2, "This document is the second document."},
                    {3, "And this is the third one."},
                    {4, "Is this the first document?"}
                    }
                    );

    private static final List<Record> smallExpectedRecords = convertToRecords(
            new String[]{"Id", "Words",  "TfIdf"},
            new Object[][]{
                    {1, "document", 0.258},
                    {1, "first", 0.339},
                    {1, "is", 0.2},
                    {1, "the", 0.2},
                    {1, "this", 0.2},
                    {2, "document", 0.429},
                    {2, "is", 0.167},
                    {2, "second", 0.398},
                    {2, "the", 0.167},
                    {2, "this", 0.167},
                    {3, "and", 0.398},
                    {3, "is", 0.167},
                    {3, "one", 0.398},
                    {3, "the", 0.167},
                    {3, "third", 0.398},
                    {3, "this", 0.167},
                    {4, "document", 0.258},
                    {4, "first", 0.339},
                    {4, "is", 0.2},
                    {4, "the", 0.2},
                    {4, "this", 0.2}
            }
            );

    @Test
    void generalTest() {
        var listDumper = new ListDumper();
        GraphPartBuilder
                .startFrom(outputNode)
                .map(listDumper);

        for (var record : smallInputRecords) {
            inputNode.push(record, 0);
        }
        inputNode.push(Record.terminalRecord(), 0);

        List<Record> actualRecords = listDumper.getRecords();

        assertRecordsEqual(smallExpectedRecords, actualRecords);
    }
}
