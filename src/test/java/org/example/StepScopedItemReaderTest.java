package org.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class StepScopedItemReaderTest {

    @Configuration
    @EnableBatchProcessing
    static class TestConfig {
        @Autowired
        private JobBuilderFactory jobBuilder;
        @Autowired
        private StepBuilderFactory stepBuilder;

        @Bean
        JobLauncherTestUtils jobLauncherTestUtils() {
            return new JobLauncherTestUtils();
        }

        @Bean
        public DataSource dataSource() {
            EmbeddedDatabaseBuilder embeddedDatabaseBuilder = new EmbeddedDatabaseBuilder();
            return embeddedDatabaseBuilder.addScript("classpath:org/springframework/batch/core/schema-drop-hsqldb.sql")
                    .addScript("classpath:org/springframework/batch/core/schema-hsqldb.sql")
                    .setType(EmbeddedDatabaseType.HSQL)
                    .build();
        }

        @Bean
        public Job jobUnderTest() {
            return jobBuilder.get("job-under-test")
                    .start(stepUnderTest())
                    .build();
        }

        @Bean
        public Step stepUnderTest() {
            return stepBuilder.get("step-under-test")
                    .<String, String>chunk(1)
                    .reader(reader())
                    .processor(processor())
                    .writer(writer())
                    .build();
        }

        @Bean
        @StepScope
        public ItemReader<String> reader() {
            return new StatefulItemReader();
        }

        @Bean
        public ItemProcessor<String, String> processor() {
            return new StatefulItemProcessor();
        }

        @Bean
        public ItemWriter<String> writer() {
            return new StatefulItemWriter();
        }
    }

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void testStepExecution() {
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("step-under-test");

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }

}
