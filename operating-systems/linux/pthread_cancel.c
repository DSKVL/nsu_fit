#include <pthread.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>

void* printChars(void*) {
  while(1) {
		puts("РУССКИЕ ВПЕРЕД");
	}
	pthread_exit(0);
}

int main() {
	pthread_t pthread;
  int err = 0;
  if (0 !=(err = pthread_create(&pthread, NULL, printChars, NULL))) {
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
  
	if (0 != (err = pthread_cancel(pthread))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
	  puts(buf);
	  return 1;
  }
	
  sleep(1);
	puts("And it's gone.");
	return 0;
}
