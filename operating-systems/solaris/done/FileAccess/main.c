#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <unistd.h>

int main(int argc, char** argv) {
	printf("%d %d\n",getuid(), geteuid());
    if (argv == 0)
        return EXIT_FAILURE;
    FILE* f = fopen(argv[1], "r");
    if (f == NULL) {
        perror("Cannot open file");
        return EXIT_FAILURE;
    }
    fclose(f);
    seteuid(getuid());
    printf("%d %d\n",getuid(), geteuid());
    f = fopen(argv[1], "r");
    if (f == NULL) {
        perror("Cannot open file");
        return EXIT_FAILURE;
    }
    fclose(f);
	return EXIT_SUCCESS;
}

