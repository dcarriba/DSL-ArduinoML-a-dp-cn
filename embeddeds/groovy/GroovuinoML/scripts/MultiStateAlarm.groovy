sensor "button" onPin 9
actuator "led" pin 12
actuator "buzzer" pin 10

state "initial" means "led" becomes "low" and "buzzer" becomes "low"
state "first" means "led" becomes "high" and "buzzer" becomes "low"
state "second" means led becomes low and "buzzer" becomes "high"

initial "initial"

from "initial" to "first" when "button" becomes "high"
from "first" to "second" when "button" becomes "high"
from "second" to "initial" when button becomes "high"

export "Switch!"