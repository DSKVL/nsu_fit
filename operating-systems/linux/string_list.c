#include <pthread.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>

#define BUF_SIZE 81

typedef struct ListNode {
  pthread_mutex_t mutex;
  char string[BUF_SIZE];
  struct ListNode* next;
} ListNode;

typedef struct ListHead {
  pthread_mutex_t mutex;
  ListNode* list;
  size_t size;
  int isValid;
} ListHead;

int initList(ListHead*);
int insertList(ListHead*, char*, size_t);
int sortList(ListHead*);
int printList(ListHead*);
int destroyList(ListHead*);

typedef struct setSortDaemonArgs {
  ListHead* list;
  unsigned int interval;
  int err;
} ssdargs;
void* setSortDaemon(void*);

int main() {
  pthread_t sortThread;
  ListHead list;
  ssdargs args; 
  int err = 0;

  if (0 != (err = initList(&list))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
    puts(buf);
    return 1;
  }

  args.list = &list;
  args.interval = 3;
  if (0 != (err = pthread_create(&sortThread, NULL, setSortDaemon, &args))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
    puts(buf);
    return 1;
  }

  char buf[BUF_SIZE];
  while (NULL != fgets(buf, sizeof buf, stdin)) {
    size_t length;
    if (0 != (length = strlen(buf))) {
      if (length == 1 && buf[0] == '\n') err = printList(&list);
      else err = insertList(&list, buf, length - 1);
    }
    
    if (err != 0) {
      char buf[64];
      strerror_r(err, buf, sizeof buf);
      puts(buf);
      return 1;
    }
  }

  sleep(10);
  printList(&list);
  
  sleep(15);

  if (0 != (err = destroyList(&list))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
    puts(buf);
    return 1;
  }
  if (0 != (err = pthread_join(sortThread, (void*) &err))) {
    char buf[64];
    strerror_r(err, buf, sizeof buf);
    puts(buf);
    return 1;
  }

  return 0;
}

void* setSortDaemon(void* args) {
  ssdargs* a = (ssdargs*) args;
  a->err = 0;
  while (a->list->isValid) {
    sleep(a->interval);
    if (0 != (a->err = sortList(a->list))) pthread_exit(&a->err);
  }
  pthread_exit(&a->err);
}

int initList(ListHead* list) {
  list->list = NULL;
  list->size = 0;
  list->isValid = 1;
  return pthread_mutex_init(&list->mutex, NULL);
}

int destroyList(ListHead* list) {
  int err;
  if (0 != (err = pthread_mutex_lock(&list->mutex))) return err;

  ListNode* next = list->list->next;
  for (ListNode* current = list->list; current != NULL; current = next) {
    if (0 != (err = pthread_mutex_lock(&current->mutex))) return err;
    next = current->next;
    list->list = next;
    list->size--;
    if (0 != (err = pthread_mutex_unlock(&current->mutex))) return err;
    free(current);
  }

  list->isValid = 0;
  return pthread_mutex_unlock(&list->mutex);
}

int insertList(ListHead* list, char* string, size_t length) {
  int err = 0;
  if (list->isValid == 0) return 1;
  ListNode* newNode = (ListNode*) malloc(sizeof *newNode);
  if (0 == newNode) {
    return errno;
  }
  
  memset(newNode, 0, sizeof *newNode);
  memmove(newNode->string, string, length);
  if (0 != (err = pthread_mutex_init(&newNode->mutex, NULL))) {
    free(newNode);
    return err;
  }

  if (0 != (err = pthread_mutex_lock(&list->mutex))) {
    free(newNode);
    return err;
  }

  newNode->next = list->list;
  list->list = newNode;
  list->size++;
  return pthread_mutex_unlock(&list->mutex);
}

void _printList(ListHead* list) {
  for (ListNode* iter = list->list; iter; iter = iter->next) 
      puts(iter->string);
  puts("");
}

int printList(ListHead* list) {
  int err;
  if (0 != (err = pthread_mutex_lock(&list->mutex))) return err;
  _printList(list);
  return pthread_mutex_unlock(&list->mutex);
}

int sortList(ListHead* list) {
  int err;

  if (!list->isValid) return 1;
  int notSorted = 1;

  if (list->size == 0) {
    return 0;
  }

  while (notSorted) {
    notSorted = 0;
    ListNode* current = list->list, *next = current->next, *prev = NULL;           
    if (next == NULL) continue;

    int cmp = strncmp(current->string, next->string, BUF_SIZE);
    if (cmp > 0) {
      sleep(1);
      notSorted = 1;

      if (0 != (err = pthread_mutex_lock(&list->mutex))) return err;
      if (0 != (err = pthread_mutex_lock(&current->mutex))) return err;
      if (0 != (err = pthread_mutex_lock(&next->mutex))) return err;
      current->next = next->next;
      next->next = current;
      
      ListNode* temp = current;
      current = next;
      next = temp;
      list->list = current;

      if (0 != (err = pthread_mutex_unlock(&current->mutex))) return err;  
      if (0 != (err = pthread_mutex_unlock(&next->mutex))) return err;
      if (0 != (err = pthread_mutex_unlock(&list->mutex))) return err; 
      puts("\033[H\033[J");
      _printList(list);
    }
      
    prev = current;
    current = next;

    while (1) {
      next = current->next;
      if (next == NULL) break;

      int cmp = strncmp(current->string, next->string, BUF_SIZE);
      if (cmp > 0) {
        sleep(1);
        notSorted = 1;
        
        if (0 != (err = pthread_mutex_lock(&list->mutex))) return err;
        if (0 != (err = pthread_mutex_lock(&prev->mutex))) return err;
        if (0 != (err = pthread_mutex_lock(&current->mutex))) return err;
        if (0 != (err = pthread_mutex_lock(&next->mutex))) return err;

        prev->next = next;
        current->next = next->next;
        next->next = current;
        
        ListNode* temp = current;
        current = next;
        next = temp;
  
        if (0 != (err = pthread_mutex_unlock(&current->mutex))) return err;  
        if (0 != (err = pthread_mutex_unlock(&next->mutex))) return err;
        if (0 != (err = pthread_mutex_unlock(&prev->mutex))) return err;
        if (0 != (err = pthread_mutex_unlock(&list->mutex))) return err; 
        puts("\033[H\033[J");
        _printList(list);
      }

      prev = current;
      current = next;
    }                
  }

  return 0;
}

