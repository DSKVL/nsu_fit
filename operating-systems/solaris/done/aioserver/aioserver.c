#include <aio.h>
#include <signal.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <stddef.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <ctype.h>
#include <string.h>
#include <fcntl.h>
#include <errno.h>

#define CAPACITY 4

int setUpListener() {
    int sock;
    if ((sock = socket(AF_UNIX, SOCK_STREAM, 0)) < 0) {
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
    if (bind(sock, (struct sockaddr *)&address, len) < 0) {
        perror("Can not bind socket");
        exit(1);
    }

    if (listen(sock, 5) == -1) {
        perror("Can not listen socket");
        exit(1);
    }
    return sock;
}

extern int errno;

int socketd;
char bigbuf[256*CAPACITY];
struct aiocb readrq[CAPACITY];
int occupied[CAPACITY];
int readerCount = 0;

void setUpRequests() {
    for (int i = 0; i < CAPACITY; i++) {
        memset(&readrq[i], 0, sizeof readrq[i]);
        readrq[i].aio_buf=bigbuf + 256*i;
        readrq[i].aio_nbytes=256;
        readrq[i].aio_sigevent.sigev_notify=SIGEV_SIGNAL;
        readrq[i].aio_sigevent.sigev_signo=SIGIO;
        readrq[i].aio_sigevent.sigev_value.sival_ptr=&readrq[i];
    }
}

void addConnection(int newClient) {
    printf("Trying add new client\n");
    if (readerCount < CAPACITY) {
	printf("There's enough room\n");
        readerCount++;
        int index = 0;
        while (occupied[index] != 0) index++;
   
	printf("%d request slot\n", index);
        occupied[index] = 1;
        readrq[index].aio_fildes = newClient;
        if (aio_read(&readrq[index])) {
            perror("aio_read");
            exit(1);
        }        
    } else {
        printf("There's not enough slots\n");
    }
}

void closeConnection(int sockDesc) {
    int index = 0;
    while (sockDesc != readrq[index].aio_fildes) index++;
    
    close(readrq[index].aio_fildes);
    readrq[index].aio_fildes = 0;
    occupied[index] = 0;
    readerCount--;
    return;    
}

void sigHandle(int signo, siginfo_t *info, void* cotext) { 
    struct aiocb *req;
    if (signo!=SIGIO || info->si_signo!=SIGIO) return;
    
    req=(struct aiocb *)info->si_value.sival_ptr;

    if (aio_error(req)==0) {
        size_t size;
        size=aio_return(req);
        if (size==0) {
            closeConnection(req->aio_fildes);
        }
        
        for (int i = 0; i < size; i++) {
            char c = toupper(((char*) req->aio_buf)[i]);
            write(STDOUT_FILENO, &c, 1);
        }
        aio_read(req);
    }
}


int main() {
    setUpRequests();   
    socketd = setUpListener();
    sigset_t set;
    sigemptyset(&set);
    sigaddset(&set, SIGIO);

    struct sigaction sigact;
    memset(&sigact, 0, sizeof sigact);
    sigact.sa_sigaction=sigHandle;
    sigact.sa_flags=SA_SIGINFO;
    sigact.sa_mask=set;
    
    sigaction(SIGIO, &sigact, NULL);
    
    while (1) {
        errno = 0;
        int newClient;
	if ((newClient = accept(socketd, NULL, NULL)) == -1) {
            if (errno != EINTR) {
                perror("Can not accept");
                exit(1);
            } else {
	        continue;
	    }
	}
        addConnection(newClient);
    }
}
