/*
 * MD5 Hash Calculator with Multithreading
 * Uses 8 worker threads to hash files concurrently
 * Recursively scans directories
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <dirent.h>
#include <sys/stat.h>
#include <openssl/md5.h>
#include <unistd.h>
#include <errno.h>

#define NUM_THREADS 8
#define MAX_PATH 4096
#define BUFFER_SIZE 8192

/* Work queue structure */
typedef struct FileNode {
    char filepath[MAX_PATH];
    struct FileNode *next;
} FileNode;

/* Shared work queue */
typedef struct {
    FileNode *head;
    FileNode *tail;
    int count;
    int done;  // Signal that no more files will be added
    pthread_mutex_t mutex;
    pthread_cond_t cond;
} WorkQueue;

/* Global work queue */
WorkQueue work_queue;

/* Statistics */
pthread_mutex_t stats_mutex = PTHREAD_MUTEX_INITIALIZER;
int files_processed = 0;
int files_total = 0;

/*
 * Initialize work queue
 */
void init_queue(WorkQueue *queue) {
    queue->head = NULL;
    queue->tail = NULL;
    queue->count = 0;
    queue->done = 0;
    pthread_mutex_init(&queue->mutex, NULL);
    pthread_cond_init(&queue->cond, NULL);
}

/*
 * Add file to work queue
 */
void enqueue(WorkQueue *queue, const char *filepath) {
    FileNode *node = (FileNode *)malloc(sizeof(FileNode));
    if (!node) {
        fprintf(stderr, "Error: Memory allocation failed for %s\n", filepath);
        return;
    }
    
    strncpy(node->filepath, filepath, MAX_PATH - 1);
    node->filepath[MAX_PATH - 1] = '\0';
    node->next = NULL;
    
    pthread_mutex_lock(&queue->mutex);
    
    if (queue->tail == NULL) {
        queue->head = node;
        queue->tail = node;
    } else {
        queue->tail->next = node;
        queue->tail = node;
    }
    
    queue->count++;
    pthread_cond_signal(&queue->cond);  // Wake up a waiting thread
    
    pthread_mutex_unlock(&queue->mutex);
}

/*
 * Remove file from work queue
 * Returns NULL if queue is empty and done
 */
FileNode* dequeue(WorkQueue *queue) {
    pthread_mutex_lock(&queue->mutex);
    
    while (queue->head == NULL && !queue->done) {
        pthread_cond_wait(&queue->cond, &queue->mutex);
    }
    
    if (queue->head == NULL && queue->done) {
        pthread_mutex_unlock(&queue->mutex);
        return NULL;  // No more work
    }
    
    FileNode *node = queue->head;
    queue->head = node->next;
    
    if (queue->head == NULL) {
        queue->tail = NULL;
    }
    
    queue->count--;
    
    pthread_mutex_unlock(&queue->mutex);
    
    return node;
}

/*
 * Signal that no more files will be added
 */
void mark_queue_done(WorkQueue *queue) {
    pthread_mutex_lock(&queue->mutex);
    queue->done = 1;
    pthread_cond_broadcast(&queue->cond);  // Wake up all waiting threads
    pthread_mutex_unlock(&queue->mutex);
}

/*
 * Calculate MD5 hash of a file
 */
int calculate_md5(const char *filepath, unsigned char *result) {
    FILE *file = fopen(filepath, "rb");
    if (!file) {
        return -1;
    }
    
    MD5_CTX md5_ctx;
    MD5_Init(&md5_ctx);
    
    unsigned char buffer[BUFFER_SIZE];
    size_t bytes_read;
    
    while ((bytes_read = fread(buffer, 1, BUFFER_SIZE, file)) > 0) {
        MD5_Update(&md5_ctx, buffer, bytes_read);
    }
    
    MD5_Final(result, &md5_ctx);
    fclose(file);
    
    return 0;
}

/*
 * Convert MD5 hash to hex string
 */
void md5_to_string(unsigned char *md5, char *output) {
    for (int i = 0; i < MD5_DIGEST_LENGTH; i++) {
        sprintf(output + (i * 2), "%02X", md5[i]);
    }
    output[MD5_DIGEST_LENGTH * 2] = '\0';
}

/*
 * Worker thread function
 */
void* worker_thread(void *arg) {
    int thread_id = *(int *)arg;
    
    while (1) {
        FileNode *node = dequeue(&work_queue);
        
        if (node == NULL) {
            break;  // No more work
        }
        
        unsigned char md5_result[MD5_DIGEST_LENGTH];
        char md5_string[MD5_DIGEST_LENGTH * 2 + 1];
        
        if (calculate_md5(node->filepath, md5_result) == 0) {
            md5_to_string(md5_result, md5_string);
            
            // Print result (with mutex to avoid interleaved output)
            pthread_mutex_lock(&stats_mutex);
            printf("%-50s %s\n", node->filepath, md5_string);
            files_processed++;
            pthread_mutex_unlock(&stats_mutex);
        } else {
            fprintf(stderr, "Error: Cannot hash %s: %s\n", 
                    node->filepath, strerror(errno));
        }
        
        free(node);
    }
    
    return NULL;
}

/*
 * Check if path is a regular file
 */
int is_regular_file(const char *path) {
    struct stat path_stat;
    if (stat(path, &path_stat) != 0) {
        return 0;
    }
    return S_ISREG(path_stat.st_mode);
}

/*
 * Check if path is a directory
 */
int is_directory(const char *path) {
    struct stat path_stat;
    if (stat(path, &path_stat) != 0) {
        return 0;
    }
    return S_ISDIR(path_stat.st_mode);
}

/*
 * Recursively scan directory and add files to queue
 */
void scan_directory(const char *dirpath) {
    DIR *dir = opendir(dirpath);
    if (!dir) {
        fprintf(stderr, "Error: Cannot open directory %s: %s\n", 
                dirpath, strerror(errno));
        return;
    }
    
    struct dirent *entry;
    while ((entry = readdir(dir)) != NULL) {
        // Skip . and ..
        if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0) {
            continue;
        }
        
        char fullpath[MAX_PATH];
        snprintf(fullpath, MAX_PATH, "%s/%s", dirpath, entry->d_name);
        
        if (is_directory(fullpath)) {
            // Recursively scan subdirectory
            scan_directory(fullpath);
        } else if (is_regular_file(fullpath)) {
            // Add file to queue
            enqueue(&work_queue, fullpath);
            
            pthread_mutex_lock(&stats_mutex);
            files_total++;
            pthread_mutex_unlock(&stats_mutex);
        }
    }
    
    closedir(dir);
}

/*
 * Process a single path (file or directory)
 */
void process_path(const char *path) {
    if (is_directory(path)) {
        scan_directory(path);
    } else if (is_regular_file(path)) {
        enqueue(&work_queue, path);
        
        pthread_mutex_lock(&stats_mutex);
        files_total++;
        pthread_mutex_unlock(&stats_mutex);
    } else {
        fprintf(stderr, "Error: %s is not a valid file or directory\n", path);
    }
}

/*
 * Main function
 */
int main(int argc, char *argv[]) {
    if (argc < 2) {
        fprintf(stderr, "USAGE: %s <directory/file> [more directories/files]\n", argv[0]);
        return 1;
    }
    
    printf("MD5 Hash Calculator (Using %d threads)\n", NUM_THREADS);
    printf("%-50s %s\n", "FILE", "MD5 HASH");
    printf("%s\n", "================================================================================");
    
    // Initialize work queue
    init_queue(&work_queue);
    
    // Create worker threads
    pthread_t threads[NUM_THREADS];
    int thread_ids[NUM_THREADS];
    
    for (int i = 0; i < NUM_THREADS; i++) {
        thread_ids[i] = i;
        if (pthread_create(&threads[i], NULL, worker_thread, &thread_ids[i]) != 0) {
            fprintf(stderr, "Error: Failed to create thread %d\n", i);
            return 1;
        }
    }
    
    // Process all command-line arguments
    for (int i = 1; i < argc; i++) {
        process_path(argv[i]);
    }
    
    // Signal that no more files will be added
    mark_queue_done(&work_queue);
    
    // Wait for all threads to complete
    for (int i = 0; i < NUM_THREADS; i++) {
        pthread_join(threads[i], NULL);
    }
    
    // Print statistics
    printf("%s\n", "================================================================================");
    printf("Total files processed: %d\n", files_processed);
    
    // Cleanup
    pthread_mutex_destroy(&work_queue.mutex);
    pthread_cond_destroy(&work_queue.cond);
    pthread_mutex_destroy(&stats_mutex);
    
    return 0;
}