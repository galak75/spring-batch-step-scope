package org.example;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;

import java.util.ArrayList;
import java.util.List;

public class StatefulItemProcessor implements ItemProcessor<String, String> {

    private List<String> list;

    @BeforeStep
    public void initializeState(StepExecution stepExecution) {
        this.list = new ArrayList<>();
    }

    @AfterStep
    public ExitStatus exploitState(StepExecution stepExecution) {
        System.out.println("******************************");
        System.out.println(" PROCESSING RESULTS : " + list.size());

        return stepExecution.getExitStatus();
    }

    @Override
    public String process(String inputItem) throws Exception {
        this.list.add("some stateful processing information for " + inputItem);
        return String.format("value '%s' has been processed", inputItem);
    }
}
