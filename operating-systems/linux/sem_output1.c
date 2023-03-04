#include <pthread.h>
#include <stdio.h>
#include <string.h>
#include <semaphore.h>

#define STRINGS_NUMBER 10

typedef struct printStringArgs {
    sem_t* sem1;
    sem_t* sem2;
    int err;
} psargs;

int printStrings(const char* string, int amount, sem_t *sem1, sem_t *sem2) {
  int err = 0;
  for (int i = 0; i< amount; i++) {
    if (0 != (err = sem_wait(sem1))) return err;
    puts(string);
    if (0 != (err = sem_post(sem2))) return err;
  }
  return 0;
}

void* printString(void* arg) {
    psargs *args = (psargs*) arg;
    args->err = printStrings("second", STRINGS_NUMBER, args->sem1, args->sem2);
    pthread_exit(&args);
}

int main() {
  sem_t sem[2];
  psargs args;
  int err = 0;
  pthread_t pthread;

  if (0 != (err = sem_init(&sem[1], 0, 0))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
    puts(buf);
    return 1;
  }
  if (0 != (err = sem_init(&sem[0], 0, 0))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
    puts(buf);
    return 1;
  }

  args.sem1 = &sem[1];
  args.sem2 = &sem[0];
  
  if (0 != (err = sem_post(&sem[0]))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
    puts(buf);
    return 1;
  }

  if (0 != (err = pthread_create(&pthread, NULL, printString, (void*) &args))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
    puts(buf);
    return 1;
  }

  if (0 != (err = printStrings("first", STRINGS_NUMBER, &sem[0], &sem[1]))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
    puts(buf);
    return 1;
  }

  psargs res;
  if (0 != (err = pthread_join(pthread, (void*) &res))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
    puts(buf);
    return 1;
  }

  if(0 != args.err) {
    char buf[64];
    strerror_r(args.err, buf, sizeof buf);
    puts(buf);
    return 1;
  }

  if (0 != (err = sem_destroy(&sem[0]))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
    puts(buf);
    return 1;
  }
  if (0 != (err = sem_destroy(&sem[1]))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
    puts(buf);
    return 1;
  }

  pthread_exit(0);
}
