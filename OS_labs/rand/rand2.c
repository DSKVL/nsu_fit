#include <stdio.h>
#include <libgen.h>
#include <stdlib.h>

int main() {
    FILE *sortfp[2];
    pid_t pid;

    pid = p2open("/bin/sort -n", sortfp);
    if (pid == -1) {
        perror("rand");
        exit(1);
    }

    for (int i = 0; i < 100; i++) 
        fprintf(sortfp[0], "%d\n", rand()%101);
    fprintf(sortfp[0],"\n");
    fclose(sortfp[0]);

    for (int i = 0; i < 10; i++) {
        for (int j = 0; j < 10; j++) {
            int d;
            fscanf(sortfp[1], "%d", &d);
            printf("%d ", d);
        }
        printf("\n");
    }
    fclose(sortfp[1]);
}
