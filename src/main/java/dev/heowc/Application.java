package dev.heowc;

import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.hibernate.jpa.AvailableHints;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.HintSettableJpaCursorItemReader;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StopWatch;

@SpringBootApplication
@EnableBatchProcessing
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  ApplicationRunner runner(JdbcTemplate template) {
    return args -> {
      int insertCount = 100_000;
      int batchSize = 3_000;

      List<User> batchList = IntStream.range(0, insertCount)
          .mapToObj(i -> new User("nickname___" + i))
          .collect(Collectors.toList());

      StopWatch stopWatch = new StopWatch();
      stopWatch.start();
      template.batchUpdate("INSERT INTO user (nickname, nickname2, nickname3) VALUES (?, ?, ?)", batchList, batchSize, (ps, arg) -> {
        ps.setString(1, arg.getNickname());
        ps.setString(2, arg.getNickname2());
        ps.setString(3, arg.getNickname3());
      });
      stopWatch.stop();
      System.out.println(stopWatch.prettyPrint());
    };
  }

  @Configuration
  static class SimpleJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    SimpleJobConfig(JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        EntityManagerFactory entityManagerFactory) {
      this.jobRepository = jobRepository;
      this.transactionManager = transactionManager;
      this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public Job job() {
      return new JobBuilder("job", jobRepository)
          .start(step(jobRepository))
          .build();
    }

    @Bean
    public Step step(JobRepository jobRepository) {
      return new StepBuilder("step", jobRepository)
          .chunk(100, transactionManager)
          .reader(reader())
          .writer(ignored -> {})
          .build();
    }

//    private JpaCursorItemReader<User> reader() {
//      final JpaCursorItemReader<User> reader = new JpaCursorItemReader<>();
//      reader.setEntityManagerFactory(entityManagerFactory);
//      reader.setQueryString("SELECT u FROM User u");
//      return reader;
//    }

    // https://github.com/spring-projects/spring-batch/releases/tag/v5.2.0-M1
    private HintSettableJpaCursorItemReader<User> reader() {
      final HintSettableJpaCursorItemReader<User> reader = new HintSettableJpaCursorItemReader<>();
      reader.setEntityManagerFactory(entityManagerFactory);
      reader.setQueryString("SELECT u FROM User u");
      reader.setHintValues(Map.of(AvailableHints.HINT_FETCH_SIZE, Integer.MIN_VALUE));
      return reader;
    }
  }
}
