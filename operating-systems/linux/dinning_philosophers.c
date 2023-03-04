#include <pthread.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>

#define THREADS_NUMBER 5
#define STATEBUF_SIZE 16
#define SLEEP_LIMIT 50

typedef struct args {
  pthread_mutex_t* firstMutex;
  pthread_mutex_t* secondMutex;
  int thread_number;
  int err;
} thread_routine_args;

int count[THREADS_NUMBER];
int out = 0;

void* thread_routine(void* args) {
  thread_routine_args* a = (thread_routine_args*) args;
  a->err = 0;

  struct random_data buf;
  char statebuf[STATEBUF_SIZE];
  
  if (0 != initstate_r((unsigned int)a->thread_number, statebuf, STATEBUF_SIZE, &buf)) {
    a->err = errno;
    pthread_exit(&a->err);
  }

  while(1) {
    unsigned int rand;  
    if (0 != random_r(&buf, (int32_t*) &rand)) {
      a->err = errno;
      pthread_exit(&a->err);
    }

    usleep(rand % SLEEP_LIMIT);
    
    if (0 != (a->err = pthread_mutex_lock(a->firstMutex))) pthread_exit(&a->err);
    if (0 != (a->err = pthread_mutex_lock(a->secondMutex))) pthread_exit(&a->err);
    
    if (0 != random_r(&buf, (int32_t*) &rand)) {
      a->err = errno;
      pthread_exit(&a->err);
    }

    usleep(rand % SLEEP_LIMIT);
    
    if (0 != (a->err = pthread_mutex_unlock(a->secondMutex))) pthread_exit(&a->err);
    if (0 != (a->err = pthread_mutex_unlock(a->firstMutex))) pthread_exit(&a->err);
    count[a->thread_number]++;
    if (out) break;
  }

  pthread_exit(&a->err);
}

int main() {
  pthread_mutexattr_t attributes;
  pthread_mutex_t mutexes[THREADS_NUMBER];
  pthread_t threads[THREADS_NUMBER];
  thread_routine_args args[THREADS_NUMBER];
  int err = 0;


  if (0 != (err = pthread_mutexattr_init(&attributes))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
    puts(buf);
    return 1;
  }
  if (0 != (err = pthread_mutexattr_settype(&attributes, PTHREAD_MUTEX_ERRORCHECK))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
    puts(buf);
    return 1;
  }
  if (0 != (err = pthread_mutexattr_setrobust(&attributes, PTHREAD_MUTEX_ROBUST))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
    puts(buf);
    return 1;
  }

  for (int i = 0; i < THREADS_NUMBER; i++) {
    if (0 != (err = pthread_mutex_init(&mutexes[i], &attributes))) {
      char buf[64];
      strerror_r(err, buf, sizeof buf);
	    puts(buf);
	    return 1;
    }
  }

  for (int i = 0; i < THREADS_NUMBER - 1; i++) {
    args[i].firstMutex = &mutexes[i];
    args[i].secondMutex = &mutexes[i+1];
    args[i].thread_number = i;
  }

  args[THREADS_NUMBER-1].firstMutex = &mutexes[0];
  args[THREADS_NUMBER-1].secondMutex = &mutexes[THREADS_NUMBER-1];
  args[THREADS_NUMBER-1].thread_number = THREADS_NUMBER-1;

  for (int i = 0; i < THREADS_NUMBER; i++) {
    if (0 != (err = pthread_create(&threads[i], NULL, thread_routine, &args[i]))) {
      char buf[64];
      strerror_r(err, buf, sizeof buf);
	    puts(buf);
	    return 1;
    }
  }

  sleep(5); 

  out = 1;
  for (int i = 0; i < THREADS_NUMBER; i++) {
    int thread_err;
    if (0 != (err = pthread_join(threads[i],(void*) &thread_err))) {
      char buf[64];
      strerror_r(err, buf, sizeof buf);
      puts(buf);
      return 1;
    }
    printf("%d ", count[i]); 
  }

}

