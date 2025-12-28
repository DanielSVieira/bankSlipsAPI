package com.bankslips.service.interfaces;

import java.util.List;
import java.util.UUID;

public interface IPersistenceBulkService <T>  {

    UUID startAsyncBulkUpload(List<T> records);
    void bulkSaveInParallel(List<T> records);
    void processKafkaBulk(UUID id, List<T> records);
}
