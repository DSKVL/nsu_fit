#include <stdio.h>
#include <alloca.h>
#include <pthread.h>
#include <string.h>
#include <stdlib.h>

#define numberOfSteps 800000000U

typedef struct piParams_t {
	unsigned long from;
	unsigned long to;
	double result;
} params;

void *piPartial(void* arg) {
	params* parameters = (params*) arg;
	double res = 0;
	for (unsigned long i = parameters->from; i < parameters->to; i++) {
		res += 1.0/(double)(i*4 + 1);
		res -= 1.0/(double)(i*4 + 3);
	}
	parameters->result = res;
	pthread_exit(&parameters->result);
}

int main(int argc, char** argv) {
	unsigned long threadsNumber = 1;
	if (argc > 1) {
		threadsNumber = strtoul(argv[1], NULL, 10);
	} 
  
  int err = 0;
	pthread_t threads[threadsNumber];
	params* parameters = (params*) alloca(threadsNumber*sizeof(params));
  
  unsigned long stepsInOne = numberOfSteps/threadsNumber;
	unsigned long currentSteps = 0;
	for (unsigned long i = 0; i < threadsNumber; i++) {
		parameters[i] = (params) {.from = currentSteps, 
					  .to = currentSteps + stepsInOne, 
					  .result = 0};
		currentSteps += stepsInOne;
	}

	for (unsigned long i = 0; i < threadsNumber; i++) {
  	if (0 != (err = pthread_create(threads + i, NULL, piPartial, parameters + i))) {
      char buf[64];
      strerror_r(err, buf, sizeof buf);
		  puts(buf);
	    return 1;
    }
	}

	double result = 0;
	for (unsigned long i = 0; i < threadsNumber; i++) {
		double *ppartial = NULL;
  	if (0 != (err = pthread_join(threads[i], (void**)  &ppartial))) {
        char buf[64];
        strerror_r(err, buf, sizeof buf);
		    puts(buf);
	      return 1;
    }
		result += *ppartial;
	}
	result *= 4.0;

	printf("pi = %.15g \n", result);
}
