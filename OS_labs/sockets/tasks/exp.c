#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/un.h>
#include <stddef.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <ctype.h>
#include <string.h>
#include <time.h>

int main() {
    int socketd;
    if ((socketd = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        perror("Can not create socket");
        exit(1);
    }
     
    char* path = "/home/students/20200/d.koval2/OS_labs/sockets/socket";
    unlink(path);

    struct sockaddr_in address;
    memset(&address, 0, sizeof(address));
    address.sin_family = AF_INET6;
    address.sin_port = 1844;
    inet_aton("127.0.0.1", &address.sin_addr);
    if (bind(socketd, (struct sockaddr *)&address, sizeof(struct sockaddr_in)) < 0) {
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


    clock_t start = clock();
    while (count = recv(clientd, &c, 1, 0)) {
        c = toupper(c);
        write(STDOUT_FILENO, &c, 1);
    }
    clock_t end = clock();
    printf("%lf", (double) (end-start)/CLOCKS_PER_SEC);

}
