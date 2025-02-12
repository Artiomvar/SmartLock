package be.kdg.processor;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CompositeProcessor implements DataProcessor {

    private final List<DataProcessor> processors = new ArrayList<>();

    public CompositeProcessor(List<DataProcessor> processors) {
        this.processors.addAll(processors);
    }

    @Override
    public void process(String eventData) {
        System.out.println("CompositeProcessor: Processing event data sequentially...");
        for (DataProcessor processor : processors) {
            processor.process(eventData);
        }
    }
}
