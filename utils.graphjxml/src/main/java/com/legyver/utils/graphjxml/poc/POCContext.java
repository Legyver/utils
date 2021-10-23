package com.legyver.utils.graphjxml.poc;

import java.io.File;

/**
 * Handle file references for etc directory in this project
 */
public class POCContext {
    static File etcFile(String project, String filename) {
        File file = new File("etc" + File.separator + project + File.separator + filename);
        return file;
    }

    static File etcFile(String filename) {
        return etcFile("graphjxml", filename);
    }
}
