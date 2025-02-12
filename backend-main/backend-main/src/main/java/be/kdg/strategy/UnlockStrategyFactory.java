package be.kdg.strategy;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UnlockStrategyFactory {

    private final Map<String, UnlockStrategy> strategies;

    public UnlockStrategyFactory(Map<String, UnlockStrategy> strategies) {
        this.strategies = strategies;
    }

    public UnlockStrategy getStrategy(String method) {
        // Return the strategy for the given method, or a default strategy that always fails validation
        return strategies.getOrDefault(method, new UnlockStrategy() {
            @Override
            public boolean validate(Long lockId, String credentials) {
                return false;  // Default strategy returns false for validation
            }
        });
    }
}
