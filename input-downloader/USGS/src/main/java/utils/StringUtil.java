package utils;

public class StringUtil {

	public static String getStringInsidePatterns(String text, String leftPattern, String rightPattern) {
		int leftPatternLastIndex = text.lastIndexOf(leftPattern);
		if (leftPatternLastIndex == -1) {
			return null;
		}
		int rightPatternFirstIndex = text.lastIndexOf(rightPattern, leftPatternLastIndex);
		if (rightPatternFirstIndex == -1) {
			return null;
		}
		rightPatternFirstIndex = rightPatternFirstIndex - rightPattern.length() - 1;
		return text.substring(leftPatternLastIndex, rightPatternFirstIndex);
	}
}
