#include <sys/socket.h>
#include <stdio.h>
#include <unistd.h>
#include <netinet/in.h>
#include <sys/types.h>
#include <netdb.h>
#include <poll.h>
#include <stdlib.h>
#include <string.h>
#include <arpa/inet.h>
#include <termios.h>

char buf[2048];
size_t bufIndex = 0;
int socketd;

void closeSocket() {
  close(socketd);
}

int connectToURL(char* URL) {
  struct addrinfo hints, *res;
  memset(&hints, 0, sizeof hints);
  hints.ai_socktype = SOCK_STREAM;
  hints.ai_family = AF_INET;

  int index = getaddrinfo(URL, "80", &hints, &res);
  if (index) {
	  puts(gai_strerror(index));
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
  atexit(closeSocket);
  
  freeaddrinfo(res);

  return socketd;
}

size_t printScreen(int fd, char* buf, size_t bufSize) {
  int lineCount = 1;
  int charsCount = 0;
  size_t pos = 0;
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
  write(fd, buf, pos);
  memmove(buf, buf + pos, bufSize - pos);
  puts("\n                     --  Press spacebar to continue  -- ");
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
  term.c_lflag = (tcflag_t) ((int) term.c_lflag & ~(ICANON | ISIG | ECHO));
  term.c_cc[VMIN] = 1;
  tcsetattr(STDIN_FILENO, TCSAFLUSH, &term);
    
  atexit(resetTerminal);
}
int main(int argc, char** argv) {
  if (argc != 2) exit(1);
  socketd = connectToURL(argv[1]);
  switchTerminalMode();

  char *req = "GET /\r\n\r\n"; 
  write(socketd, req, strlen(req));

  struct pollfd fds[2];
    
  fds[0].fd = socketd;
  fds[0].events = POLLIN;
  fds[1].fd = STDIN_FILENO;
  fds[1].events = POLLIN;

  while (1) {   
    (void) poll(fds, 2, -1);

    if (fds[0].revents & POLLIN && bufIndex < 2012) {
      ssize_t r = read(socketd, buf + bufIndex, 32);
      if (r < 0) {
        perror("Cannot read");
        exit(1);
      }
      bufIndex += (size_t) r;	
	  }

	  if (fds[1].revents & POLLIN) {
	    char input;
	    if (0 > read(STDIN_FILENO, &input, 1)) {
        perror("Cant read stdin");
        exit(1);
      }
	    if (input == ' ') bufIndex = printScreen(STDOUT_FILENO, buf, bufIndex); 
	    if (input == 'q') exit(0);
	  }
  }
}
