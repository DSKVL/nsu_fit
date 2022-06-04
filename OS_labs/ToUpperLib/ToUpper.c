#include <stdio.h>
#include <ctype.h>
#include <unistd.h>

int main() {
    int count = 0;
    char buf[1024];
    while(count = read(0, buf, 1024)){
        for (int i = 0; i < count; i++) {
            fputc(toupper(buf[i]), stdout);
        }
    }
}
