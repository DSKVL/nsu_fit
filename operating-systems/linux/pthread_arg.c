#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void* printString(void* arg) {
  puts((char*) arg);
	pthread_exit(0);
}

void exitIfError(int err) { 
  	if (err != 0) {
        char buf[64];
        strerror_r(err, buf, sizeof buf);
		    puts(buf);
	      exit(1);
    }
}

int main() {
	pthread_t pthread[4];
	
	exitIfError(pthread_create(&pthread[0], NULL, printString, "I'm second thread"));
	exitIfError(pthread_create(&pthread[1], NULL, printString, "I'm third thread"));
	exitIfError(pthread_create(&pthread[2], NULL, printString, "I'm fourth thread"));
	exitIfError(pthread_create(&pthread[3], NULL, printString, "I'm fifth thread"));
	
	puts("I'm first thread");
	pthread_exit(0);
}
