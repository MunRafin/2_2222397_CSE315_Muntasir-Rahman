/*
 * simple_module.c - Linux Kernel Module
 * Demonstrates kernel module basics with GOLDEN_RATIO_PRIME and GCD
 */

#include <linux/init.h>
#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/hash.h>
#include <linux/gcd.h>

/* Module metadata */
MODULE_LICENSE("GPL");
MODULE_AUTHOR("Your Name");
MODULE_DESCRIPTION("Simple Linux Kernel Module - OS Project");
MODULE_VERSION("1.0");

/*
 * Module initialization function
 * Called when module is loaded with insmod
 */
static int __init simple_module_init(void)
{
    printk(KERN_INFO "==============================================\n");
    printk(KERN_INFO "Loading Simple Kernel Module\n");
    printk(KERN_INFO "==============================================\n");
    
    /* Print GOLDEN_RATIO_PRIME from <linux/hash.h> */
    printk(KERN_INFO "GOLDEN_RATIO_PRIME = %lu\n", GOLDEN_RATIO_PRIME);
    
    printk(KERN_INFO "Module loaded successfully!\n");
    printk(KERN_INFO "==============================================\n");
    
    return 0;  /* 0 = success, non-zero = failure */
}

/*
 * Module cleanup function
 * Called when module is removed with rmmod
 */
static void __exit simple_module_exit(void)
{
    unsigned long result;
    
    printk(KERN_INFO "==============================================\n");
    printk(KERN_INFO "Removing Simple Kernel Module\n");
    printk(KERN_INFO "==============================================\n");
    
    /* Calculate GCD of 3700 and 24 using <linux/gcd.h> */
    result = gcd(3700, 24);
    printk(KERN_INFO "GCD(3700, 24) = %lu\n", result);
    
    printk(KERN_INFO "Module removed successfully!\n");
    printk(KERN_INFO "==============================================\n");
}

/* Register module entry and exit points */
module_init(simple_module_init);
module_exit(simple_module_exit);