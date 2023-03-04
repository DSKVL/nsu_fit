#include <stdio.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <fcntl.h>
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
	if (pid == 0) {
            close(filedes[1]);

            char buf[1024];
            int count = 0;
            while((count = read(STDIN_FILENO, buf, 1024))){
                write(filedes[0], buf, count);
            }
            close(filedes[0]);
            exit(0);

	} else if (pid > 0) {
	    close(filedes[1]);
	    int count = 0;
	    char buf[1024];

            while((count = read(filedes[0], buf, 1024))){
                write(STDOUT_FILENO, buf, count);
            }
            close(filedes[0]);

            exit(0);
	}
    } else if (pid > 0) {
	close(filedes[0]);

        int count = 0;
        char buf[1024];
	    
        while (count = read(filedes[1], buf, 1024)) {
            for (int i = 0; i < count; i++) {
	        char c = toupper(buf[i]);
                write(filedes[1], &c, 1);
            }
	}
            
	close(filedes[1]);
        exit(0);
    }
    printf("Something went wrong");

    exit(1);
}
