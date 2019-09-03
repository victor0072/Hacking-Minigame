Superlist superlist = new Superlist();
String target;
int popmax;
float mutationRate;
Population population;
int corIndex = 9001;
String bestPhrase = "";
float advFit = 0;
String playerInput = "";
PFont font;


void setup() {
  size(800, 150);
  target = superlist.getMessage();
  popmax = target.length()*30;
  mutationRate=0.01;
  population = new Population(target, mutationRate, popmax);
  font = loadFont("Monospaced.bold-15.vlw");
  population.calcFitness();
}

void keyReleased() {
  if (keyCode == SHIFT) {
    shiftDown = false;
  }
}
int incBright = 0;

void checkCorrect() {
  boolean correct = true;
  if (playerInput.length() == target.length()) {
    for (int i = 0; i < target.length(); i++) {
      if (playerInput.charAt(i) != target.charAt(i)) {
        correct = false;
      }
    }
  } else {
    correct = false;
  }
  if (correct) {
    checkBestPhrase();
    winner = true;
    stop = true;
  } else {
    incBright = 255;
  }
}

int pcScore = 0;

void checkBestPhrase() {
  if (bestPhrase.length() == target.length()) {
    for (int i = 0; i < target.length(); i++) {
      if (bestPhrase.charAt(i) == target.charAt(i)) {
        pcScore++;
      }
    }
  }
}

//score = (100 - int(population.max*100) / timer) * 1000
int curSecond = second();
int timer = 0;
boolean stop = false;
boolean winner = false;
void draw() {
  background(0);

  if (!stop) {
    if (curSecond != second()) {
      curSecond = second();
      timer++;

      population.naturalSelection();
      population.generate();
      population.calcFitness();
      population.evaluate();
    }

    if (int(population.max*100) == 100) {
      stop = true;
    }
  }

  textFont(font);
  int sep = 15;
  fill(255);
  if (stop) {
    text("Solved: " + target, 10, 15 + sep * 0);
  } else {
    text("Attempting to Decrypt: " + bestPhrase, 10, 15 + sep * 0);
  }
  text("Timer: " + timer, 10, 15 + sep * 1);
  text("Solve: " + playerInput, 10, 15 + sep * 2);
  fill(incBright);
  text("Incorect", 10, 15 + sep * 3);
  fill(255);
  if (stop) {
    if (winner) {
      text("Winner!", 10, 15 + sep * 4);
      text("Time: " + timer, 10, 15 + sep * 5);
      text("Press Enter to Reset", 10, 15 + sep * 6);
    } else {
      text("Out of Time", 10, 15 + sep * 4);
      text("Time: " + timer, 10, 15 + sep * 5);
      text("Press Enter to Reset", 10, 15 + sep * 6);
    }
  }
  if (incBright > 0) {
    incBright--;
  }
}

void Reset() {
  target = superlist.getMessage();
  popmax = constrain(target.length()*30, 50, 500);
  mutationRate=0.01;
  population = new Population(target, mutationRate, popmax);

  stop = false;
  winner = false;
  timer = 0;
  incBright = 0;
  population.calcFitness();
  playerInput = "";
}

boolean shiftDown = false;
void keyPressed() {
  if (!stop) {
    if (keyCode != ENTER) {
      if (keyCode != BACKSPACE) {
        if (keyCode != SHIFT) {
          if (keyCode >= 65) {
            if (keyCode <= 90) {
              if (shiftDown) {
                playerInput = playerInput + char(keyCode);
              } else {
                playerInput = playerInput + char(keyCode + 32);
              }
            }
          }
        }
      }
    }
    //if punctuation
    if (keyCode == 49) { //!
      playerInput = playerInput + char(33);
    }
    if (keyCode == 46) {//.
      playerInput = playerInput + char(keyCode);
    }
    if (keyCode == 47) {//?
      playerInput = playerInput + char(63);
    }
    if (keyCode == 32) {//"  "
      playerInput = playerInput + char(keyCode);
    }
    if (keyCode == 44) {//,
      playerInput = playerInput + char(keyCode);
    }
    if (keyCode == 222) {//'
      playerInput = playerInput + "'";
    }
    if (keyCode == BACKSPACE) {
      if (playerInput.length() > 0) {
        playerInput = playerInput.substring(0, playerInput.length()-1);
      }
    }
    if (keyCode == SHIFT) {
      shiftDown = true;
    }
    if (keyCode == ENTER) {
      checkCorrect();
    }
  } else {
    if (keyCode == ENTER) {
      Reset();
    }
  }
}
