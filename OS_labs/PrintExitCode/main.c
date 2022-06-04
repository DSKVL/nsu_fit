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
        execv(argv[1], &argv[1]);
    } else if (pid > 0) {
        int status;
        pid_t child_pid = wait(&status);
        if (child_pid > 0) {
            printf("\nExit status is: %d\n", WEXITSTATUS(status));
        }
    } else {
        printf("Something went wrong");
    }

    return 0;
}

