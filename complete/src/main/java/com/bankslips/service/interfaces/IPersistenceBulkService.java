package com.bankslips.service.interfaces;

import java.util.List;
import java.util.UUID;

public interface IPersistenceBulkService <T>  {

    UUID startAsyncBulkUpload(List<T> slips);
    void bulkSaveInParallel(List<T> records);
}
