#include <stdio.h>
#include <string.h>

int main() {
  for (int i = 0; i < 135; i++) {
    char buf[4096];
    strerror_r(i, buf,sizeof buf);
    puts(buf);
    printf("%lu\n", strlen(buf));
  }
}
