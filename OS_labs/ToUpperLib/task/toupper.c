#include <stdio.h>
#include <ctype.h>
#include <unistd.h>
#include <fcntl.h>
#include <stdlib.h>

int main() {
    int in = open("./mixedCase", O_RDONLY);
    int out = open("./upperCase", O_WRONLY);

    int count = 0;
    char buf[1024];
    while(count = read(in, buf, 1024)){
        for (int i = 0; i < count; i++) {
            char c = toupper(buf[i]);
	    write(out, &c, 1);
        }
    }
    close(out);
    close(in);
    exit(0);
}
                                                                             
                                                                              
                                                                              
                                                                              
                                                                              
                                                                              
                                                                              
                                                
