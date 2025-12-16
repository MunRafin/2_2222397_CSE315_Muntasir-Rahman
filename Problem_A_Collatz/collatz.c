#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/types.h>

/**
 * Collatz Conjecture Program
 * Uses fork() to create child process that generates the sequence
 * Parent waits for child to complete
 */

void generate_collatz_sequence(int n) {
    printf("%d", n);
    
    while (n != 1) {
        if (n % 2 == 0) {
            n = n / 2;  // If even, divide by 2
        } else {
            n = 3 * n + 1;  // If odd, multiply by 3 and add 1
        }
        printf(", %d", n);
    }
    printf("\n");
}

int main(int argc, char *argv[]) {
    // Error checking: ensure correct number of arguments
    if (argc != 2) {
        fprintf(stderr, "Usage: %s <positive integer>\n", argv[0]);
        return 1;
    }
    
    // Convert argument to integer
    int n = atoi(argv[1]);
    
    // Validate that n is a positive integer
    if (n <= 0) {
        fprintf(stderr, "Error: Please provide a positive integer\n");
        return 1;
    }
    
    printf("Starting number: %d\n", n);
    printf("Collatz sequence: ");
    
    // Fork a child process
    pid_t pid = fork();
    
    if (pid < 0) {
        // Fork failed
        fprintf(stderr, "Fork failed\n");
        return 1;
    }
    else if (pid == 0) {
        // Child process
        generate_collatz_sequence(n);
        exit(0);  // Child exits after completing sequence
    }
    else {
        // Parent process
        // Wait for child to complete
        wait(NULL);
        printf("Child process completed successfully\n");
    }
    
    return 0;
}