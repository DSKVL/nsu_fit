#include <termios.h>
#include <stdio.h>
#include <signal.h>
#include <stdlib.h>

int count = 0;

void intHandler() {
    printf("\a");
    fflush(stdout);
    count++;
}

void quitHandler() {
    printf("\n%d\n", count);
    exit(0);
}


int main() {

    void quitHandler();
    void intHandler();
    
    signal(SIGQUIT, quitHandler);
    struct sigaction action;
    action.sa_handler = intHandler;
    sigemptyset(&action.sa_mask);
    action.sa_flags = SA_NODEFER;
    sigaction(SIGINT, &action, 0);

    while (1) {}

    return 0;
}
