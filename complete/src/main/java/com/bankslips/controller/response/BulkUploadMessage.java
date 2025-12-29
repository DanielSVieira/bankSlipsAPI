package com.bankslips.controller.response;

public enum BulkUploadMessage {

    SYNC_COMPLETED("Bulk upload completed"),
    ASYNC_STARTED("Bulk upload started");

    private final String text;

    BulkUploadMessage(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}