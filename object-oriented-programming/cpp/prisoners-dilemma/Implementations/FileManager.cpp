#include "../Headers/FileManager.h"

#include <regex>
#include <fstream>
#include <filesystem>
#include <iostream>

#include "../Headers/Factory.h"
#include "../Headers/PayoffsRule.h"
#include "../Headers/Metastrategy.h"


PayoffsRule FileManager::parsePayoffsFile(std::string path) {
    if (path.empty()) {
        return *(new PayoffsRule());
    } else {
        std::ifstream matrix_file;
        matrix_file.exceptions(std::ifstream::failbit | std::ifstream::badbit );

        int payoffs[2][2][2][3];
        std::regex payoff_table_regex(R"((C|D)  (C|D)  (C|D)  =>  (\d+)  (\d+)  (\d+))");
        std::string line;
        try {
            matrix_file.open(path);
            while(matrix_file.good()) {
                std::getline(matrix_file, line);
                std::smatch match;
                if (std::regex_search(line, match,payoff_table_regex)) {
                    int resp1 = match.str(1) == "C" ? 1 : 0;
                    int resp2 = match.str(2) == "C" ? 1 : 0;
                    int resp3 = match.str(3) == "C" ? 1 : 0;
                    payoffs[resp1][resp2][resp3][0] = std::stoi(match.str(4));
                    payoffs[resp1][resp2][resp3][1] = std::stoi(match.str(5));
                    payoffs[resp1][resp2][resp3][2] = std::stoi(match.str(6));
                } else {
                    throw bad_payoffs_file_format("Bad format");
                }
            }
        } catch (std::ifstream::failure &e) {
            if (matrix_file.eof()) {
                PayoffsRule res(payoffs);
                return res;
            }
            std::cout << e.what() << "Bad matrix file\n";
        }
    }
}

std::unique_ptr<Strategy> CreateMetastr(std::vector<std::string> s,
                                        MetaStrategyMode m, std::vector<size_t> o, std::string n){
    std::vector<std::unique_ptr<Strategy> > substrat;
    for (const auto& name : s) {
        substrat.push_back(Factory<std::string, Strategy>::getInstance()->createProductById(name));
    }
    return std::make_unique<Metastrategy>(substrat, m, o, n);
}

bool is_number(const std::string& s)
{
    return !s.empty() && std::find_if(s.begin(),
                                      s.end(), [](unsigned char c) { return !std::isdigit(c); }) == s.end();
}

void FileManager::registerMetastrategies(std::string dir) {
    if (!dir.empty()) {
        std::filesystem::path path{dir};
        if (!std::filesystem::is_directory(path)) {
            throw bad_configs_exception("--configs= argument is not a directory");
        }
        for (auto const &dir_entry: std::filesystem::directory_iterator{path}) {
            if (dir_entry.is_regular_file()) {
                std::ifstream cfg;
                cfg.exceptions(std::ifstream::failbit | std::ifstream::badbit);
                try {
                    cfg.open(dir_entry.path());

                    std::string name;
                    std::getline(cfg, name);

                    std::string mode_string;
                    std::getline(cfg, mode_string);

                    MetaStrategyMode m;

                    if (mode_string == "P") {
                        m = MetaStrategyMode::PARALLEL;
                    } else if (mode_string == "L") {
                        m = MetaStrategyMode::LINEAR;
                    } else {
                        throw bad_configs_exception("bad file");
                    }

                    std::regex metastrategy_config_regex(R"(^((\d+) ([a-zA-Z]+))$|^([a-zA-Z]+)$)");
                    std::string input;

                    std::vector<std::string> substrat;
                    std::vector<size_t> strat_opt;

                    std::smatch match;
                    try {
                        while (cfg.good()) {
                            std::getline(cfg, input);
                            if (std::regex_search(input, match, metastrategy_config_regex)) {
                                if (is_number(match.str(2))) {
                                    strat_opt.emplace_back(std::stoi(match.str(2)));
                                    substrat.push_back(match.str(3));

                                } else {
                                    strat_opt.emplace_back(std::numeric_limits<unsigned int>::max());
                                    substrat.push_back(match.str(4));
                                }
                            } else {
                                throw bad_configs_exception("bad format");
                            }
                        }
                    } catch (std::ios_base::failure &e) {
                        if (!cfg.eof()) {
                            std::cout << "Can't read" << dir_entry;
                        }
                    }

                    auto createMS = [substrat, m, strat_opt, name] {
                        return CreateMetastr(substrat, m, strat_opt, name);
                    };

                    Factory<std::string, Strategy>::getInstance()->registerCreator(name, createMS);
                } catch (std::ifstream::failure &e) {
                    std::cout << "Can't open " << dir_entry;
                }
            }
        }
    }
}