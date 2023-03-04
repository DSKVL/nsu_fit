#include <pthread.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>

void cleanup(void* arg) {
  (void) arg;
	puts("РУССКИЕ НАЗАД");
}

void* printChars(void* arg) {
  (void) arg;

	pthread_cleanup_push(cleanup, 0);
	while(1) {
		puts("РУССКИЕ ВПЕРЕД");
	}
	pthread_cleanup_pop(1);
	pthread_exit(0);
}

int main() {
	pthread_t pthread;
  int err =0;
	if (0 != (pthread_create(&pthread, NULL, printChars, NULL))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
	  puts(buf);
    return 1;
	}
	
  if (0 != (err = pthread_detach(pthread))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
	  puts(buf);
	  return 1;
  }

	sleep(2);

	if (0 != (pthread_cancel(pthread))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
	  puts(buf);
    return 1;
	}
	
	sleep(1);

	puts("It's done.");

	return 0;
}
