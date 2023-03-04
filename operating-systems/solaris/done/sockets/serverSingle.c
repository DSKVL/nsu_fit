#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/un.h>
#include <stddef.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <ctype.h>
#include <string.h>

int main() {
    int socketd;
    if ((socketd = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        perror("Can not create socket");
        exit(1);
    }
     
    char* path = "/home/students/20200/d.koval2/OS_labs/sockets/socket";
    unlink(path);

    struct sockaddr_un address;
    memset(&address, 0, sizeof(address));
    address.sun_family = AF_UNIX;
    strcpy(address.sun_path, path);
    int len = offsetof(struct sockaddr_un, sun_path) + strlen(path);
    if (bind(socketd, (struct sockaddr *)&address, len) < 0) {
        perror("Can not bind socket");
        exit(1);
    }

    if (listen(socketd, 5) == -1) {
        perror("Can not listen socket");
        exit(1);
    }
    
    int clientd;
    if ((clientd = accept(socketd, NULL, NULL)) == -1) {
        perror("Can not accept");
        exit(1);
    }
 	    
    int count = 0;
    char c;

    while (count = recv(clientd, &c, 1, 0)) {
        c = toupper(c);
        write(STDOUT_FILENO, &c, 1);
    }
}
