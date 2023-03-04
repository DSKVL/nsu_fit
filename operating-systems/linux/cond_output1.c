#include <pthread.h>
#include <stdio.h>
#include <string.h>

#define STRINGS_NUMBER 10

enum turns{FIRST, SECOND};

typedef struct printStringArgs {
    pthread_mutex_t mutex;
    pthread_cond_t cond;
    enum turns turn;
    int err;
} psargs;

int initMutex(pthread_mutex_t* mutex) {
  int err = 0;
  pthread_mutexattr_t attributes;
  if (0 != (err = pthread_mutexattr_init(&attributes))) return err;
  if (0 != (err = pthread_mutexattr_settype(&attributes, PTHREAD_MUTEX_ERRORCHECK))) return err;
  if (0 != (err = pthread_mutexattr_setrobust(&attributes, PTHREAD_MUTEX_ROBUST))) return err;
  if (0 != (err = pthread_mutex_init(mutex, &attributes))) return err;
  if (0 != (err = pthread_mutexattr_destroy(&attributes))) return err; 

  return 0;
}

int initCond(pthread_cond_t* cond) {
  pthread_condattr_t cattributes;
  int err = 0;
  if (0 != (err = pthread_condattr_init(&cattributes))) return err;
  if (0 != (err = pthread_cond_init(cond, &cattributes))) return err;
  if (0 != (err = pthread_condattr_destroy(&cattributes))) return err; 
  return 0;
}

int printStrings(const char* string, int amount,
    pthread_mutex_t* mutex, pthread_cond_t* cond,
    enum turns* currentTurn, enum turns myTurn, enum turns nextTurn){
  int err = 0;
  for (int i = 0; i< amount; i++) {
    if (0 != (err = pthread_mutex_lock(mutex))) return err;
    while(*currentTurn != myTurn)
      if (0 != (err = pthread_cond_wait(cond, mutex))) return err;

    puts(string);
    *currentTurn = nextTurn;

    if (0 != (err = pthread_cond_broadcast(cond))) return err;
    if (0 != (err = pthread_mutex_unlock(mutex))) return err;
  }
  return 0;
}

void* printString(void* arg) {
    psargs *args = (psargs*) arg;

    if (0 != (args->err = 
          printStrings("first", STRINGS_NUMBER, &args->mutex, &args->cond, &args->turn, FIRST, SECOND))) pthread_exit(&args);
  
    pthread_exit(&args);
}

int main() {
  psargs args;
  int err = 0;
  pthread_t pthread;

  if (0 != (err = initMutex(&args.mutex))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
    puts(buf);
    return 1;
  }
  if (0 != (err = initCond(&args.cond))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
    puts(buf);
    return 1;
  }
  args.turn = FIRST;


  if (0 != (err = pthread_create(&pthread, NULL, printString, (void*) &args))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
    puts(buf);
    return 1;
  }

  if (0 != (err = printStrings("second", STRINGS_NUMBER, &args.mutex, &args.cond, &args.turn, SECOND, FIRST))) {
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

  if (0 != (err = pthread_mutex_destroy(&args.mutex))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
    puts(buf);
    return 1;
  }

  if (0 != (err = pthread_cond_destroy(&args.cond))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
    puts(buf);
    return 1;
  }
  pthread_exit(0);
}
