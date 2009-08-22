package org.trinkets.util.diff;

/**
 * Incremental words plain text marker.
 *
 * @author Alexey Efimov
 */
public class PlainTextIncrementalWordsDiffMarker extends PlainTextStringDiffMarker {
    public void markupText(DiffNode.Type sourceType, CharSequence source, DiffNode.Type targetType, CharSequence target) {
        if (DiffNode.Type.REMOVED.equals(sourceType) && targetType.equals(DiffNode.Type.ADDED)) {
            // Compare chars
            PlainTextCharsDiffMarker marker = new PlainTextCharsDiffMarker();
            DiffMarkup.compareChars(source.toString(), target.toString(), marker);

            if (marker.getAddedPercent() < 0.5 && marker.getRemovePercent() < 0.5) {
                sourceResult.append("*");
                sourceResult.append(marker.getSourceResult());
                sourceResult.append("*");
                targetResult.append("*");
                targetResult.append(marker.getTargetResult());
                targetResult.append("*");
            } else {
                super.markupText(sourceType, source, targetType, target);
            }
        } else {
            super.markupText(sourceType, source, targetType, target);
        }
    }
}
