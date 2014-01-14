package org.example;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.ArrayList;
import java.util.List;

public class StatefulItemReader implements ItemReader<String> {

    private List<String> list;

    @BeforeStep
    public void initializeState(StepExecution stepExecution) {
        this.list = new ArrayList<>();
    }

    @AfterStep
    public ExitStatus exploitState(StepExecution stepExecution) {
        System.out.println("******************************");
        System.out.println(" READING RESULTS : " + list.size());

        return stepExecution.getExitStatus();
    }

    @Override
    public String read() throws Exception {
        this.list.add("some stateful reading information");
        if (list.size() < 10) {
            return "value " + list.size();
        }
        return null;
    }
}
