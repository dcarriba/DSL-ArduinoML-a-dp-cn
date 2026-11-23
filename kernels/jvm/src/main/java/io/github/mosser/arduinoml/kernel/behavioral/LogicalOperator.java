package io.github.mosser.arduinoml.kernel.behavioral;

public enum LogicalOperator {
    AND("&&"),
    OR("||");

    LogicalOperator(String symbol) {
        this.symbol = symbol;
    }

    public final String symbol;
}
