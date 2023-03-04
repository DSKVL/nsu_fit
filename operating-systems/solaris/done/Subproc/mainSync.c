#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <errno.h>
#include <wait.h>

int main(int argc, char** argv) {
    if (argc < 1) {
        exit(EINVAL);
    }

    pid_t pid;
    pid = fork();

    if (pid == 0) {
    	execl("/bin/cat", "/bin/cat", argv[1], (char *) 0);
    } else if (pid > 0) {
	pid_t child_pid;
	int status;
	if ((child_pid = wait(&status)) > 0)
            printf("Subrocess! I am your father!\n");
    } else {
        printf("Something went wrong");
    }

    return 0;
}
