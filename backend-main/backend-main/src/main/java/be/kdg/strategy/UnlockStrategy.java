package be.kdg.strategy;

import be.kdg.domain.User;

public interface UnlockStrategy {


    boolean validate(Long lockId, String credentials);
    /**
     * Fetches the validated user associated with this unlock strategy.
     * By default, this method is unsupported and should be overridden when user validation is required.
     *
     * @return The validated user.
     * @throws UnsupportedOperationException if not implemented.
     */
    default User getValidatedUser() {
        throw new UnsupportedOperationException("Fetching validated user is not supported for this strategy.");
    }
}