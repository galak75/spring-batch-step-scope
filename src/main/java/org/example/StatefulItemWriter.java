package org.example;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;

import java.util.ArrayList;
import java.util.List;

public class StatefulItemWriter implements ItemWriter<String> {

    private List<String> list;

    @BeforeStep
    public void initializeState(StepExecution stepExecution) {
        this.list = new ArrayList<>();
    }

    @AfterStep
    public ExitStatus exploitState(StepExecution stepExecution) {
        System.out.println("******************************");
        System.out.println(" WRITING RESULTS : " + list.size());

        return stepExecution.getExitStatus();
    }

    @Override
    public void write(List<? extends String> strings) throws Exception {
        this.list.add("some stateful writing information");

        // Writing results somewhere...
    }
}
