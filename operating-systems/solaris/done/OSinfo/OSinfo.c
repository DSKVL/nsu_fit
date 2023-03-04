#include <stdio.h>
#include <getopt.h>
#include <unistd.h>
#include <sys/types.h>
#include <ulimit.h>
#include <stdlib.h>
#include <sys/resource.h>


extern char *optarg;

int main(int argc, char*argv[], char* envp[]) {
    struct option options[] = {
        {"uids", no_argument, 0, 'i'},
        {"setpgid", no_argument, 0, 's'},
	{"pids", no_argument, 0, 'p'},
	{"ulimit", no_argument, 0, 'u'},
	{"newUlimit", required_argument,0, 'U'},
        {"corefilesize", no_argument, 0, 'c'},
	{"newCorefilesize", required_argument, 0, 'C'},
	{"workingDirectory", no_argument, 0, 'd'},
	{"environmentVaribles", no_argument, 0, 'v'},
	{"newVariable", required_argument, 0, 'V'}
    };
    int optionIndex = 0;
    char c;
    while ((c = getopt_long(argc,argv,"ispuU:cC:dvV:",
				    options,&optionIndex)) != -1) {
	switch(c) {
	    case 'i': 
		    printf("uid: %d\neuid: %d\ngid: %d\negid: %d\n",
				    getuid(), geteuid(), getgid(), getegid());
		    break;
	    case 's':
                    setpgid(getpid(), getpid()); 
		    break;
	    case 'p':
		    printf("pid: %d\nppid: %d\npgid: %d\n",
				    getpid(), getppid(), getpgid(getpid()));
		    break;
	    case 'u':
		    printf("ulimit: %ld\n", ulimit(UL_GETFSIZE, 0));
		    break;
	    case 'U':
	            ulimit(UL_SETFSIZE, atol(optarg));
		    break;
            case 'c':{
	            struct rlimit lim;
	            getrlimit(RLIMIT_CORE, &lim); 
                    printf("core file size: %ld\n", lim.rlim_cur);          
		    break;}
            case 'C':{
	            struct rlimit lim;
		    getrlimit(RLIMIT_CORE, &lim); 
		    int newlim = atol(optarg);
		    if (newlim < lim.rlim_max && newlim > 0) {
  			lim.rlim_cur = newlim;
		        setrlimit(RLIMIT_CORE, &lim);
		    } else {
		        printf("invalid new limit\n"); 
		    }
		    break;}
	    case 'd':{
	            char buf[1024];
		    getcwd(buf, 1024);
		    printf("%s\n", buf);
		    break;}
	    case 'v':
                    for (int i = 0; envp[i] != NULL; i++){    
                        printf("%s\n", envp[i]);
                    }
		    break;
            case 'V':
		    putenv(optarg);
		    break;
	}
    }
}
