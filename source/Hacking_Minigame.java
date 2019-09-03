import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Hacking_Minigame extends PApplet {

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


public void setup() {
  
  target = superlist.getMessage();
  popmax = target.length()*30;
  mutationRate=0.01f;
  population = new Population(target, mutationRate, popmax);
  font = loadFont("Monospaced.bold-15.vlw");
  population.calcFitness();
}

public void keyReleased() {
  if (keyCode == SHIFT) {
    shiftDown = false;
  }
}
int incBright = 0;

public void checkCorrect() {
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

public void checkBestPhrase() {
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
public void draw() {
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

    if (PApplet.parseInt(population.max*100) == 100) {
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

public void Reset() {
  target = superlist.getMessage();
  popmax = constrain(target.length()*30, 50, 500);
  mutationRate=0.01f;
  population = new Population(target, mutationRate, popmax);

  stop = false;
  winner = false;
  timer = 0;
  incBright = 0;
  population.calcFitness();
  playerInput = "";
}

boolean shiftDown = false;
public void keyPressed() {
  if (!stop) {
    if (keyCode != ENTER) {
      if (keyCode != BACKSPACE) {
        if (keyCode != SHIFT) {
          if (keyCode >= 65) {
            if (keyCode <= 90) {
              if (shiftDown) {
                playerInput = playerInput + PApplet.parseChar(keyCode);
              } else {
                playerInput = playerInput + PApplet.parseChar(keyCode + 32);
              }
            }
          }
        }
      }
    }
    //if punctuation
    if (keyCode == 49) { //!
      playerInput = playerInput + PApplet.parseChar(33);
    }
    if (keyCode == 46) {//.
      playerInput = playerInput + PApplet.parseChar(keyCode);
    }
    if (keyCode == 47) {//?
      playerInput = playerInput + PApplet.parseChar(63);
    }
    if (keyCode == 32) {//"  "
      playerInput = playerInput + PApplet.parseChar(keyCode);
    }
    if (keyCode == 44) {//,
      playerInput = playerInput + PApplet.parseChar(keyCode);
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
class DNA {
  String[] genes;
  float fitness = 0;
  float fitnessx = 0; // noPower


  DNA(int num) {
    genes = new String[num];

    for (int i = 0; i < num; i++) {
      genes[i] = newChar();
    }
  }

  public String getPhrase() {
    return join(genes, "");
  }

  public void calcFitness(String target) {
    float score = 0;
    for (int i = 0; i < genes.length; i++) {
      char a = target.charAt(i);
      char b = genes[i].charAt(0);
      if (a==b) {
        score++;
      }
    }

    
    fitnessx = score/target.length();
    fitness = pow(fitnessx,constrain(target.length()/4,1,10));
    //println("Score: "+ score+ " Fitness: "+score/target.length()+ " Fitness according to Stats: "+this.fitness);
    
  }

  public DNA crossover(DNA partner) {
    DNA child = new DNA(genes.length);
    int midpoint = floor(random(genes.length));
    for (int i = 0; i < genes.length; i++) {
      if (i > midpoint) {
        child.genes[i] = genes[i];
      } else {
        child.genes[i] = partner.genes[i];
      }
    }
    return child;
  }

  public void mutate(float mutationRate) {
    for (int i = 0; i < genes.length; i ++) {
      if (random(1) < mutationRate) {
        genes[i]= newChar();
      }
    }
  }

  public String newChar() {
    int c = floor(random(59, 123));
    if (c == 59) {
      return "?";
    }
    if (c == 60) {
      return "!";
    }
    if (c == 61) {
      return ",";
    }
    if (c == 62) {
      return "'";
    }
    if (c == 63) {
      return " ";
    }
    if (c == 64) {
      return ".";
    }
    return String.valueOf(PApplet.parseChar(c));
  }
}
class Population {

  ArrayList<DNA> matingPool = new ArrayList<DNA>();
  int generations = 0;
  boolean finished = false;
  float perfectScore = 0.99999999f;
  String best = "";

  String target;
  float mutationRate;
  DNA[] population;

  float max = 0;

  Population(String p, float m, int num) {
    target = p;
    mutationRate = m;


    population = new DNA[num];
    for (int i =0; i < num; i++) {
      population[i] = new DNA(target.length());
    }
  }

  public void calcFitness() {
    for (int i =0; i < population.length; i++) {
      population[i].calcFitness(target);
    }
  }

  public void naturalSelection() {
    matingPool.clear();

    float maxFitness = 0;
    float totalFit = 0;
    for (int i = 0; i < population.length; i++) {
      totalFit += population[i].fitness;
      if (population[i].fitness > maxFitness) {
        maxFitness = population[i].fitness;
      }
    }
    advFit = totalFit / population.length;

    if (maxFitness > max) {
      max = maxFitness;
    }

    for (int i = 0; i < population.length; i++) {
      float fitness = map(population[i].fitness, 0, maxFitness, 0, 1);
      int n = floor(fitness*100);
      for (int j = 0; j < n; j++) {
        matingPool.add(population[i]);
      }
    }
  }

  public void generate() {
    for (int i = 0; i < population.length; i ++) {
      int a = floor(random(matingPool.size()));
      int b = floor(random(matingPool.size()));
      DNA partnerA = matingPool.get(a);
      DNA partnerB = matingPool.get(b);
      DNA child = partnerA.crossover(partnerB);
      child.mutate(mutationRate);
      population[i] = child;
    }
    generations++;
  }

  public void evaluate() {
    int fitIndex = 0;
    for (int i = 0; i < population.length; i++) {
      if(population[i].fitness > population[fitIndex].fitness){
        fitIndex = i;
      }
      if (population[i].fitness == 1) {
        corIndex = i;
        finished = true;
      }
    }
    bestPhrase = population[fitIndex].getPhrase();
  }

  public boolean isFinished() {
    return finished;
  }
}
class Superlist{
  public String getMessage(){
   int index = floor(random(superlist.length));
   return superlist[index];
 }
 
 String[] superlist = {
   "My life is my message.",
   "Broken crayons still color.",
   "Boldness be my friend.",
   "My life is my argument.",
   "Leave no stone unturned.",
   "Fight till the last gasp.",
   "Stay hungry. Stay foolish.",
   "And so the adventure begins.",
   "You can if you think you can.",
   "Impossible is for the unwilling.",
   "Grow through what you go through.",
   "Do it with passion or not at all.",
   "Take the risk or lose the chance.",
   "The past does not equal the future.",
   "Good things happen to those who hustle.",
   "At the end of hardship comes happiness.",
   "Dream without fear. Love without limits.",
   "You can do anything you set your mind to.",
   "The wisest mind has something yet to learn.",
   "Open your mind. Get up off the couch. Move. ",
   "Be faithful to that which exists within yourself.",
   "I would rather die on my feet than live on my knees.",
   "Let him that would move the world first move himself.",
   "We rise by lifting others.",
   "Every day is a second chance.",
   "You are amazing. Remember that.",
   "Happiness looks gorgeous on you.",
   "You are capable of amazing things.",
   "Try Again. Fail again. Fail better.",
   "Nothing is worth more than this day.",
   "You are stronger than you think you are.",
   "The first step is you have to say that you can.",
   "Start every day off with a smile and get it over with.",
   "Feel the fear and do it anyway.",
   "Why do they put pizza in a square box?",
   "Do crabs think we walk sideways?",
   "I intend to live forever. So far, so good. ",
   "This suspense is terrible. I hope it will last.",
   "A day without sunshine is like, you know, night.",
   "In heaven, all the interesting people are missing.",
   "Guests, like fish, begin to smell after three days.",
   "Every novel is a mystery novel if you never finish it.",
   "The risk I took was calculated, but man, I am bad at math."
   
 };
 
 
}
  public void settings() {  size(800, 150); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Hacking_Minigame" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
