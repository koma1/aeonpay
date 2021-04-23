package pw.komarov.aeonpay.rest;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
public class ExceptionWrapper {
    @JsonPropertyOrder({"type","message"})
    private static class WrappedError {
        private final Class<? extends Throwable> clazz;
        private final String message;

        public WrappedError(Class<? extends Throwable> clazz, String message) {
            this.clazz = clazz;
            this.message = message;
        }

        public String getType() {
            return clazz.getSimpleName();
        }

        public String getMessage() {
            return message;
        }
    }

    private final WrappedError error;

    public ExceptionWrapper(Class<? extends Throwable> clazz, String message) {
        error = new WrappedError(clazz, message);
    }

    public ExceptionWrapper(Exception e) {
        this(e.getClass(), e.getMessage());
    }

    public WrappedError getError() {
        return error;
    }
}
