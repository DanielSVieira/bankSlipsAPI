package com.bankslips.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bankslips.Application;
import com.bankslips.config.TestAsyncConfig;
import com.bankslips.domain.BankSlips;
import com.bankslips.domain.bulkupload.BulkJobStatus;
import com.bankslips.domain.bulkupload.BulkUploadFailure;
import com.bankslips.domain.bulkupload.BulkUploadJob;
import com.bankslips.repository.BankSlipsRepository;
import com.bankslips.service.interfaces.IPersistenceBulkService;
import com.bankslips.testutils.TestUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestAsyncConfig.class)
@TestPropertySource(properties = {
	    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
	    "spring.kafka.listener.auto-startup=false"
	})
public class BankSlipsServiceAsyncTest {
	
	  @Autowired
	  private IPersistenceBulkService<BankSlips> bankSlipsAsyncService;
    
    @Autowired
    private BulkJobService jobService;

    @Autowired
    private BankSlipsRepository bankSlipsRepository;
    
    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @BeforeEach
    private void setp() { 
    	bankSlipsRepository.deleteAll();
    }

    @Test
    void shouldPersist100BankSlips() {
        int totalRecords = 100;
        List<BankSlips> slips =
            TestUtils.generateValidBankSlipsList(totalRecords);

        UUID jobId = bankSlipsAsyncService.startAsyncBulkUpload(slips);

        BulkUploadJob job = waitForJobCompletion(jobId);

        assertEquals(BulkJobStatus.COMPLETED, job.getStatus());
        assertEquals(totalRecords, bankSlipsRepository.count());
        assertEquals(totalRecords, job.getSuccessRecords());
        assertEquals(0, job.getFailedRecords());
    }

    
    @Test
    void shouldRejectDuplicateBankSlips() {
        int totalRecords = 100;

        List<BankSlips> slips =
            TestUtils.generateValidBankSlipsList(totalRecords);

        slips.add(
            TestUtils.generateBankSlipWiwithDuplicatedExternalID(slips.get(0))
        );

        assertEquals(totalRecords + 1, slips.size());

        UUID jobId = bankSlipsAsyncService.startAsyncBulkUpload(slips);

        BulkUploadJob job = waitForJobCompletion(jobId);

        assertEquals(BulkJobStatus.COMPLETED_WITH_ERRORS, job.getStatus());
        assertEquals(totalRecords, bankSlipsRepository.count());
        assertEquals(1, job.getFailedRecords());

        List<BulkUploadFailure> failures =
            jobService.getFailureJobById(jobId);

        assertEquals(1, failures.size());
    }
    
    @Test
    void testInserting1InvalidRecordAmongAValidBatch() {
        int totalRecords = 100;

        List<BankSlips> slips =
            TestUtils.generateValidBankSlipsList(totalRecords);

        slips.add(
            TestUtils.generateBankSlipWithPastDueDate()
        );

        assertEquals(totalRecords + 1, slips.size());

        UUID jobId = bankSlipsAsyncService.startAsyncBulkUpload(slips);

        BulkUploadJob job = waitForJobCompletion(jobId);

        assertEquals(BulkJobStatus.COMPLETED_WITH_ERRORS, job.getStatus());
        assertEquals(totalRecords, bankSlipsRepository.count());
        assertEquals(1, job.getFailedRecords());

        List<BulkUploadFailure> failures =
            jobService.getFailureJobById(jobId);

        assertEquals(1, failures.size());
    }

    /**
     * ignore excetions during the tests
     * Increase the timeout, if needed
     * @param jobId
     * @return
     */
    private BulkUploadJob waitForJobCompletion(UUID jobId) {
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)  
                .pollInterval(200, TimeUnit.MILLISECONDS)
                .ignoreExceptions()            
                .until(() -> {
                    Optional<BulkUploadJob> optionalJob = jobService.getJobById(jobId);
                    if (optionalJob.isEmpty()) {
                        return false; // job not yet persisted
                    }
                    BulkUploadJob job = optionalJob.get();
                    return job.getStatus() == BulkJobStatus.COMPLETED
                            || job.getStatus() == BulkJobStatus.COMPLETED_WITH_ERRORS
                            || job.getStatus() == BulkJobStatus.FAILED;
                });

        return jobService.getJobById(jobId)
                .orElseThrow(() -> new IllegalStateException("Job never completed"));
    }





}
