package org.example.model;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

@Data
public class ExtensionStats {
    private AtomicLong quantity;
    private AtomicLong bytes;
    private AtomicLong lines;
    private AtomicLong nonEmptyLines;
    private AtomicLong commentLines;

    public ExtensionStats() {
        this.quantity = new AtomicLong(0);
        this.bytes = new AtomicLong(0);
        this.lines = new AtomicLong(0);
        this.nonEmptyLines = new AtomicLong(0);
        this.commentLines = new AtomicLong(0);
    }

    public void incQuantity() {
        this.quantity.incrementAndGet();
    }

    public void addBytes(Long bytes) {
        this.bytes.addAndGet(bytes);
    }

    public void addLines(Long lines) {
        this.lines.addAndGet(lines);
    }

    public void addNonEmptyLines(Long nonEmptyLines) {
        this.nonEmptyLines.addAndGet(nonEmptyLines);
    }

    public void addCommentLines(Long commentLines) {
        this.commentLines.addAndGet(commentLines);
    }
}
