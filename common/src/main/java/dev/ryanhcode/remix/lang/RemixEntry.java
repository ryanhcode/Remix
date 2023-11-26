package dev.ryanhcode.remix.lang;

import java.util.Collection;

public record RemixEntry(String classTarget, String methodTarget, String invokeTarget, String remixFunction, Collection<RemixParam> params) {

}