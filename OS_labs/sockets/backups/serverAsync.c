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

#define CAPACITY 4

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



int socketd;
char bigbuf[256*CAPACITY];
static struct aiocb readrq[CAPACITY];
const struct aiocb *readrqv[CAPACITY];
int readerCount = 0;

void setUpRequests() {
    for (int i = 0; i < CAPACITY; i++) {
        readrqv[i] = &readrq[i];
        memset(&readrq[i], 0, sizeof readrq[i]);
        readrq[i].aio_buf=bigbuf + 256*i;
        readrq[i].aio_nbytes=1;
        readrq[i].aio_sigevent.sigev_notify=SIGEV_SIGNAL;
        readrq[i].aio_sigevent.sigev_signo=SIGIO;
        readrq[i].aio_sigevent.sigev_value.sival_ptr=&readrq[i];
    }
}

void closeConnection(int sockDesc) {
    int index = 0;
    while (index != readrq[index].aio_fildes) index++;

    close(readrq[index].aio_fildes);
    readrq[index] = readrq[readerCount--];
}

void sigHandle(int signo, siginfo_t *info, void* cotext) { 
    printf("IN HANDLER");
    struct aiocb *req;
    if (signo!=SIGIO || info->si_signo!=SIGIO) return;
    req=(struct aiocb *)info->si_value.sival_ptr;
    
    if (aio_error(req)==0) {
        size_t size;
        size=aio_return(req);
        if (size==0) {
            closeConnection(req->aio_fildes);
        }
        
        if (req->aio_fildes != socketd) {
		
            for (int i = 0; i < size; i++) {
                char c = toupper(((char*) req->aio_buf)[i]);
                write(STDOUT_FILENO, &c, 1);
            }
            aio_read(req);
	} else {
	    int newClient;
            if ((newClient = accept(socketd, NULL, NULL)) == -1) {
                perror("Can not accept");
                exit(1);
            }
        
            readrq[readerCount].aio_fildes = newClient;

           if (aio_read(&readrq[readerCount++])) {
                perror("aio_read");
    	        exit(1);
            }
	}
    }
}

int main() {
   
    socketd = setUpListener();
    sigset_t set;
    sigemptyset(&set);


    struct sigaction sigact;
    memset(&sigact, 0, sizeof sigact);
    sigact.sa_sigaction=sigHandle;
    sigact.sa_flags=SA_SIGINFO;
    sigact.sa_mask=set;
    
    sigaction(SIGIO, &sigact, NULL);

    readrq[readerCount].aio_fildes = socketd;
    if (aio_read(&readrq[readerCount++])) {
        perror("aio_read");
        exit(1);
    }
    printf("Reading listener\n");	

    while (1) sigsuspend(&set);
}
