package dev.ryanhcode.remix.lang;

public class RemixParam {
    private final ParamType dataType;
    private final int ordinal;

    public RemixParam(ParamType dataType, int ordinal) {
        this.dataType = dataType;
        this.ordinal = ordinal;
    }

    public ParamType type() {
        return dataType;
    }

    public int ordinal() {
        return ordinal;
    }

    public static enum ParamType {
        LOCAL,
        PARAM,
        FIELD,
        RESULT;
    }

}
