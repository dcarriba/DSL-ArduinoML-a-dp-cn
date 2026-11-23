package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;

public class BinaryCondition extends Condition {

    private LogicalOperator operator;
    private Condition left;
    private Condition right;

    public BinaryCondition() { }

    public BinaryCondition(Condition left, LogicalOperator operator, Condition right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public LogicalOperator getOperator() {
        return operator;
    }

    public void setOperator(LogicalOperator operator) {
        this.operator = operator;
    }

    public Condition getLeft() {
        return left;
    }

    public void setLeft(Condition left) {
        this.left = left;
    }

    public Condition getRight() {
        return right;
    }

    public void setRight(Condition right) {
        this.right = right;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
