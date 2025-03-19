package pl.software2.awsblocks.model;

public record Error(int statusCode, String error, String message) {
    public static Error notFound(String message) {
        return new Error(404, "Not Found", message);
    }

    public static Error badRequest(String message) {
        return new Error(400, "Bad Request", message);
    }

    public static Error internalServerError(String message) {
        return new Error(500, "Internal Server Error", message);
    }
}
