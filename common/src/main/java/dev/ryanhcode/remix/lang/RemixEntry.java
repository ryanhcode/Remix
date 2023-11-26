package dev.ryanhcode.remix.lang;

import java.util.Collection;

public record RemixEntry(String classTarget, String methodTarget, String invokeTarget, String remixFunction, Collection<RemixParam> params) {

    @Override
    public String toString() {
        return "RemixEntry{\n" +
            "\tclassTarget='" + classTarget + '\'' + ",\n" +
            "\tmethodTarget='" + methodTarget + '\'' + ",\n" +
            "\tinvokeTarget='" + invokeTarget + '\'' + ",\n" +
            "\tremixFunction='" + remixFunction + '\'' + ",\n" +
            "\tparams=[" + formatParams() + "]\n" +
            '}';
    }

    private String formatParams() {
        StringBuilder builder = new StringBuilder();
        for (RemixParam param : params) {
            builder.append(param.type()).append(" ").append(param.ordinal()).append(", ");
        }
        return builder.toString();
    }
}