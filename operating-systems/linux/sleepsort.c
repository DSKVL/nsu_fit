#include <pthread.h>
#include <stdio.h>
#include <malloc.h>
#include <unistd.h>
#include <string.h>

#define BUF_SIZE 1024 
#define ARENA_SIZE (BUF_SIZE * 100)

unsigned int sstrlen(char* string, unsigned int limit) {
	for (unsigned int i = 0; i < limit; i++) {
		if (string[i] == 0) {
			return i + 1;
		}
	}
	return limit + 1;
}

void* sleepAndPrintString(void* arg) {
	unsigned int len = sstrlen((char*) arg, BUF_SIZE);
	if (len == BUF_SIZE + 1) {
		puts("string is too big");
		pthread_exit(0);
	}
	sleep(len);
	fputs((char*) arg, stdout);
	pthread_exit(0);
}

int main() {
	char arena[ARENA_SIZE];
  char *parena = (char*) &arena;
	pthread_t pthread;
	while(0 != fgets(parena, BUF_SIZE, stdin)) {
		int err = pthread_create(&pthread, NULL, sleepAndPrintString, parena);
		if (err != 0) {
			perror("Unable to create thread: ");
			return 0;
		}
		parena += BUF_SIZE;
	}

	pthread_exit(0);
}
