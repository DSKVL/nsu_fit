#include <unistd.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>

int main(int argc, char** argv) {
    pid_t pid;
    pid = fork();
	
    if (pid ==0) {
	int out = open("./mixedCase", O_WRONLY);
    
        int count;
        char buf[1024];
        while(count = read(STDIN_FILENO, buf, 1024)) {
    	    write(out, buf, count);
        }
	
        close(out);
	exit(0);

    } else {
        int in = open("./upperCase", O_RDONLY);

    	int count = 0;
	char buf[1024];
        while(count = read(in, buf, 1024)) {
	    write(STDOUT_FILENO, buf, count);
        }

        close(in);
        exit(0);
    }
}
