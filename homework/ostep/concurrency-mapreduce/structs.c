#include <string.h>
#include "structs.h"

unsigned long hash(char* str) {
    unsigned long hash = 5381, c = 0;
    while ((c = *str++) != '\0')
        hash = ((hash << 5) + hash) + c;
    return hash % TABLE_SIZE;
}

int node_compare(const void* s1, const void* s2) {
    node **n1 = (node**)s1, **n2 = (node**)s2;
    if (n1 && n2)
        return strcmp((*n1)->key, (*n2)->key);
    else if (*n1 == NULL)
        return -1;
    else if (*n2 == NULL)
        return 1;
    return 0;
}

void table_insert(table* t, char* key, char* val) {    
    subnode* new_subnode = malloc(sizeof(subnode));
    new_subnode->val = strdup(val);
    new_subnode->next = NULL;
    
    long long pos = hash(key);
    pthread_mutex_t lock = t->locks[pos];
    pthread_mutex_lock(&lock);

    /* If node with current key already exists, add val as subnode on that node*/
    node* temp = t->list[pos];
    while (temp) {
        if (strcmp(temp->key, key) == 0) {
            new_subnode->next = temp->subnode;
            temp->size++;
            temp->subnode = temp->current = new_subnode;
            pthread_mutex_unlock(&lock);
            return;
        }
        temp = temp->next;
    }
    
    /* If not, add val as subnode of a new node */
    pthread_mutex_lock(&t->keylock);
    t->nodesize++;
    pthread_mutex_unlock(&t->keylock);
    
    node* new_node = malloc(sizeof(node));
    new_node->key = strdup(key);
    new_node->subnode = new_subnode;
    new_node->current = new_node->subnode;
    new_node->next = t->list[pos];
    t->list[pos] = new_node;
    pthread_mutex_unlock(&lock);
}

table* table_create(size_t size) {
    table* t = malloc(sizeof(table));
    t->list  = malloc(sizeof(node*) * size);
    t->locks = malloc(sizeof(pthread_mutex_t) * size);
    
    t->size  = size;
    t->nodesize = 0;
    pthread_mutex_init(&t->keylock, NULL);
    
    for (size_t i = 0; i < size; i++) {
        t->list[i] = NULL;
        pthread_mutex_init(&(t->locks[i]), NULL);
    }
    return t;
}

void table_free(table* t) {
    for (int i = 0; i < t->size; i++) {
        node* cur_node = t->list[i];
        while (cur_node) {
            subnode* cur_subnode = cur_node->subnode;
            while (cur_subnode) {
                subnode* tmp_subnode = cur_subnode;
                cur_subnode = cur_subnode->next;
                free(tmp_subnode->val);
                free(tmp_subnode);
            }
            node* tmp_node = cur_node;
            cur_node = cur_node->next;
            free(tmp_node->key);
            free(tmp_node);
        }
        pthread_mutex_t* l = &(t->locks[i]);
        pthread_mutex_destroy(l);
    }
    free(t->locks);
    free(t->list);
    free(t);
}