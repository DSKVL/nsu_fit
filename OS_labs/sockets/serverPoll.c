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
#include <poll.h>
#include <fcntl.h>

int setUpListener() {
    int socketd;
    if ((socketd = socket(AF_UNIX, SOCK_STREAM, 0)) < 0) {
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
    return socketd;
}

int main() {
    int socketd = setUpListener();

    struct pollfd clients[4];
    clients[0].fd = socketd;
    clients[0].events = POLLIN | POLLHUP;
    for (int i = 1; i < 4; i++) {
        clients[i].fd = -1;
        clients[i].events = POLLIN | POLLHUP;
    }

    int sockCount = 1;
    for (;;) {
        int pollCount = poll(clients, sockCount, -1);

        if (pollCount == -1) {
            perror("poll");
            exit(1);
        }
        for (int i = 0; i < sockCount && pollCount; i++) {
            if (clients[i].revents & POLLIN) {
                if (clients[i].fd == socketd) {
                    int newClient;
                    if ((newClient = accept(socketd, NULL, NULL)) == -1) {
                        perror("Can not accept");
                        exit(1);
                    }

                    clients[sockCount++].fd = newClient;
                    if (fcntl(newClient, F_SETFD, fcntl(newClient, F_GETFL, 0) | O_NONBLOCK) < 0) {
                        perror("Cannot set to nonblock");
                    }
                    pollCount--;
                } else {
                    int count = 0;
                    char buf[32];
                    char c;

                    count = recv(clients[i].fd, buf, 32, 0);
                    for (int i = 0; i < count; i++) {
                        c = toupper(buf[i]);
                        write(STDOUT_FILENO, &c, 1);
                    }
                }
            }
	    if (clients[i].revents & POLLHUP) {
		close(clients[i].fd);
	    	clients[i] = clients[sockCount--];
	    }
        }
    }
}
