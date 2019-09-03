class Population {

  ArrayList<DNA> matingPool = new ArrayList<DNA>();
  int generations = 0;
  boolean finished = false;
  float perfectScore = 0.99999999;
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

  void calcFitness() {
    for (int i =0; i < population.length; i++) {
      population[i].calcFitness(target);
    }
  }

  void naturalSelection() {
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

  void generate() {
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

  void evaluate() {
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

  boolean isFinished() {
    return finished;
  }
}
