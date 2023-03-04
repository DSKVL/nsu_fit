#ifndef PRISONERSDILEMMA_FILEMANAGER_H
#define PRISONERSDILEMMA_FILEMANAGER_H

#include <string>

class PayoffsRule;

class FileManager {
public:
    FileManager() = default;
    static PayoffsRule parsePayoffsFile(std::string);
    static void registerMetastrategies(std::string);
};


#endif //PRISONERSDILEMMA_FILEMANAGER_H
