#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <sys/un.h>
#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <stddef.h>

int main() {
    int socketd;
    if ((socketd = socket(AF_UNIX, SOCK_STREAM, 0)) == -1) {
        perror("Cannot create socket");
        exit(1);
    }
    
    char* path = "/home/students/20200/d.koval2/OS_labs/sockets/socket";    
    struct sockaddr_un server;
    memset(&server, 0, sizeof(server));
    server.sun_family = AF_UNIX;
    strcpy(server.sun_path, path);
    int len = offsetof(struct sockaddr_un, sun_path) + strlen(path);
    if(connect(socketd,(struct sockaddr*)&server, len) < 0){
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
