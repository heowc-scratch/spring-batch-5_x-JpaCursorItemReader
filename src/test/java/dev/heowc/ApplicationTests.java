package dev.heowc;

import dev.heowc.Application.SimpleJobConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@SpringBatchTest
class ApplicationTests {

  @Autowired
  private JobLauncherTestUtils jobLauncher;

  @BeforeAll
  static void beforeAll() throws InterruptedException {
    Thread.sleep(10_000L);
  }

  @AfterAll
  static void afterAll() throws InterruptedException {
    Thread.sleep(10_000L);
  }

  @Test
  void contextLoads(@Autowired SimpleJobConfig simpleJobConfig) throws Exception {
    jobLauncher.setJob(simpleJobConfig.job());

    jobLauncher.launchJob();
  }

}
