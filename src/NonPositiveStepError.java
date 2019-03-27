public class NonPositiveStepError extends Exception {
    @Override
    public String getMessage() {
        return "Metod step must be > 0!";

    }
}
