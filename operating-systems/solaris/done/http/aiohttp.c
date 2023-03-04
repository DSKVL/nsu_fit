#include <sys/socket.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <netdb.h>
#include <stdlib.h>
#include <string.h>
#include <termios.h>
#include <aio.h>
#include <signal.h>

#define BUFSIZE 4096
#define READSIZE 256


char buf[BUFSIZE];
char writebuf[BUFSIZE];
int bufIndex = 0;
int socketd;
struct aiocb stdinReadRq, socketReadRq, stdoutWriteRq;  

int connectToURL(char* URL) {
    struct addrinfo hints, *res;
    memset(&hints, 0, sizeof hints);
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_family = AF_INET;

    int index = getaddrinfo(URL, "80", &hints, &res);
    if (index) {
	printf(gai_strerror(index));
	printf("\n");
	exit(1);
    }

    int socketd;
    if ((socketd = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        perror("Cannot create socket");
        exit(1);
    }

    if(connect(socketd, res->ai_addr, res->ai_addrlen) < 0){
        perror("Cannot connect");
        exit(1);
    }

    return socketd;
}

size_t printScreen(int fd, char* buf, size_t bufSize) {
    int lineCount = 1;
    int charsCount = 0;
    int pos = 0;
    while (lineCount < 24 && pos < bufSize) {
        if (charsCount++ >= 79) {
	    charsCount = 0;
            lineCount++;
	}
	if (buf[pos++] == '\n') {
	    lineCount++;
	    charsCount = 0;
	}
    }
    const char* mes = "\n                     --  Press spacebar to continue  -- \n";
    const int len = strlen(mes);
    memcpy(writebuf, buf, pos);
    sprintf(writebuf + pos, mes);
    stdoutWriteRq.aio_nbytes = pos + len;
    aio_write(&stdoutWriteRq);
    memmove(buf, buf + pos, bufSize - pos);
    return bufSize - pos;
}

struct termios oldTerminal;

void resetTerminal() {
    tcsetattr(STDIN_FILENO, TCSANOW, &oldTerminal);
}

void switchTerminalMode() {
    struct termios term;

    tcgetattr(STDIN_FILENO, &term);
    oldTerminal = term;
    term.c_lflag &= ~(ICANON | ISIG | ECHO);
    term.c_cc[VMIN] = 1;
    tcsetattr(STDIN_FILENO, TCSAFLUSH, &term);
    
    atexit(resetTerminal);
}

void setUpRequest(struct aiocb *cb, int fd, void* buf, size_t bufSize) {
    memset(cb, 0, sizeof *cb);
    cb->aio_fildes = fd;
    cb->aio_buf=buf;
    cb->aio_nbytes=bufSize;
    cb->aio_sigevent.sigev_notify=SIGEV_SIGNAL;
    cb->aio_sigevent.sigev_signo=SIGIO;
    cb->aio_sigevent.sigev_value.sival_ptr=cb;
}

void sigRead(int signo, siginfo_t *info, void* cotext) {
    struct aiocb *req;
    if (signo!=SIGIO || info->si_signo!=SIGIO) return;

    req=(struct aiocb *)info->si_value.sival_ptr;

    if (aio_error(req)==0) {
        size_t size;
        size=aio_return(req);
        if (size==0) {
        }

        if (req->aio_fildes == socketd) {
	    bufIndex += READSIZE;
	    if (bufIndex < BUFSIZE-READSIZE) {
                req->aio_buf += READSIZE;
		aio_read(req);
	    }
	}
        if (req->aio_fildes == STDIN_FILENO) {
	    char input;
	    read(STDIN_FILENO, &input, 1);
	    if (input == ' '){
	        int overflowFlag = bufIndex >= BUFSIZE - READSIZE ? 1 : 0;
	        bufIndex = printScreen(STDOUT_FILENO, buf, bufIndex);
		socketReadRq.aio_buf = buf + bufIndex;
	        if (bufIndex < BUFSIZE - READSIZE && overflowFlag)
		    aio_read(&socketReadRq);
	    }
	    if (input == 'q')
		exit(0);
            aio_read(req);
	}

    }
}

int main(int argc, char** argv) {
    if (argc != 2) exit(1);
    socketd = connectToURL(argv[1]);
    
    setUpRequest(&stdinReadRq, STDIN_FILENO, calloc(1, sizeof(char)), 1);
    setUpRequest(&socketReadRq, socketd, buf, READSIZE);    
    setUpRequest(&stdoutWriteRq, STDOUT_FILENO, writebuf, 0);    

    switchTerminalMode();

    char *req = "GET /\r\n\r\n"; 
    write(socketd, req, strlen(req));
   
    sigset_t set;
    sigemptyset(&set);
    sigaddset(&set, SIGIO);

    struct sigaction sigact;
    memset(&sigact, 0, sizeof sigact);
    sigact.sa_sigaction=sigRead;
    sigact.sa_flags=SA_SIGINFO;
    sigact.sa_mask=set;

    sigaction(SIGIO, &sigact, NULL);

    if (aio_read(&socketReadRq)) {
            perror("aio_read");
            exit(1);
        }
    if (aio_read(&stdinReadRq)) {
            perror("aio_read");
            exit(1);
        }
    sigset_t set2;
    sigemptyset(&set2);
    while(1) sigsuspend(&set2);    
}
