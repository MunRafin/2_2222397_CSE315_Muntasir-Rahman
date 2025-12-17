# Makefile for MD5 Hash Calculator

CC = gcc
CFLAGS = -Wall -Wextra -pthread -O2
LIBS = -lssl -lcrypto -lpthread
TARGET = md5hash
SOURCE = md5hash.c

all: $(TARGET)

$(TARGET): $(SOURCE)
	$(CC) $(CFLAGS) -o $(TARGET) $(SOURCE) $(LIBS)

clean:
	rm -f $(TARGET) *.o

test: $(TARGET)
	./$(TARGET) .

run: $(TARGET)
	./$(TARGET) .

help:
	@echo "Available targets:"
	@echo "  make          - Compile the program"
	@echo "  make clean    - Remove compiled files"
	@echo "  make test     - Compile and run on current directory"
	@echo "  make run      - Run on current directory"
	@echo "  make help     - Show this help message"

.PHONY: all clean test run help
