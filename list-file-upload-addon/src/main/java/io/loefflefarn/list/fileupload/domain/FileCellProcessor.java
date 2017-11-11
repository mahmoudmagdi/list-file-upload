package io.loefflefarn.list.fileupload.domain;

import com.itelg.texin.in.parser.CellProcessor;

public interface FileCellProcessor<T> extends CellProcessor<T> {
    default boolean applies(com.itelg.texin.domain.Cell cell) {
        return false;
    }
}
