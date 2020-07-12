package com.myorg;

import software.amazon.awscdk.core.App;

public final class CdkAppJavaApp {
    public static void main(final String[] args) {
        App app = new App();

        new CdkAppJavaStack(app, "CdkAppJavaStack");

        app.synth();
    }
}
