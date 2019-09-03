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

  String getPhrase() {
    return join(genes, "");
  }

  void calcFitness(String target) {
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

  DNA crossover(DNA partner) {
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

  void mutate(float mutationRate) {
    for (int i = 0; i < genes.length; i ++) {
      if (random(1) < mutationRate) {
        genes[i]= newChar();
      }
    }
  }

  String newChar() {
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
    return String.valueOf(char(c));
  }
}
