#include <pthread.h>
#include <stdio.h>
#include <string.h>

#define STRINGS_NUMBER 10
#define MUTEXES_NUMBER 3 

typedef struct printStringArgs {
    pthread_mutex_t mutexes[MUTEXES_NUMBER];
    int err;
} psargs;

int waitForInitialization = 1;

void* printString(void* arg) {
    psargs *args = (psargs*) arg;

    if (0 != (args->err = pthread_mutex_lock(&args->mutexes[2]))) {
        waitForInitialization = 0;
        pthread_exit(&args);
    }

    waitForInitialization = 0;

    if (0 != (args->err = pthread_mutex_lock(&args->mutexes[0]))) {
        waitForInitialization = 0;
        pthread_exit(&args);
    }

    int i = 2;
    for(int j = 0; j < STRINGS_NUMBER; j++) {
        puts("I'm second thread.");

        if (0 != (args->err = pthread_mutex_unlock(&args->mutexes[i%MUTEXES_NUMBER]))) {
            pthread_exit(&args);
        }

        if (0 != (args->err = pthread_mutex_lock(&args->mutexes[(i + 2)%MUTEXES_NUMBER]))) {
            pthread_exit(&args);
        }
        i++;
    }

    if (0 != (args->err = pthread_mutex_unlock(&args->mutexes[i%3]))) {
        pthread_exit(&args);
    }
    if (0 != (args->err = pthread_mutex_unlock(&args->mutexes[(i+1)%3]))) {
        pthread_exit(&args);
    }

    pthread_exit(&args);
}

int main() {
    pthread_t pthread;
    psargs args;
    pthread_mutexattr_t attributes;
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

    for(int i = 0; i < MUTEXES_NUMBER; i++) {
        if (0 != (err = pthread_mutex_init(&args.mutexes[i],&attributes))) {
            char buf[64];
            strerror_r(err, buf, sizeof buf);
            puts(buf);
            return 1;
        }
    }

    for(int i = 0; i < 2; i++) {
        if (0 != (err = pthread_mutex_lock(&args.mutexes[i]))) {
            char buf[64];
            strerror_r(err, buf, sizeof buf);
            puts(buf);
            return 1;
        }
    }

    if (0 != (err = pthread_create(&pthread, NULL, printString, (void*) &args))) {
        char buf[64];
        strerror_r(err, buf, sizeof buf);
        puts(buf);
        return 1;
    }

    while(waitForInitialization) {}
    
    int i = 0;
    for(int j = 0; i < STRINGS_NUMBER; j++) {
        puts("I'm first thread.");
        if (0 != (err = pthread_mutex_unlock(&args.mutexes[i%MUTEXES_NUMBER]))) {
            char buf[64];
            strerror_r(err, buf, sizeof buf);
            puts(buf);
            return 1;
        }

        if (0 != (err = pthread_mutex_lock(&args.mutexes[(i + 2)%MUTEXES_NUMBER]))) {
            char buf[64];
            strerror_r(err, buf, sizeof buf);
            puts(buf);
            return 1;
        }
        i++;
    }


    if (0 != (err = pthread_mutex_unlock(&args.mutexes[i%MUTEXES_NUMBER]))) {
        char buf[64];
        strerror_r(err, buf, sizeof buf);
        puts(buf);
        return 1;
    }
    if (0 != (err = pthread_mutex_unlock(&args.mutexes[(i+1)%MUTEXES_NUMBER]))) {
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



    for(int i = 0; i < MUTEXES_NUMBER; i++) {
        if (0 != (err = pthread_mutex_destroy(&args.mutexes[i]))) {
            char buf[64];
            strerror_r(err, buf, sizeof buf);
            puts(buf);
            return 1;
        }
    }

    pthread_exit(0);
}
