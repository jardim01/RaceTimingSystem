#define GATE_1_BTN 8
#define GATE_1_LED 9
#define GATE_2_BTN 10
#define GATE_2_LED 11
#define WAITING_LED 12

void setup() {
  Serial.begin(9600);
  pinMode(GATE_1_BTN, INPUT_PULLUP);
  pinMode(GATE_1_LED, OUTPUT);
  pinMode(GATE_2_BTN, INPUT_PULLUP);
  pinMode(GATE_2_LED, OUTPUT);
  pinMode(WAITING_LED, OUTPUT);
}

void loop() {
  int gate1 = !digitalRead(GATE_1_BTN);
  int gate2 = !digitalRead(GATE_2_BTN);

  digitalWrite(GATE_1_LED, gate1);
  digitalWrite(GATE_2_LED, gate2);

  Serial.write((gate1 << 1) | gate2);
  Serial.flush();

  long iterations = 0;
  while (Serial.available() <= 0) {
    // wait for a few iterations before turning on LED (cannot blink that fast)
    if (iterations++ > 1200) {
      digitalWrite(WAITING_LED, 1);
    }
  }
  Serial.read();
  digitalWrite(WAITING_LED, 0);
}
