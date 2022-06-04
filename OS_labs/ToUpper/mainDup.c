#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <errno.h>
#include <ctype.h>


int main(int argc, char** argv) {
    if (argc < 1) {
        exit(EINVAL);
    }

    int filedes[2];
    if (pipe(filedes)) {
	perror("Cannot create pipe");
    	exit(1);
    }

    pid_t pid;
    pid = fork();

    if (pid == 0) {
	pid = fork();

	if(pid == 0) {
	    close(filedes[0]);
	    close(filedes[1]);
	    exit(0);
	} else if (pid > 0) {
 	    close(filedes[0]);
	    
	    close(1);
	    dup(filedes[1]);

	    close(filedes[1]);
	    exit(0);
	}

    } else if (pid > 0) {
	close(filedes[1]);
	int count = 0;
	char buf[1024];
	while(count = read(count = filedes[0], buf, 1024)){ 
	    for (int i = 0; i < count; i++) {
		putc(toupper((int) buf[i]), stdout);
	    }
	}
	close(filedes[0]);
	exit(0);
    } 
    printf("Something went wrong");

    exit(1);
}
