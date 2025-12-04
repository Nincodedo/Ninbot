package dev.nincodedo.ninbot.components.haiku;

import java.util.List;

public record HaikuLineAnalysisResult(boolean correctNumberOfLines, boolean line1SyllablesCorrect,
                                      boolean line2SyllablesCorrect, boolean line3SyllablesCorrect,
                                      List<Integer> lineTotals) {
    public HaikuLineAnalysisResult(List<Integer> lineTotals) {
        this(lineTotals.size() == 3,
                !lineTotals.isEmpty() && lineTotals.get(0) == 5,
                lineTotals.size() >= 2 && lineTotals.get(1) == 7,
                lineTotals.size() == 3 && lineTotals.get(2) == 5, lineTotals);
    }

    public boolean allResultsPass() {
        return correctNumberOfLines && line1SyllablesCorrect && line2SyllablesCorrect && line3SyllablesCorrect;
    }

    public int syllablesByLine(int index) {
        return lineTotals.get(index);
    }
}
