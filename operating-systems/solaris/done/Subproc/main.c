#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <errno.h>

int main(int argc, char** argv) {
    if (argc < 1) {
        exit(EINVAL);
    }

    pid_t pid;
    pid = fork();

    if (pid == 0) {
    	execl("/bin/cat", "/bin/cat", argv[1], (char*) 0); 
    } else if (pid > 0) {
        printf("Subrocess! I am your father!\n");
    } else {
        printf("Something went wrong");
    }

    return 0;
}
