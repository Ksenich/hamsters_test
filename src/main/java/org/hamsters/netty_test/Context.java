package org.hamsters.netty_test;

/**
 * Class for keeping global dependencies.
 */
public class Context {
    static Statistics statistics = new Statistics();

    public Statistics getStatistics() {
        return statistics;
    }
}
