#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdbool.h>


void scale(double array[], int array_length) {
  double acum = 0, avg, max = -10000;
  for(int i = 0; i < array_length; i++) {
    acum+=array[i];
    if(array[i] > max) {
      max = array[i];
    }
  }
  avg = acum/array_length;
  for(int i = 0; i < array_length; i++) {
    array[i] = (array[i] - avg) / max;
    printf("%f ", array[i]);
  }
}

int main() {
  // Univariate
  double params[2] = {0};
  double samples[5] = {1,2,3,4,5};
  double y[5] = {2,4,6,8,10};
  bool univariate = true;

  double alfa = 0.01;
  int samples_length = sizeof(samples)/sizeof(samples[0]);

  if(univariate) {
    double b_samples[samples_length][2];
      for(int i = 0; i < samples_length; i++) {
        b_samples[i][0] = 1;
        b_samples[i][1] = samples[i];
      }
  }

  int columns = sizeof(b_samples[0])/sizeof(b_samples[0][0]);
  for(int i = 0; i < samples_length; i++) {
    for(int j = 0; j < columns; j++) {
      printf("%f ", b_samples[i][j]);
    }
    printf("\n");
  }
  //scale(samples, samples_length);


  int epochs = 0;

  //while(epochs < 1000 || )



  return 0;
}
