#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <signal.h>
#include <stdlib.h>

int file_descriptor;

void sigcatch() {
	int count = 0;
	char buf[1024];
	printf("\n");

	lseek(file_descriptor, 0, SEEK_SET);
	while(count = read(file_descriptor, buf, 1024)) {
	
		printf("%.*s\n", count, buf);

	}
}
int main(int argc, char** argv) {
    if (argc == 1) return 0;
    file_descriptor = open(argv[1], O_RDONLY);
    int line_table[1024];
    int line_count = 0;

    int line_length_table[1024];

    char buf[1024];
    int count = 0;
    int readed = 0;
    int line_length = 0;
    while (count = read(file_descriptor, buf, 1024)) {
       for (int i = 0; i < count; i++){
            line_length++;
            if(buf[i] == '\n') {
                line_length_table[line_count++] = line_length;
                line_table[line_count] = readed*1024+i;
                line_length = 0;
            }
        }
        readed++;
    }

    void sigcatch();

    while(1) {
        printf("Enter line number:");
	alarm(5);
	signal(SIGALRM, sigcatch);

        int line_number;
        scanf("%d", &line_number);
	if(line_number == 0 || line_number >= line_count) break;
        lseek(file_descriptor, line_table[line_number-1], SEEK_SET);
        read(file_descriptor, buf, 1024);
        printf("%.*s\n", line_length_table[line_number-1], buf);
    }

    close(file_descriptor);
}

