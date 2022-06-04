#include <stdio.h>
#include <stdlib.h>

int main(int argc, char** argv) {
    printf("%d %s %s %s\n", argc, argv[1], argv[2], getenv("NEWENV"));
}
