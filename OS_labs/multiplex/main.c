#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>

#define CAPACITY 4
#define TIME_OUT 5

void doNothing() {}

int main(int argc, char** argv) {
    if (argc > CAPACITY + 1) exit(1);
  	
    int fdCount = argc - 1;
    FILE* fds[CAPACITY];
    int closed[CAPACITY];
    int closedCount = 0;

    for (int i = 0; i < fdCount; i++) {
	closed[i] = 0;
        if (NULL == (fds[i] = fopen(argv[i+1], "r"))) {
	    perror("Cant open");
	    exit(1);
	}
    }
    
    sigset(SIGALRM, doNothing);

    int current = 0;
    char* buf = malloc(1024* sizeof(char));
    char** bp = &buf;
    while (closedCount != fdCount) {
	while (closed[current] == 1) current++;
	if (current == fdCount) current = 0;
	
	alarm(TIME_OUT);
	size_t bufsize = 1024;
        int read = getline(bp, &bufsize, fds[current]);
	if (read == -1) {
	    closedCount++;
            closed[current] = 1;
	    fclose(fds[current]);
	    continue;
	}
	write(STDOUT_FILENO, buf, read);
	current++;
    }
	
    return 0;
}
