package com.oustme.oustsdk.question_module.model;

import com.oustme.oustsdk.room.dto.DTOMTFColumnData;

public class MTFSelectedAnswer {

    DTOMTFColumnData leftColumn;
    DTOMTFColumnData rightColumn;

    public DTOMTFColumnData getLeftColumn() {
        return leftColumn;
    }

    public void setLeftColumn(DTOMTFColumnData leftColumn) {
        this.leftColumn = leftColumn;
    }

    public DTOMTFColumnData getRightColumn() {
        return rightColumn;
    }

    public void setRightColumn(DTOMTFColumnData rightColumn) {
        this.rightColumn = rightColumn;
    }
}
