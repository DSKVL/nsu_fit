#include<stdio.h>
#include<stdlib.h>
#include<string.h>

typedef struct list_head { struct list_head* next; char* data; unsigned long len;} list_head_t;

list_head_t* get_list_head(char* data, unsigned long length) {
    list_head_t* res = (list_head_t*) malloc(sizeof(list_head_t));
    if (data) {
        res->data = (char*) malloc(length*sizeof(char));
        res->len = length;
        memcpy(res->data, data, length);
    } else {
        res->data = NULL;
        res->len = 0;
    }
    res->next = NULL;
    return res;
}

void append_list(list_head_t* a, list_head_t* b) {
    list_head_t* cur = a;
    while(cur->next != NULL) {
        cur = cur->next;
    }
    cur->next = b;
}

void print_list(list_head_t* list) {
    printf("%.*s", (int) list->len, list->data);
    if (list->next != NULL) print_list(list->next);
}

void delete_list(list_head_t* list) {
    if (list->next != NULL)
        delete_list(list->next);

    free(list->data);
    free(list);
}

int main() {
    char buf[1024];
    unsigned long count;
    list_head_t* list = NULL;

    printf("Enter a string: ");
    if(fgets(buf, 1024, stdin)) {
        count = strlen(buf);
        if (count !=2 && buf[0] != '.')
            list = get_list_head(buf, count+1);
        else return 0;
    }

    printf("Enter more strings: ");
    while(fgets(buf, 1024,stdin)) {
        count = strlen(buf);
        if(buf[0] == '.' && count == 2){
            break;
        }

        append_list(list, get_list_head(buf, count+1));
        printf("Enter more strings: ");
    }
    print_list(list);
    delete_list(list);
    return 0;
}

