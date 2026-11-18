
//Wiring code generated from an ArduinoML model
// Application name: DualCheckAlarm

long debounce = 200;
enum STATE {off, on};

STATE currentState = off;

bool button_oneBounceGuard = false;
long button_oneLastDebounceTime = 0;

            

bool button_twoBounceGuard = false;
long button_twoLastDebounceTime = 0;

            

	void setup(){
		pinMode(10, OUTPUT); // buzzer [Actuator]
		pinMode(9, INPUT); // button_one [Sensor]
		pinMode(8, INPUT); // button_two [Sensor]
	}
	void loop() {
			switch(currentState){

				case off:
					digitalWrite(10,LOW);
					button_oneBounceGuard = millis() - button_oneLastDebounceTime > debounce;
					button_twoBounceGuard = millis() - button_twoLastDebounceTime > debounce;
					if( (digitalRead(9) == HIGH && digitalRead(8) == HIGH) && button_oneBounceGuard && button_twoBounceGuard) {
						button_oneLastDebounceTime = millis();
						button_twoLastDebounceTime = millis();
						currentState = on;
					}
		
				break;
				case on:
					digitalWrite(10,HIGH);
					button_oneBounceGuard = millis() - button_oneLastDebounceTime > debounce;
					button_twoBounceGuard = millis() - button_twoLastDebounceTime > debounce;
					if( (digitalRead(9) == LOW || digitalRead(8) == LOW) && button_oneBounceGuard && button_twoBounceGuard) {
						button_oneLastDebounceTime = millis();
						button_twoLastDebounceTime = millis();
						currentState = off;
					}
		
				break;
		}
	}
	
