package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;

public class NotCondition extends Condition {

    private Condition condition;

    public NotCondition() { }

    public NotCondition(Condition condition) {
        this.condition = condition;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
