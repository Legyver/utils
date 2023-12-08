package com.legyver.utils.cli.internal;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class WindowWrapFactoryTest {

    @Test
    public void wrapLongLine() {
        String lineSeparator = "\\".equals(File.separator) ? "\r\n" : "\n";
        WindowWrapFactory factory = new WindowWrapFactory(110, lineSeparator);
        String result = factory.wrap("\t\tUsage: --auth:basic --file my_credentials.properties or --auth:basic --user myname --pass mypass or --auth:basic --creds myuser/mypass");
        assertThat(result).isEqualTo("\t\tUsage: --auth:basic --file my_credentials.properties or --auth:basic --user myname --pass mypass or\r\n\t\t--auth:basic --creds myuser/mypass");
    }
}
