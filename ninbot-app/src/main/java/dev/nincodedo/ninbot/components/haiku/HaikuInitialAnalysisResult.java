package dev.nincodedo.ninbot.components.haiku;

public record HaikuInitialAnalysisResult(boolean messageHasCharacters, boolean messageOnlyCharacters,
                                         boolean messageIsCorrectSyllables, int calculatedSyllableTotal) {
    public boolean messageOnlyCharactersAndIsCorrectSyllables() {
        return messageOnlyCharacters && messageIsCorrectSyllables;
    }

    public boolean allResultsPass() {
        return messageHasCharacters && messageOnlyCharacters && messageIsCorrectSyllables;
    }

    public boolean calculatedSyllableTotalPass() {
        return calculatedSyllableTotal == 17;
    }
}
