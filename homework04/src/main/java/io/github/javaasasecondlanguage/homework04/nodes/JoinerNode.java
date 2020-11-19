package io.github.javaasasecondlanguage.homework04.nodes;

import io.github.javaasasecondlanguage.homework04.Record;
import io.github.javaasasecondlanguage.homework04.RoutingCollector;

import java.util.ArrayList;
import java.util.List;

public class JoinerNode implements ProcNode {

    private final RoutingCollector collector = new RoutingCollector();
    private final List<String> keyColumns;

    private final List<Record> leftRecords = new ArrayList<>();
    private final List<Record> rightRecords = new ArrayList<>();
    private boolean leftStopAccumulate = false;
    private boolean rightStopAccumulate = false;

    public JoinerNode(List<String> keyColumns) {
        this.keyColumns = keyColumns;
    }

    @Override
    public RoutingCollector getCollector() {
        return collector;
    }

    @Override
    public void push(Record inputRecord, int gateNumber) {
        if (gateNumber != 0 && gateNumber != 1) {
            throw new IllegalArgumentException("Gate does not exist: "+gateNumber);
        }
        if (!inputRecord.isTerminal()){
            if (gateNumber==0) {
                leftRecords.add(inputRecord);
            } else{
                rightRecords.add(inputRecord);
            }
        } else {
            if (gateNumber==0) {
                leftStopAccumulate = true;
            } else {
                rightStopAccumulate = true;
            }
        }

        if (leftStopAccumulate && rightStopAccumulate) {
            for (Record lRec: leftRecords ) {
                Record leftKeyColumns = lRec.copyColumns(keyColumns);
                for (Record rRec: rightRecords  ) {
                    Record rightKeyColumns = rRec.copyColumns(keyColumns);
                    if (leftKeyColumns.equals(rightKeyColumns)) {
                        Record returnRecord = lRec.copy();
                        returnRecord.setAll(rRec.copyColumnsExcept(keyColumns).getData());
                        collector.collect(returnRecord);
                    }
                }
            }
            collector.collect(Record.terminalRecord());
        }

    }
}
