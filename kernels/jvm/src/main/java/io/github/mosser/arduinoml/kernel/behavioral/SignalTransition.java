package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

public class SignalTransition extends Transition {

    private Condition condition;

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    /*
     * Shortcuts so the existing front ends (ANTLR, Groovy, Java, Scala), keep compiling.
     */
    // ------------------------------------------------
    @Deprecated
    public void setSensor(Sensor sensor) {
        leafCondition().setSensor(sensor);
    }

    @Deprecated
    public void setValue(SIGNAL value) {
        leafCondition().setValue(value);
    }

    private SensorCondition leafCondition() {
        if (!(condition instanceof SensorCondition)) {
            condition = new SensorCondition();
        }
        return (SensorCondition) condition;
    }
    // ------------------------------------------------

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
