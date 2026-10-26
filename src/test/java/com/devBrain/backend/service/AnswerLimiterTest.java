package com.devBrain.backend.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnswerLimiterTest {

    private static final String EN_FOLLOW_UP = "Feel free to ask a follow-up question if you'd like more detail.";

    @Test
    void leavesShortAnswerUntouched() {
        String answer = "I build backends. I like Spring.";
        assertEquals(answer, AnswerLimiter.limit(answer, 100, "en"));
    }

    @Test
    void leavesAnswerExactlyAtLimitUntouched() {
        String answer = "x".repeat(50);
        assertEquals(answer, AnswerLimiter.limit(answer, 50, "en"));
    }

    @Test
    void nullAnswerStaysNull() {
        assertNull(AnswerLimiter.limit(null, 100, "en"));
    }

    @Test
    void cutsAtLastSentenceBoundaryAndAppendsFollowUp() {
        String answer = "First sentence. Second sentence. Third sentence runs on and on.";
        // limit 40 falls inside the third sentence
        assertEquals("First sentence. Second sentence. " + EN_FOLLOW_UP,
                AnswerLimiter.limit(answer, 40, "en"));
    }

    @Test
    void doesNotCutInsideDecimalNumber() {
        String answer = "I have 3.5 years of experience and I keep learning every single day.";
        // limit 8 ends right after "3." — not a sentence boundary, so it falls back to the last word
        String result = AnswerLimiter.limit(answer, 8, "en");
        assertTrue(result.startsWith("I have…"), result);
        assertTrue(result.endsWith(EN_FOLLOW_UP), result);
    }

    @Test
    void fallsBackToWordBoundaryWhenNoSentenceEnds() {
        String answer = "one very long sentence without any terminator inside the limit at all";
        String result = AnswerLimiter.limit(answer, 30, "en");
        assertEquals("one very long sentence without… " + EN_FOLLOW_UP, result);
    }

    @Test
    void usesFrenchAndArabicFollowUp() {
        String answer = "Phrase une. Phrase deux est plus longue que la limite.";
        assertTrue(AnswerLimiter.limit(answer, 20, "fr").endsWith("question de suivi si vous souhaitez plus de détails."));
        assertTrue(AnswerLimiter.limit(answer, 20, "ar").endsWith("إذا أردت المزيد من التفاصيل."));
    }

    @Test
    void unknownLanguageFallsBackToEnglish() {
        String answer = "Short one. Another much longer sentence that overflows.";
        assertTrue(AnswerLimiter.limit(answer, 15, "de").endsWith(EN_FOLLOW_UP));
    }
}
