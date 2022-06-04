#include <stdio.h>
#include <libgen.h>
#include <stdlib.h>
#include <errno.h>
#include <ctype.h>
#include <string.h>

int main(int argc, char** argv) {
    FILE* pepe[2];
   
    p2open("grep -v ^$ | wc -l", pepe);

    int count = 0;
    char buf [1024];
    while (count=fread(buf,1, 1024, stdin)) {
       fprintf(pepe[0], "%.*s", count, buf);
    } 
    fclose(pepe[0]);
    
    while (count=fread(buf,1, 1024, pepe[1])) {
       fprintf(stdout, "%.*s", count, buf);
    } 

    p2close(pepe);
    return 0;
}
