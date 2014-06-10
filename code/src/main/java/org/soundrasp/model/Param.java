package org.soundrasp.model;

/**
 * Describes a parameter used by a module.
 */
public final class Param {

    private final String name;
    private final String description;

    private final double defaultValue;
    private final double minValue;
    private final double maxValue;
    private double value;

    private Source source = null;
    private ParamListener listener;

    public Param(String name, String description, double defaultValue, double minValue, double maxValue) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
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

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
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

        // Clear source
        set((Source) null);

        // Notify listener
        if (listener != null) {
            listener.onValueChange(this, get());
        }
    }

    public Source getSource() {
        return source;
    }

    /**
     * @param source a source to use to get the parameter value.
     */
    public void set(Source source) {
        if (source != this.source) {
            Source oldSource = source;

            this.source = source;

            // Notify listener
            if (listener != null) {
                listener.onSourceChange(this, oldSource, source);
                listener.onValueChange(this, get());
            }
        }
    }

    public void setListener(ParamListener listener) {
        this.listener = listener;
    }

    public interface ParamListener {
        void onValueChange(Param param, double value);
        void onSourceChange(Param param, Source oldSource, Source newSource);
    }
}
