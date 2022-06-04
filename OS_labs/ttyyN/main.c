#include <unistd.h>
#include <stropts.h>
#include <stdio.h>
#include <termios.h>

int main() {
    struct termios term,  backup;
    
    tcgetattr(STDIN_FILENO, &term);
    backup = term;
    term.c_lflag &= ~(ICANON | ISIG | ECHO);
    term.c_cc[VMIN] = 1;
    tcsetattr(STDIN_FILENO, TCSAFLUSH, &term);
    char input = 't';
    while (input != 'y' && input != 'N') {
	printf("Do you want to proceed? y/N\n");
        fflush(stdout);
	read(STDIN_FILENO, &input, 1);
    }
    if (input == 'y') printf("Proceedeng\n");
    else printf("Aborting\n");
    
    tcsetattr(STDIN_FILENO, TCSANOW, &backup);
}
