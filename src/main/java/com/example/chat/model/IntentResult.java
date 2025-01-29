public class IntentResult {
    private String topIntent;
    private double confidence;

    public IntentResult(String topIntent, double confidence) {
        this.topIntent = topIntent;
        this.confidence = confidence;
    }

    public String getTopIntent() {
        return topIntent;
    }

    public double getConfidence() {
        return confidence;
    }

    public boolean isConfident() {
        return confidence >= 0.5; // Minimum güven skoru
    }
} 