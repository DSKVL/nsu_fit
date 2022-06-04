#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>

int execvpe(char *file, char *argv[], char *newenv[]) {
    extern char **environ;

    environ = newenv;
    execvp(file, argv);

    return(-1);
}

int main() {
    static char *argv[ ] =
                { "/home/students/20200/d.koval2/OS_labs/execvpe/test", "first arg", "second arg", (char *) 0 };
    static char *nenv[ ] =
                { "NEWENV=yes", (char *) 0 };

    execvpe(argv[0], argv, nenv);
    perror(argv[0]);

    exit(1);
}

