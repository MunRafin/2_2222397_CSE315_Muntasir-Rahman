# Makefile for Linux Kernel Module

# Name of the kernel module (without .ko extension)
obj-m += simple_module.o

# Kernel build directory
KERNELDIR := /lib/modules/$(shell uname -r)/build

# Current directory
PWD := $(shell pwd)

# Default target: build the module
all:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) modules

# Clean target: remove generated files
clean:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) clean 

# Load the module
load:
	sudo insmod simple_module.ko
	sudo dmesg | tail -20

# Unload the module
unload:
	sudo rmmod simple_module
	sudo dmesg | tail -20

# View kernel log
log:
	sudo dmesg | tail -30

# Complete test: clean, build, load, unload
test: clean all
	@echo ""
	@echo "=== Loading module ==="
	sudo insmod simple_module.ko
	sudo dmesg | tail -10
	@echo ""
	@echo "Press Enter to unload module..."
	@read dummy
	@echo ""
	@echo "=== Unloading module ==="
	sudo rmmod simple_module
	sudo dmesg | tail -10

# Help
help:
	@echo "Available targets:"
	@echo "  make          - Build the kernel module"
	@echo "  make clean    - Remove built files"
	@echo "  make load     - Load module into kernel"
	@echo "  make unload   - Remove module from kernel"
	@echo "  make log      - View recent kernel messages"
	@echo "  make test     - Complete test cycle"
	@echo "  make help     - Show this help"

.PHONY: all clean load unload log test help