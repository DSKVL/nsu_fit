#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>

int main(int argc, char** argv) {
    int filed;
    if (filed = open(argv[1], O_RDWR) == -1) {
    	perror("Can't open file");
	exit(1);
    }

    struct flock lock;
    lock.l_type = F_WRLCK;
    lock.l_whence = SEEK_SET;
    lock.l_start = 0;
    lock.l_len = 0; 
    if (fcntl(filed, F_SETLK, &lock) == -1) {
        perror("Can't set lock on file");
        exit(1);
    }

    char editorCommand[128];
    sprintf(editorCommand, "vim %s\n", argv[1]);
    system(editorCommand);

    lock.l_type = F_UNLCK;
    fcntl(filed, F_SETLK, &lock);
    close(filed);
}
