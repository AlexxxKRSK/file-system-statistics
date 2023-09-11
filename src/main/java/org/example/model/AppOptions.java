package org.example.model;

import lombok.Data;

@Data
public class AppOptions {
    private String path;
    private Boolean recursive;
    private Integer recMaxDepth;
    private Integer threadsCount;
    private String[] includeExt;
    private String[] excludeExt;

    public int getRecursionDepth() {
        if (recursive) {
            return recMaxDepth == null ? Integer.MAX_VALUE : recMaxDepth + 1;
        } else {
            return 1;
        }
    }
}
