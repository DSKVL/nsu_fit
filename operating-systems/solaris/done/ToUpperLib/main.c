#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <errno.h>
#include <ctype.h>


int main(int argc, char** argv) {
    FILE* pepe = popen("./ToUpper", "w");
    
    int count = 0;
    char buf [1024];
    while (count=fread(buf,1, 1024, stdin)) {
       fprintf(pepe, "%.*s", count, buf);
    } 

    pclose(pepe);
    return 0;
}
