#include <stdio.h>
#include <alloca.h>
#define _GNU_SOURCE 
#include <pthread.h>
#include <string.h>
#include <stdlib.h>
#include <signal.h>
#include <errno.h>

typedef struct piParams_t {
  pthread_barrier_t* bar;
  unsigned long from;
	unsigned long step;
	double result;
  int err;
} params;

int interrupted = 0;
void *piPartial(void* arg) {
  params* parameters = (params*) arg;

  double res = 0;
	unsigned long i = parameters->from;
	unsigned long step = parameters->step;
	while (1) {
		for (int j = 0; j < 100000; j++) {
			res += 1.0/(double)(i*4 + 1);
			res -= 1.0/(double)(i*4 + 3);
			i += step;
		}
    
    pthread_barrier_wait(parameters->bar);
		if (interrupted) {
			break;
		}
	}

	parameters->result = res;
	pthread_exit(parameters);
}

int main(int argc, char** argv) {
	unsigned long threadsNumber = 1;
	if (argc > 1) {
		threadsNumber = strtoul(argv[1], NULL, 10);
    if (0 >= threadsNumber) {
      puts("Specify valid nonzero thread number");
      return 0;
    }
	}

  int err = 0;

  pthread_barrierattr_t barattr;
  if (0 != (err = pthread_barrierattr_init(&barattr))) {
    char buf[64];
    strerror_r(errno, buf, sizeof buf);
	  puts(buf);
    return 1;
  }

  pthread_barrier_t bar;
  if (0 != (err = pthread_barrier_init(&bar, &barattr, (unsigned int) threadsNumber))) {
    char buf[64];
    strerror_r(errno, buf, sizeof buf);
	  puts(buf);
    return 1;
  }
  pthread_t threads[threadsNumber];
	params parameters[threadsNumber];
  
	for (unsigned long i = 0; i < threadsNumber; i++) {
		parameters[i] = (params) {.bar = &bar, .from = i, .step = threadsNumber, .result = 0, .err = 0};
	}

  sigset_t fullset;
  if (0 != sigfillset(&fullset)) {
    char buf[64];
    strerror_r(errno, buf, sizeof buf);
	  puts(buf);
    return 1;
  }
  if (0 != (err = sigprocmask(SIG_SETMASK, &fullset, NULL))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
	  puts(buf);
    return 1;
  }

  for (unsigned long i = 0; i < threadsNumber; i++) {
  	if (0 != (err = pthread_create(&threads[i], NULL, piPartial, &parameters[i]))) {
      interrupted = 1;
      char buf[64];
      strerror_r(err, buf, sizeof buf);
		  puts(buf);
	    return 1;
    }
    printf("created %lu\n", i);
    fflush(stdout);
	}

  int sig = 0;
  sigset_t set;
  if (0 != sigemptyset(&set)) {
    interrupted = 1;
    char buf[64];
    strerror_r(errno, buf, sizeof buf);
	  puts(buf);
    return 1;
  }
  puts("created emptyset");
  if (0 != sigaddset(&set, SIGINT)) {
    interrupted = 1;
    char buf[64];
    strerror_r(errno, buf, sizeof buf);
	  puts(buf);
    return 1;
  }
  puts("added shit, strt sigwait");
  if (0 != (err = sigwait(&set, &sig))) {
    interrupted = 1;
    char buf[64];
    strerror_r(err, buf, sizeof buf);
	  puts(buf);
    return 1;
  }

  interrupted = 1;
	double result = 0;
	for (unsigned long i = 0; i < threadsNumber; i++) {
		params *res = NULL;
  	if (0 != (err = pthread_join(threads[i], (void**) &res))) {
      char buf[64];
      strerror_r(err, buf, sizeof buf);
		  puts(buf);
	    return 1;
    }
    if (res->err != 0) {
      char buf[64];
      strerror_r(res->err, buf, sizeof buf);
		  puts(buf);
      return 1;
    }
		result += res->result;
	}
	result *= 4.0;
	printf("\npi = %.12g \n", result);
}
