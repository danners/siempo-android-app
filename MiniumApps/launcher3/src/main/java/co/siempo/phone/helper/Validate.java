package co.siempo.phone.helper;

/**
 * Simple validation methods. Designed for jsoup internal use
 */
public final class Validate {

    private Validate() {
    }

    /**
     * Validates that the object is not null
     *
     * @param obj object to test
     */
    public static void notNull(Object obj) {
        if (obj == null)
            throw new IllegalArgumentException("Object must not be null");
    }

}
