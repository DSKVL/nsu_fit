#include <pthread.h>
#include <stdio.h>

void* printString(void* arg) {
	for(int i = 0; i < 10; i++) {
		puts("I'm second thread.");
	}

	pthread_exit(0);
}

int main() {
	pthread_t pthread;
	int err = pthread_create(&pthread, NULL, printString, NULL);
	if (err != 0) {
		perror("Unable to start a thread: ");
		return 0;
	}

	for(int i = 0; i < 10; i++) {
		puts("I'm first thread.");
	}

	pthread_exit(0);
}
