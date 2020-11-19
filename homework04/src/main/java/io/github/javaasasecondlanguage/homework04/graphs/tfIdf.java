package io.github.javaasasecondlanguage.homework04.graphs;

import io.github.javaasasecondlanguage.homework04.GraphPartBuilder;
import io.github.javaasasecondlanguage.homework04.ProcGraph;
import io.github.javaasasecondlanguage.homework04.ops.mappers.AddColumnMapper;
import io.github.javaasasecondlanguage.homework04.ops.mappers.LowerCaseMapper;
import io.github.javaasasecondlanguage.homework04.ops.mappers.RetainColumnsMapper;
import io.github.javaasasecondlanguage.homework04.ops.mappers.TokenizerMapper;
import io.github.javaasasecondlanguage.homework04.ops.reducers.CountReducer;
import io.github.javaasasecondlanguage.homework04.ops.reducers.WordFrequencyReducer;

import java.util.List;

import static java.util.List.of;

public class tfIdf {
    public static ProcGraph createGraph() {

        GraphPartBuilder lowerGraph = GraphPartBuilder.init()
                .map(new LowerCaseMapper("Text"))
                .map(new RetainColumnsMapper(List.of("Id", "Text")));

        GraphPartBuilder tfCalc = lowerGraph
                .branch()
                .map(new TokenizerMapper("Text", "Words"))
                .sortThenReduceBy(List.of("Id"), new WordFrequencyReducer("Words", "Tf"));

        GraphPartBuilder NumOfDocuments = lowerGraph
                .branch()
                .reduceBy(List.of(), new CountReducer("totalDocs"));

        GraphPartBuilder idfCalc = lowerGraph
                .branch()
                .map(new TokenizerMapper("Text", "Words"))
                .sortThenReduceBy(List.of("Id", "Words"), new CountReducer(""))
                .map(new AddColumnMapper("countDocs", (record) -> 1))
                .sortThenReduceBy(List.of("Words"), new CountReducer("countDocs"))
                .join(NumOfDocuments, List.of())
                .map(new AddColumnMapper("idf",
                        (record) -> Math.log((float) (Integer) record.get("totalDocs") / (Integer) record.get("countDocs")) + 1));

        GraphPartBuilder tfIdfGraph = tfCalc
                .join(idfCalc, List.of("Words"))
                .map(new AddColumnMapper("TfIdf",
                        (record) -> (Double) record.get("Tf") * (Double) record.get("idf")))
                .map(new RetainColumnsMapper(List.of("Id", "Words", "TfIdf")));

        return new ProcGraph(
                of(tfIdfGraph.getStartNode()),
                of(tfIdfGraph.getEndNode())
        );
    }


}
