package org.soundrasp.model;

/**
 * Describes a parameter used by a module.
 */
public final class Parameter {

    private final String name;
    private final String description;
    private final double defaultValue;

    private double value;
    private Source source = null;

    public Parameter(String name, String description, double defaultValue) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        set(defaultValue);
    }

    /**
     * @return user readable name for the parameter.
     */
    public String getName() {
        return name;
    }

    /**
     * @return user readable description of the parameter.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return default value for the parameter.
     */
    public double getDefaultValue() {
        return defaultValue;
    }

    /**
     * @return the value of the parameter, or if a source is specified, the value of the source.
     */
    public double get() {
        if (source != null) return source.getValue();
        else return value;
    }

    /**
     * Sets the parameter to the specified value, and removes any reference to a source.
     */
    public void set(double value) {
        this.value = value;
        this.source = null;
    }

    public Source getSource() {
        return source;
    }

    /**
     * @param source a source to use to get the parameter value.
     */
    public void setFrom(Source source) {
        this.source = source;
    }
}
