package main.groovy.groovuinoml.dsl

import io.github.mosser.arduinoml.kernel.behavioral.TimeUnit
import io.github.mosser.arduinoml.kernel.behavioral.Action
import io.github.mosser.arduinoml.kernel.behavioral.State
import io.github.mosser.arduinoml.kernel.behavioral.BinaryCondition
import io.github.mosser.arduinoml.kernel.behavioral.Condition
import io.github.mosser.arduinoml.kernel.behavioral.LogicalOperator
import io.github.mosser.arduinoml.kernel.behavioral.NotCondition
import io.github.mosser.arduinoml.kernel.behavioral.SensorCondition
import io.github.mosser.arduinoml.kernel.structural.Actuator
import io.github.mosser.arduinoml.kernel.structural.Sensor
import io.github.mosser.arduinoml.kernel.structural.SIGNAL

abstract class GroovuinoMLBasescript extends Script {
//	public static Number getDuration(Number number, TimeUnit unit) throws IOException {
//		return number * unit.inMillis;
//	}

	// sensor "name" pin n
	def sensor(String name) {
		[pin: { n -> ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createSensor(name, n) },
		onPin: { n -> ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createSensor(name, n)}]
	}
	
	// actuator "name" pin n
	def actuator(String name) {
		[pin: { n -> ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createActuator(name, n) }]
	}
	
	// state "name" means actuator becomes signal [and actuator becomes signal]*n
	def state(String name) {
		List<Action> actions = new ArrayList<Action>()
		((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createState(name, actions)
		// recursive closure to allow multiple and statements
		def closure
		closure = { actuator -> 
			[becomes: { signal ->
				Action action = new Action()
				action.setActuator(actuator instanceof String ? (Actuator)((GroovuinoMLBinding)this.getBinding()).getVariable(actuator) : (Actuator)actuator)
				action.setValue(signal instanceof String ? (SIGNAL)((GroovuinoMLBinding)this.getBinding()).getVariable(signal) : (SIGNAL)signal)
				actions.add(action)
				[and: closure]
			}]
		}
		[means: closure]
	}
	
	// initial state
	def initial(state) {
		((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().setInitialState(state instanceof String ? (State)((GroovuinoMLBinding)this.getBinding()).getVariable(state) : (State)state)
	}

	def not(sensor) {
		[negatedSensor: sensor]
	}

	private Sensor resolveSensor(sensor) {
		sensor instanceof String ? (Sensor)((GroovuinoMLBinding)this.getBinding()).getVariable(sensor) : (Sensor)sensor
	}

	private SIGNAL resolveSignal(signal) {
		signal instanceof String ? (SIGNAL)((GroovuinoMLBinding)this.getBinding()).getVariable(signal) : (SIGNAL)signal
	}

	Condition sensorCondition(sensorExpression, signal) {
		boolean negated = sensorExpression instanceof Map && sensorExpression.containsKey("negatedSensor")
		def sensor = negated ? sensorExpression.negatedSensor : sensorExpression
		Condition condition = new SensorCondition(resolveSensor(sensor), resolveSignal(signal))
		negated ? new NotCondition(condition) : condition
	}
	
	// from state1 to state2 when sensor becomes signal
	def from(state1) {
		[to: { state2 -> 
			State fromState = state1 instanceof String ?
				(State)((GroovuinoMLBinding)this.getBinding()).getVariable(state1) : (State)state1
			State toState = state2 instanceof String ?
				(State)((GroovuinoMLBinding)this.getBinding()).getVariable(state2) : (State)state2
			def conditions
			conditions = { List<List<Condition>> groups ->
				Condition condition
				groups.each { group ->
					Condition conjunction = group[0]
					group.drop(1).each { next ->
						conjunction = new BinaryCondition(conjunction, LogicalOperator.AND, next)
					}
					condition = condition == null ? conjunction :
						new BinaryCondition(condition, LogicalOperator.OR, conjunction)
				}
				((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createTransition(
					fromState, toState, condition)
				def continuations = [
					and: { sensor ->
						[becomes: { signal ->
							groups[groups.size() - 1].add(sensorCondition(sensor, signal))
							conditions(groups)
						}]
					},
					or: { sensor ->
						[becomes: { signal ->
							groups.add([sensorCondition(sensor, signal)])
							conditions(groups)
						}]
					}
				]
				return continuations
			}
			[when: { sensor ->
				[becomes: { signal ->
					conditions([[sensorCondition(sensor, signal)]])
				}]
			},
			after: { delay ->
				((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createTransition(
						fromState,
						toState,
						delay)
			}]
		}]
	}
	
	// export name
	def export(String name) {
		println(((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().generateCode(name).toString())
	}
	
	// disable run method while running
	int count = 0
	abstract void scriptBody()
	def run() {
		if(count == 0) {
			count++
			scriptBody()
		} else {
			println "Run method is disabled"
		}
	}
}
