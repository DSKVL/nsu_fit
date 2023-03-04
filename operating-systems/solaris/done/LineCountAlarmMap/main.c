#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <signal.h>
#include <stdlib.h>
#include <sys/mman.h>

char* file_image;
off_t file_length;

void sigcatch() {
    printf("\n%.*s", file_length, file_image);
    exit(0);
}

int main(int argc, char** argv) {
    if (argc == 1) return 0;
    int file_descriptor = open(argv[1], O_RDONLY);
    off_t line_table[1024];
    int line_count = 1;

    off_t line_length_table[1024];

    file_length = lseek(file_descriptor, 0, SEEK_END);
    file_image = (char*) mmap(0, file_length,PROT_READ,
                          MAP_SHARED, file_descriptor, 0);

    if(file_image == MAP_FAILED) {
        perror("An error occured while mapping a file");
    }

    int line_length = 0;
    for (off_t current_offset = 0; current_offset < file_length; current_offset++) {
        line_length++;
        if(file_image[current_offset] == '\n') {
            line_length_table[line_count++] = line_length;
            line_table[line_count] = current_offset;
            line_length = 0;
        }
    }

    void sigcatch();

    while(1) {
        printf("Enter line number:");
        alarm(5);
        signal(SIGALRM, sigcatch);

        int line_number;
        scanf("%d", &line_number);
        if(line_number == 0 || line_number >= line_count) break;
        printf("%.*s\n", line_length_table[line_number], file_image+line_table[line_number]);
    }

    munmap(file_image, file_length);
    close(file_descriptor);
}

