package com.devBrain.backend.service;

import java.util.Map;

/**
 * Hard length safeguard applied to LLM answers, independent of the prompt.
 */
public final class AnswerLimiter {

    private static final Map<String, String> FOLLOW_UP = Map.of(
            "en", "Feel free to ask a follow-up question if you'd like more detail.",
            "fr", "N'hésitez pas à poser une question de suivi si vous souhaitez plus de détails.",
            "ar", "لا تتردد في طرح سؤال آخر إذا أردت المزيد من التفاصيل."
    );

    private AnswerLimiter() {
    }

    /**
     * Returns the answer unchanged if it fits in {@code maxChars}. Otherwise cuts it at the last
     * sentence boundary within the limit (or at the last word if there is none) and appends a
     * follow-up invitation in {@code lang}. The invitation is not counted in {@code maxChars}.
     */
    public static String limit(String answer, int maxChars, String lang) {
        if (answer == null || maxChars <= 0 || answer.length() <= maxChars) {
            return answer;
        }

        String followUp = FOLLOW_UP.getOrDefault(lang, FOLLOW_UP.get("en"));
        int boundary = lastSentenceEnd(answer, maxChars);
        String kept;
        if (boundary > 0) {
            kept = answer.substring(0, boundary);
        } else {
            int lastSpace = answer.lastIndexOf(' ', maxChars);
            String cut = answer.substring(0, lastSpace > 0 ? lastSpace : maxChars);
            kept = cut.replaceAll("[\\s,;:\\-]+$", "") + "…";
        }
        return kept.stripTrailing() + " " + followUp;
    }

    /** Index just after the last sentence terminator that ends within the first {@code maxChars}. */
    private static int lastSentenceEnd(String text, int maxChars) {
        for (int i = maxChars - 1; i >= 0; i--) {
            char c = text.charAt(i);
            if ((c == '.' || c == '!' || c == '?' || c == '؟' || c == '…')
                    && (i + 1 == text.length() || Character.isWhitespace(text.charAt(i + 1)))) {
                return i + 1;
            }
        }
        return -1;
    }
}
