#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <sys/un.h>
#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <stddef.h>
#include <arpa/inet.h>

int main() {
    int socketd;
    if ((socketd = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        perror("Cannot create socket");
        exit(1);
    }
    
    struct sockaddr_in server;
    memset(&server, 0, sizeof(server));
    server.sin_family = AF_INET;
    server.sin_port = 1844;
    inet_aton("127.0.0.1", &server.sin_addr);
    if(connect(socketd,(struct sockaddr*)&server, sizeof(struct sockaddr_in)) < 0){
        perror("Cannot connect");
        exit(1);
    }

    char buf[1024];
    int count = 0;
    while((count = read(STDIN_FILENO, buf, 1024))){
        send(socketd, buf, count, 0);
    }
    
    printf("Succsess");

    close(socketd);    
}    
